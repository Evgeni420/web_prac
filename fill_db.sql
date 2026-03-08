-- 
BEGIN;


INSERT INTO companies (name) VALUES ('Автотранс') RETURNING id;

DO $$
DECLARE
    v_company_id INTEGER;
    v_route1_id INTEGER;
    v_route2_id INTEGER;
    v_route3_id INTEGER;
    v_route4_id INTEGER;
    v_stop_ids INTEGER[];
BEGIN
    SELECT id INTO v_company_id FROM companies WHERE name = 'Автотранс' LIMIT 1;

    INSERT INTO routes (company_id, route_number, route_description, bus_capacity, departure_times)
    VALUES
        (v_company_id, '001', 'Москва – Санкт-Петербург, каждый день, каждые 4 часа с 8:00 до 20:00', 50, ARRAY['08:00','12:00','16:00','20:00'])
    RETURNING id INTO v_route1_id;

    INSERT INTO routes (company_id, route_number, route_description, bus_capacity, departure_times)
    VALUES
        (v_company_id, '002', 'Санкт-Петербург – Москва, каждый день', 50, ARRAY['09:00','13:00','17:00','21:00'])
    RETURNING id INTO v_route2_id;

    INSERT INTO routes (company_id, route_number, route_description, bus_capacity, departure_times)
    VALUES
        (v_company_id, '003', 'Москва – Казань, каждый день, каждые 4 часа с 7:00 до 19:00', 45, ARRAY['07:00','11:00','15:00','19:00'])
    RETURNING id INTO v_route3_id;

    INSERT INTO routes (company_id, route_number, route_description, bus_capacity, departure_times)
    VALUES
        (v_company_id, '004', 'Казань – Москва, каждый день', 45, ARRAY['08:30','12:30','16:30','20:30'])
    RETURNING id INTO v_route4_id;


    INSERT INTO route_stops (route_id, stop_index, stop_name, offset_minutes) VALUES
        (v_route1_id, 0, 'Москва (автовокзал)', 0),
        (v_route1_id, 1, 'Тверь', 180),
        (v_route1_id, 2, 'Санкт-Петербург', 420);

    INSERT INTO route_stops (route_id, stop_index, stop_name, offset_minutes) VALUES
        (v_route2_id, 0, 'Санкт-Петербург', 0),
        (v_route2_id, 1, 'Тверь', 240),
        (v_route2_id, 2, 'Москва (автовокзал)', 420);

    INSERT INTO route_stops (route_id, stop_index, stop_name, offset_minutes) VALUES
        (v_route3_id, 0, 'Москва (автовокзал)', 0),
        (v_route3_id, 1, 'Владимир', 120),
        (v_route3_id, 2, 'Нижний Новгород', 300),
        (v_route3_id, 3, 'Казань', 600);

    INSERT INTO route_stops (route_id, stop_index, stop_name, offset_minutes) VALUES
        (v_route4_id, 0, 'Казань', 0),
        (v_route4_id, 1, 'Нижний Новгород', 300),
        (v_route4_id, 2, 'Владимир', 420),
        (v_route4_id, 3, 'Москва (автовокзал)', 600);


    INSERT INTO fares (route_id, from_stop_id, to_stop_id, price, travel_time_minutes)
    SELECT v_route1_id, s1.id, s2.id, 1000.00, 420
    FROM route_stops s1, route_stops s2
    WHERE s1.route_id = v_route1_id AND s2.route_id = v_route1_id
      AND s1.stop_index = 0 AND s2.stop_index = 2;

    INSERT INTO fares (route_id, from_stop_id, to_stop_id, price, travel_time_minutes)
    SELECT v_route1_id, s1.id, s2.id, 350.00, 180
    FROM route_stops s1, route_stops s2
    WHERE s1.route_id = v_route1_id AND s2.route_id = v_route1_id
      AND s1.stop_index = 0 AND s2.stop_index = 1;

    INSERT INTO fares (route_id, from_stop_id, to_stop_id, price, travel_time_minutes)
    SELECT v_route1_id, s1.id, s2.id, 650.00, 240
    FROM route_stops s1, route_stops s2
    WHERE s1.route_id = v_route1_id AND s2.route_id = v_route1_id
      AND s1.stop_index = 1 AND s2.stop_index = 2;


    FOR i IN 1..50 LOOP
        INSERT INTO seats (route_id, seat_number, seat_type) VALUES
            (v_route1_id, 'A' || i, 'standard'),
            (v_route2_id, 'A' || i, 'standard');
    END LOOP;
    FOR i IN 1..45 LOOP
        INSERT INTO seats (route_id, seat_number, seat_type) VALUES
            (v_route3_id, 'B' || i, 'standard'),
            (v_route4_id, 'B' || i, 'standard');
    END LOOP;

