-- companies = перевозчики
INSERT INTO companies (name, created_at) VALUES
    ('Автобусный парк №1', '2024-01-01 10:00:00+03'),
    ('Городские перевозки', '2024-01-02 11:30:00+03'),
    ('ТрансАвто', '2024-01-03 09:15:00+03'),
    ('Межгород Транс', '2024-01-04 14:20:00+03'),
    ('Экспресс-Сервис', '2024-01-05 16:45:00+03');

-- routes = маршруты
INSERT INTO routes (company_id, route_number, bus_capacity, published, created_at) VALUES
    (1, '101', 40, true, '2024-01-10 08:00:00+03'),
    (1, '102', 35, true, '2024-01-10 08:30:00+03'),
    (2, '205', 50, true, '2024-01-11 09:00:00+03'),
    (2, '210', 45, true, '2024-01-11 09:30:00+03'),
    (3, '301', 30, false, '2024-01-12 10:00:00+03'),
    (3, '302', 40, true, '2024-01-12 10:30:00+03'),
    (4, '401', 55, true, '2024-01-13 11:00:00+03'),
    (4, '402', 50, true, '2024-01-13 11:30:00+03'),
    (5, '501', 45, true, '2024-01-14 12:00:00+03'),
    (5, '502', 40, true, '2024-01-14 12:30:00+03');

-- route_stops = остановки
-- М101
INSERT INTO route_stops (route_id, stop_index, stop_name, stop_datetime) VALUES
    (1, 1, 'Вокзал', '2024-02-01 08:00:00+03'),
    (1, 2, 'Центральный рынок', '2024-02-01 08:15:00+03'),
    (1, 3, 'Площадь Ленина', '2024-02-01 08:30:00+03'),
    (1, 4, 'Парк культуры', '2024-02-01 08:45:00+03'),
    (1, 5, 'Микрорайон Солнечный', '2024-02-01 09:00:00+03');

-- М102
INSERT INTO route_stops (route_id, stop_index, stop_name, stop_datetime) VALUES
    (2, 1, 'Ж/д вокзал', '2024-02-01 09:00:00+03'),
    (2, 2, 'Университет', '2024-02-01 09:20:00+03'),
    (2, 3, 'Больничный комплекс', '2024-02-01 09:40:00+03'),
    (2, 4, 'ТЦ "Мега"', '2024-02-01 10:00:00+03');

-- М205
INSERT INTO route_stops (route_id, stop_index, stop_name, stop_datetime) VALUES
    (3, 1, 'Автовокзал', '2024-02-01 10:00:00+03'),
    (3, 2, 'Гостиница "Центральная"', '2024-02-01 10:20:00+03'),
    (3, 3, 'Парк Победы', '2024-02-01 10:40:00+03'),
    (3, 4, 'Стадион', '2024-02-01 11:00:00+03'),
    (3, 5, 'Поселок Северный', '2024-02-01 11:30:00+03');

-- М302
INSERT INTO route_stops (route_id, stop_index, stop_name, stop_datetime) VALUES
    (6, 1, 'Аэропорт', '2024-02-01 12:00:00+03'),
    (6, 2, 'Гостиница "Аэро"', '2024-02-01 12:15:00+03'),
    (6, 3, 'Бизнес-центр', '2024-02-01 12:30:00+03'),
    (6, 4, 'Ж/д вокзал', '2024-02-01 12:50:00+03');

-- clients = клиенты
INSERT INTO clients (full_name, address, phone, email, created_at) VALUES
    ('Иванов Иван Иванович', 'ул. Ленина, д. 1, кв. 10', '+7 (999) 123-45-67', 'ivanov@email.com', '2024-01-15 10:00:00+03'),
    ('Петрова Мария Сергеевна', 'ул. Гагарина, д. 5, кв. 25', '+7 (999) 234-56-78', 'petrova@email.com', '2024-01-16 11:30:00+03'),
    ('Сидоров Алексей Петрович', 'пр. Мира, д. 10, кв. 42', '+7 (999) 345-67-89', 'sidorov@email.com', '2024-01-17 09:15:00+03'),
    ('Козлова Елена Владимировна', 'ул. Пушкина, д. 15, кв. 7', '+7 (999) 456-78-90', 'kozlova@email.com', '2024-01-18 14:20:00+03'),
    ('Морозов Дмитрий Александрович', 'ул. Лесная, д. 3, кв. 15', '+7 (999) 567-89-01', 'morozov@email.com', '2024-01-19 16:45:00+03'),
    ('Волкова Анна Игоревна', 'пр. Победы, д. 20, кв. 33', '+7 (999) 678-90-12', 'volkova@email.com', '2024-01-20 08:30:00+03'),
    ('Соколов Павел Андреевич', 'ул. Советская, д. 8, кв. 19', '+7 (999) 789-01-23', 'sokolov@email.com', '2024-01-21 10:45:00+03'),
    ('Михайлова Татьяна Николаевна', 'ул. Кирова, д. 12, кв. 5', '+7 (999) 890-12-34', 'mihailova@email.com', '2024-01-22 12:00:00+03'),
    ('Александров Сергей Владимирович', 'пр. Ленинградский, д. 7, кв. 21', '+7 (999) 901-23-45', 'alexandrov@email.com', '2024-01-23 13:30:00+03'),
    ('Новикова Ольга Дмитриевна', 'ул. Московская, д. 4, кв. 12', '+7 (999) 012-34-56', 'novikova@email.com', '2024-01-24 15:15:00+03');

