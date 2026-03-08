-- перевозчики
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- маршруты
CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    route_number TEXT NOT NULL,
    route_description TEXT,
    bus_capacity INTEGER NOT NULL CHECK (bus_capacity > 0),
    departure_times TEXT[] NOT NULL DEFAULT '{}',
    published BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (company_id, route_number)
);

-- остановки
CREATE TABLE route_stops (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_index INTEGER NOT NULL,
    stop_name TEXT NOT NULL,
    offset_minutes INTEGER NOT NULL CHECK (offset_minutes >= 0),
    UNIQUE (route_id, stop_index),
    UNIQUE (route_id, stop_name)
);

-- поездки
CREATE TABLE trips (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    scheduled_departure TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (route_id, scheduled_departure)
);

-- клиенты
CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    address TEXT,
    phone TEXT,
    email TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- цены
CREATE TABLE fares (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    travel_time_minutes INTEGER NOT NULL CHECK (travel_time_minutes > 0),
    CHECK (from_stop_id <> to_stop_id)
);

-- места
CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    seat_number TEXT NOT NULL,
    seat_type TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    UNIQUE (route_id, seat_number)
);

-- бронирование
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    trip_id INTEGER NOT NULL REFERENCES trips(id) ON DELETE RESTRICT,
    client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE RESTRICT,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE RESTRICT,
    seat_number TEXT,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    status TEXT NOT NULL DEFAULT 'booked' CHECK (status IN ('booked', 'paid', 'cancelled')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CHECK (from_stop_id <> to_stop_id)
);

-- занятость
CREATE TABLE seat_occupancies (
    id SERIAL PRIMARY KEY,
    seat_id INTEGER NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    booking_id INTEGER NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    from_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    to_stop_id INTEGER NOT NULL REFERENCES route_stops(id) ON DELETE CASCADE,
    CHECK (from_stop_id <> to_stop_id)
);


-- индексы
    CREATE INDEX idx_route_stops_route ON route_stops(route_id, stop_index);
    CREATE INDEX idx_fares_route ON fares(route_id);
    CREATE INDEX idx_fares_stops ON fares(from_stop_id, to_stop_id);
    CREATE INDEX idx_trips_route_scheduled ON trips(route_id, scheduled_departure);
    CREATE INDEX idx_bookings_trip ON bookings(trip_id);
    CREATE INDEX idx_bookings_client ON bookings(client_id);
    CREATE INDEX idx_bookings_status ON bookings(status);
    CREATE INDEX idx_seat_occ_booking ON seat_occupancies(booking_id);
    CREATE INDEX idx_seat_occ_seat ON seat_occupancies(seat_id);
    CREATE INDEX idx_seat_occ_stops ON seat_occupancies(from_stop_id, to_stop_id);
    CREATE UNIQUE INDEX unique_active_trip_seat ON bookings (trip_id, seat_number) WHERE seat_number IS NOT NULL AND status != 'cancelled';


-- 
CREATE OR REPLACE FUNCTION seat_segment_conflicts(p_trip_id INTEGER, p_seat_number TEXT, p_from_stop_id INTEGER, p_to_stop_id INTEGER)
RETURNS BOOLEAN AS $$
DECLARE
    v_from_idx INTEGER;
    v_to_idx INTEGER;
    v_route_id INTEGER;
    v_seat_id INTEGER;
BEGIN
    SELECT route_id INTO v_route_id FROM trips WHERE id = p_trip_id;

    SELECT stop_index INTO v_from_idx FROM route_stops WHERE id = p_from_stop_id;
    SELECT stop_index INTO v_to_idx FROM route_stops WHERE id = p_to_stop_id;

    SELECT id INTO v_seat_id FROM seats WHERE route_id = v_route_id AND seat_number = p_seat_number;

    RETURN EXISTS (
        SELECT 1
        FROM bookings b
        JOIN seat_occupancies so ON b.id = so.booking_id
        JOIN route_stops rs_from ON so.from_stop_id = rs_from.id
        JOIN route_stops rs_to ON so.to_stop_id = rs_to.id
        WHERE b.trip_id = p_trip_id
          AND b.status != 'cancelled'
          AND so.seat_id = v_seat_id
          AND NOT (rs_to.stop_index <= v_from_idx OR rs_from.stop_index >= v_to_idx)
    );