END;
$$;

SELECT generate_trips_for_week('2026-03-09', '2026-03-15');


COMMIT;



-- 
BEGIN;


DO $$
DECLARE
    v_company_id INTEGER;
    v_route_id INTEGER;
BEGIN
    SELECT id INTO v_company_id FROM companies WHERE name = 'Kazan';
    IF v_company_id IS NULL THEN
        INSERT INTO companies (name) VALUES ('Kazan') RETURNING id INTO v_company_id;
    END IF;


    IF NOT EXISTS (SELECT 1 FROM routes WHERE company_id = v_company_id AND route_number = '715') THEN
        INSERT INTO routes (company_id, route_number, route_description, bus_capacity, departure_times)
        VALUES (v_company_id, '715', 'Санкт-Петербург – Казань, каждый день в 8 14 20', 40, ARRAY['08:00','14:00','20:00'])
        RETURNING id INTO v_route_id;

        INSERT INTO route_stops (route_id, stop_index, stop_name, offset_minutes) VALUES
            (v_route_id, 0, 'Санкт-Петербург', 0),
            (v_route_id, 1, 'Тверь', 240),
            (v_route_id, 2, 'Москва (автовокзал)', 420),
            (v_route_id, 3, 'Владимир', 540),
            (v_route_id, 4, 'Нижний Новгород', 840),
            (v_route_id, 5, 'Казань', 1220);

        WITH stops AS (
            SELECT id, stop_index FROM route_stops WHERE route_id = v_route_id
        )
        INSERT INTO fares (route_id, from_stop_id, to_stop_id, price, travel_time_minutes)
        SELECT v_route_id,
               (SELECT id FROM stops WHERE stop_index = 0),
               (SELECT id FROM stops WHERE stop_index = 2),
               2500.00, 1020
        UNION ALL
        SELECT v_route_id,
               (SELECT id FROM stops WHERE stop_index = 0),
               (SELECT id FROM stops WHERE stop_index = 1),
               1200.00, 420
        UNION ALL
        SELECT v_route_id,
               (SELECT id FROM stops WHERE stop_index = 1),
               (SELECT id FROM stops WHERE stop_index = 2),
               1300.00, 600;

        FOR i IN 1..40 LOOP
            INSERT INTO seats (route_id, seat_number, seat_type)
            VALUES (v_route_id, 'C' || i, 'standard');
        END LOOP;

    END IF;
END;
$$;

SELECT generate_trips_for_week('2026-03-09', '2026-03-15');


INSERT INTO clients (full_name, address, phone, email)
SELECT * FROM (VALUES
    ('Иванов Иван Иванович', 'г. Москва, ул. Ленина, д.1', '+7(999)111-11-11', 'ivanov@example.ru'),
    ('Петров Петр Петрович', 'г. Санкт-Петербург, Невский пр., д.2', '+7(999)222-22-22', 'petrov@example.ru'),
    ('Сидорова Анна Сергеевна', 'г. Казань, ул. Баумана, д.3', '+7(999)333-33-33', 'sidorova@example.ru'),
    ('Козлов Дмитрий Алексеевич', 'г. Тверь, пр. Победы, д.4', '+7(999)444-44-44', 'kozlov@example.ru'),
    ('Михайлова Елена Викторовна', 'г. Москва, ул. Тверская, д.5', '+7(999)555-55-55', 'mikhailova@example.ru'),
    ('Николаев Алексей Сергеевич', 'г. Санкт-Петербург, ул. Садовая, д.6', '+7(999)666-66-66', 'nikolaev@example.ru')
) AS v(full_name, address, phone, email)
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE email = v.email);


