CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    route_number TEXT NOT NULL,
    bus_capacity INTEGER NOT NULL CHECK (bus_capacity > 0),
    published BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (company_id, route_number)
);

CREATE TABLE route_stops (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_index INTEGER NOT NULL, -- порядок следования
    stop_name TEXT NOT NULL,
    stop_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (route_id, stop_index),
    UNIQUE (route_id, stop_name)
);

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    address TEXT,
    phone TEXT,
    email TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE fares (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    CHECK (from_stop_id <> to_stop_id)
);

CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    seat_number TEXT NOT NULL,
    seat_type TEXT, -- например, 'standard', 'vip'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (route_id, seat_number)
);

CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE RESTRICT,
    client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE RESTRICT,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE RESTRICT,
    seat_number TEXT, -- nullable, если свободный выбор
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    status TEXT NOT NULL DEFAULT 'booked', -- booked, paid, cancelled
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CHECK (from_stop_id <> to_stop_id)
);

CREATE UNIQUE INDEX unique_active_booking_seat ON bookings (route_id, seat_number) WHERE (seat_number IS NOT NULL AND status <> 'cancelled');

CREATE TABLE seat_occupancies (
    id SERIAL PRIMARY KEY,
    seat_id INTEGER NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    booking_id INTEGER REFERENCES bookings(id) ON DELETE SET NULL,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CHECK (from_stop_id <> to_stop_id)
);

CREATE INDEX idx_route_stops_route ON route_stops(route_id, stop_index);
CREATE INDEX idx_fares_route ON fares(route_id);
CREATE INDEX idx_bookings_route ON bookings(route_id);
CREATE INDEX idx_bookings_client ON bookings(client_id);
CREATE INDEX idx_seat_occ_seat ON seat_occupancies(seat_id);
CREATE INDEX idx_seat_occ_from_to ON seat_occupancies(from_stop_id, to_stop_id);
CREATE INDEX idx_seat_occ_booking ON seat_occupancies(booking_id);

-- проверка пересечения сегментов

CREATE OR REPLACE FUNCTION seat_segment_conflicts(p_seat_id INTEGER, p_from INTEGER, p_to INTEGER)
RETURNS BOOLEAN LANGUAGE SQL STABLE AS $$
  SELECT EXISTS (
    SELECT 1 FROM seat_occupancies so
    JOIN route_stops sf ON so.from_stop_id = sf.id
    JOIN route_stops st ON so.to_stop_id = st.id
    JOIN route_stops pf ON pf.id = p_from
    JOIN route_stops pt ON pt.id = p_to
    WHERE so.seat_id = p_seat_id
      AND so.from_stop_id <> so.to_stop_id
      -- сравнение по порядку остановок внутри одного рейса:
      AND sf.route_id = pf.route_id AND st.route_id = pf.route_id
      -- сегменты пересекаются, т.е. не (existing.to <= new.from OR existing.from >= new.to)
      AND NOT (st.stop_index <= pf.stop_index OR sf.stop_index >= pt.stop_index)
  );
$$;

-- occupancy при назначенном seat_number при создании брони

CREATE OR REPLACE FUNCTION trg_create_occupancy_on_booking()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
  v_seat_id INTEGER;
BEGIN
  IF NEW.seat_number IS NULL THEN
    RETURN NEW;
  END IF;

  SELECT id INTO v_seat_id FROM seats WHERE route_id = NEW.route_id AND seat_number = NEW.seat_number;
  IF v_seat_id IS NULL THEN
    RAISE EXCEPTION 'Seat % not found on route %', NEW.seat_number, NEW.route_id;
  END IF;

  IF seat_segment_conflicts(v_seat_id, NEW.from_stop_id, NEW.to_stop_id) THEN
    RAISE EXCEPTION 'Seat % is already occupied on overlapping segment', NEW.seat_number;
  END IF;

  INSERT INTO seat_occupancies (seat_id, booking_id, from_stop_id, to_stop_id)
  VALUES (v_seat_id, NEW.id, NEW.from_stop_id, NEW.to_stop_id);

  RETURN NEW;
END;
$$;

-- удаления occupancy при отмене брони

CREATE OR REPLACE FUNCTION trg_delete_occupancy_on_booking_delete()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  DELETE FROM seat_occupancies WHERE booking_id = OLD.id;
  RETURN OLD;
END;
$$;

-- вставка occupancy после создания брони, удаление при удалении брони

CREATE TRIGGER after_booking_insert
AFTER INSERT ON bookings
FOR EACH ROW
EXECUTE PROCEDURE trg_create_occupancy_on_booking();

CREATE TRIGGER after_booking_delete
AFTER DELETE ON bookings
FOR EACH ROW
EXECUTE PROCEDURE trg_delete_occupancy_on_booking_delete();

-- при обновлении брони обновление occupancy:

CREATE OR REPLACE FUNCTION trg_update_occupancy_on_booking_update()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
  v_seat_id INTEGER;
BEGIN
  -- если место или сегмент изменился, пересоздаём occupancy
  IF (OLD.seat_number IS DISTINCT FROM NEW.seat_number)
     OR (OLD.from_stop_id IS DISTINCT FROM NEW.from_stop_id)
     OR (OLD.to_stop_id IS DISTINCT FROM NEW.to_stop_id) THEN

    DELETE FROM seat_occupancies WHERE booking_id = OLD.id;

    IF NEW.seat_number IS NOT NULL THEN
      SELECT id INTO v_seat_id FROM seats WHERE route_id = NEW.route_id AND seat_number = NEW.seat_number;
      IF v_seat_id IS NULL THEN
        RAISE EXCEPTION 'Seat % not found on route %', NEW.seat_number, NEW.route_id;
      END IF;

      IF seat_segment_conflicts(v_seat_id, NEW.from_stop_id, NEW.to_stop_id) THEN
        RAISE EXCEPTION 'Seat % is already occupied on overlapping segment', NEW.seat_number;
      END IF;

      INSERT INTO seat_occupancies (seat_id, booking_id, from_stop_id, to_stop_id)
      VALUES (v_seat_id, NEW.id, NEW.from_stop_id, NEW.to_stop_id);
    END IF;
  END IF;

  RETURN NEW;
END;
$$;

CREATE TRIGGER after_booking_update
AFTER UPDATE ON bookings
FOR EACH ROW
EXECUTE PROCEDURE trg_update_occupancy_on_booking_update();