END;
$$ LANGUAGE plpgsql;

-- 
CREATE OR REPLACE FUNCTION trg_create_occupancy_on_booking()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    v_seat_id INTEGER;
    v_route_id INTEGER;
BEGIN
    IF NEW.seat_number IS NOT NULL THEN
        SELECT route_id INTO v_route_id FROM trips WHERE id = NEW.trip_id;

        SELECT id INTO v_seat_id FROM seats
        WHERE route_id = v_route_id AND seat_number = NEW.seat_number;
        IF v_seat_id IS NULL THEN
            RAISE EXCEPTION 'Seat % not found on route %', NEW.seat_number, NEW.trip_id;
        END IF;

        IF seat_segment_conflicts(NEW.trip_id, NEW.seat_number, NEW.from_stop_id, NEW.to_stop_id) THEN
            RAISE EXCEPTION 'Seat % is already occupied on overlapping segment', NEW.seat_number;
        END IF;

        INSERT INTO seat_occupancies (seat_id, booking_id, from_stop_id, to_stop_id)
        VALUES (v_seat_id, NEW.id, NEW.from_stop_id, NEW.to_stop_id);
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER after_booking_insert
    AFTER INSERT ON bookings
    FOR EACH ROW
    EXECUTE FUNCTION trg_create_occupancy_on_booking();


-- 
CREATE OR REPLACE FUNCTION trg_update_occupancy_on_booking_update()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
  v_seat_id INTEGER;
  v_route_id INTEGER;
BEGIN
  IF (OLD.seat_number IS DISTINCT FROM NEW.seat_number)
     OR (OLD.from_stop_id IS DISTINCT FROM NEW.from_stop_id)
     OR (OLD.to_stop_id IS DISTINCT FROM NEW.to_stop_id)
     OR (OLD.trip_id IS DISTINCT FROM NEW.trip_id)
     OR (OLD.status = 'cancelled' AND NEW.status != 'cancelled') THEN

    DELETE FROM seat_occupancies WHERE booking_id = OLD.id;

    IF NEW.status != 'cancelled' AND NEW.seat_number IS NOT NULL THEN
      SELECT route_id INTO v_route_id FROM trips WHERE id = NEW.trip_id;
      SELECT id INTO v_seat_id FROM seats WHERE route_id = v_route_id AND seat_number = NEW.seat_number;
      IF v_seat_id IS NULL THEN
        RAISE EXCEPTION 'Seat % not found on route %', NEW.seat_number, NEW.trip_id;
      END IF;

      IF seat_segment_conflicts(NEW.trip_id, NEW.seat_number, NEW.from_stop_id, NEW.to_stop_id) THEN
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
EXECUTE FUNCTION trg_update_occupancy_on_booking_update();



-- поездки
CREATE OR REPLACE FUNCTION generate_trips_for_week(p_start_date DATE, p_end_date DATE)
RETURNS VOID AS $$
DECLARE
    r RECORD;
    d DATE;
    t TEXT;
    ts TIMESTAMP WITH TIME ZONE;
BEGIN
    FOR d IN SELECT generate_series(p_start_date, p_end_date, '1 day') LOOP
        FOR r IN SELECT id, unnest(departure_times) AS dep_time FROM routes WHERE array_length(departure_times, 1) > 0 LOOP
            BEGIN
                ts := (d + r.dep_time::time) AT TIME ZONE 'UTC';
                INSERT INTO trips (route_id, scheduled_departure)
                VALUES (r.id, ts)
                ON CONFLICT (route_id, scheduled_departure) DO NOTHING;
            EXCEPTION WHEN OTHERS THEN
                RAISE NOTICE 'Error inserting route % on % %: %', r.id, d, r.dep_time, SQLERRM;
            END;
        END LOOP;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