DO $$
DECLARE
    v_client_ids INTEGER[];
    v_trip1 INTEGER;
    v_trip2 INTEGER;
    v_trip3 INTEGER;
    v_trip4 INTEGER;
    v_trip5 INTEGER;
    v_route1_id INTEGER;
    v_route2_id INTEGER;
    v_route3_id INTEGER;
    v_route4_id INTEGER;
    v_route5_id INTEGER;
    v_stop_from INTEGER;
    v_stop_to INTEGER;
BEGIN
    SELECT array_agg(id ORDER BY email) INTO v_client_ids FROM clients WHERE email IN (
        'ivanov@example.com',
        'petrov@example.com',
        'sidorova@example.com',
        'kozlov@example.com',
        'mikhailova@example.com',
        'nikolaev@example.com'
    );

    SELECT id INTO v_route1_id FROM routes WHERE route_number = '001' AND company_id = (SELECT id FROM companies WHERE name = 'ООО "Автотранс"');
    SELECT id INTO v_route2_id FROM routes WHERE route_number = '002' AND company_id = (SELECT id FROM companies WHERE name = 'ООО "Автотранс"');
    SELECT id INTO v_route3_id FROM routes WHERE route_number = '003' AND company_id = (SELECT id FROM companies WHERE name = 'ООО "Автотранс"');
    SELECT id INTO v_route4_id FROM routes WHERE route_number = '004' AND company_id = (SELECT id FROM companies WHERE name = 'ООО "Автотранс"');
    SELECT id INTO v_route5_id FROM routes WHERE route_number = '715' AND company_id = (SELECT id FROM companies WHERE name = 'ООО "Скоростные линии"');

    SELECT id INTO v_trip1 FROM trips WHERE route_id = v_route1_id AND scheduled_departure = '2026-03-09 08:00:00+00';
    SELECT id INTO v_trip2 FROM trips WHERE route_id = v_route2_id AND scheduled_departure = '2026-03-10 09:00:00+00';
    SELECT id INTO v_trip3 FROM trips WHERE route_id = v_route3_id AND scheduled_departure = '2026-03-11 07:00:00+00';
    SELECT id INTO v_trip4 FROM trips WHERE route_id = v_route4_id AND scheduled_departure = '2026-03-12 08:30:00+00';
    SELECT id INTO v_trip5 FROM trips WHERE route_id = v_route5_id AND scheduled_departure = '2026-03-13 08:00:00+00';

    IF v_trip1 IS NOT NULL AND array_length(v_client_ids,1) >= 1 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route1_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route1_id AND stop_index = 2;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip1, v_client_ids[1], v_stop_from, v_stop_to, 'A1', 1000.00, 'paid');
    END IF;

    IF v_trip2 IS NOT NULL AND array_length(v_client_ids,1) >= 2 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route2_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route2_id AND stop_index = 2;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip2, v_client_ids[2], v_stop_from, v_stop_to, 'A5', 1000.00, 'booked');
    END IF;

    IF v_trip3 IS NOT NULL AND array_length(v_client_ids,1) >= 3 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route3_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route3_id AND stop_index = 3;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip3, v_client_ids[3], v_stop_from, v_stop_to, 'B10', 2500.00, 'paid');
    END IF;

    IF v_trip4 IS NOT NULL AND array_length(v_client_ids,1) >= 4 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route4_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route4_id AND stop_index = 3;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip4, v_client_ids[4], v_stop_from, v_stop_to, 'B5', 2500.00, 'cancelled');
    END IF;

    IF v_trip5 IS NOT NULL AND array_length(v_client_ids,1) >= 5 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route5_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route5_id AND stop_index = 2;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip5, v_client_ids[5], v_stop_from, v_stop_to, 'C15', 2500.00, 'booked');
    END IF;

    IF v_trip1 IS NOT NULL AND array_length(v_client_ids,1) >= 6 THEN
        SELECT id INTO v_stop_from FROM route_stops WHERE route_id = v_route1_id AND stop_index = 0;
        SELECT id INTO v_stop_to FROM route_stops WHERE route_id = v_route1_id AND stop_index = 1;
        INSERT INTO bookings (trip_id, client_id, from_stop_id, to_stop_id, seat_number, price, status)
        VALUES (v_trip1, v_client_ids[6], v_stop_from, v_stop_to, 'A2', 350.00, 'booked');
    END IF;

END;
$$;

COMMIT;