-- seats = места
-- М101
INSERT INTO seats (route_id, seat_number, seat_type, created_at)
SELECT 
    1,
    'A' || LPAD(gs::text, 2, '0'),
    CASE WHEN gs <= 10 THEN 'vip' ELSE 'standard' END,
    '2024-01-25 10:00:00+03'
FROM generate_series(1, 40) AS gs;

-- М102
INSERT INTO seats (route_id, seat_number, seat_type, created_at)
SELECT 
    2,
    'B' || LPAD(gs::text, 2, '0'),
    'standard',
    '2024-01-25 11:00:00+03'
FROM generate_series(1, 35) AS gs;

-- М205
INSERT INTO seats (route_id, seat_number, seat_type, created_at)
SELECT 
    3,
    'C' || LPAD(gs::text, 2, '0'),
    CASE WHEN gs <= 15 THEN 'vip' WHEN gs <= 30 THEN 'standard' ELSE 'economy' END,
    '2024-01-25 12:00:00+03'
FROM generate_series(1, 50) AS gs;

-- М302
INSERT INTO seats (route_id, seat_number, seat_type, created_at)
SELECT 
    6,
    'D' || LPAD(gs::text, 2, '0'),
    'standard',
    '2024-01-25 13:00:00+03'
FROM generate_series(1, 40) AS gs;

-- fares = тарифы
-- М101
INSERT INTO fares (route_id, from_stop_id, to_stop_id, price) VALUES
    (1, 1, 2, 50.00),  -- Вокзал -> Центральный рынок
    (1, 1, 3, 100.00), -- Вокзал -> Площадь Ленина
    (1, 1, 4, 150.00), -- Вокзал -> Парк культуры
    (1, 1, 5, 200.00), -- Вокзал -> Микрорайон Солнечный
    (1, 2, 3, 60.00),  -- Центральный рынок -> Площадь Ленина
    (1, 2, 4, 110.00), -- Центральный рынок -> Парк культуры
    (1, 2, 5, 160.00), -- Центральный рынок -> Микрорайон Солнечный
    (1, 3, 4, 70.00),  -- Площадь Ленина -> Парк культуры
    (1, 3, 5, 120.00), -- Площадь Ленина -> Микрорайон Солнечный
    (1, 4, 5, 80.00);  -- Парк культуры -> Микрорайон Солнечный

-- М202
INSERT INTO fares (route_id, from_stop_id, to_stop_id, price) 
SELECT 
    3,
    f.id,
    t.id,
    (t.stop_index - f.stop_index) * 75.00
FROM route_stops f
CROSS JOIN route_stops t
WHERE f.route_id = 3 
    AND t.route_id = 3 
    AND f.stop_index < t.stop_index;

-- bookings = бронирование
INSERT INTO bookings (route_id, client_id, from_stop_id, to_stop_id, seat_number, price, status, created_at) VALUES
    (1, 1, 1, 3, 'A01', 100.00, 'booked', '2024-01-26 09:00:00+03'),
    (1, 2, 2, 5, 'A05', 160.00, 'paid', '2024-01-26 09:15:00+03'),
    (1, 3, 1, 4, 'A10', 150.00, 'booked', '2024-01-26 09:30:00+03'),
    (1, 4, 3, 5, 'A15', 120.00, 'paid', '2024-01-26 10:00:00+03'),
    (1, 5, 4, 5, NULL, 80.00, 'booked', '2024-01-26 10:30:00+03');

INSERT INTO bookings (route_id, client_id, from_stop_id, to_stop_id, seat_number, price, status, created_at) VALUES
    (3, 6, 11, 15, 'C01', 300.00, 'paid', '2024-01-27 11:00:00+03'),
    (3, 7, 12, 14, 'C10', 150.00, 'booked', '2024-01-27 11:30:00+03'),
    (3, 8, 11, 13, 'C20', 225.00, 'paid', '2024-01-27 12:00:00+03'),
    (3, 9, 13, 15, 'C30', 150.00, 'cancelled', '2024-01-27 12:30:00+03'),
    (3, 10, 14, 15, NULL, 75.00, 'booked', '2024-01-27 13:00:00+03');



-- Проверка
SELECT 'companies' as table_name, COUNT(*) as count FROM companies
UNION ALL
SELECT 'routes', COUNT(*) FROM routes
UNION ALL
SELECT 'route_stops', COUNT(*) FROM route_stops
UNION ALL
SELECT 'clients', COUNT(*) FROM clients
UNION ALL
SELECT 'seats', COUNT(*) FROM seats
UNION ALL
SELECT 'fares', COUNT(*) FROM fares
UNION ALL
SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL
SELECT 'seat_occupancies', COUNT(*) FROM seat_occupancies
ORDER BY table_name;
