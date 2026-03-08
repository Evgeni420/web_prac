--
-- SELECT * FROM companies;
--
--
-- SELECT * FROM routes;
--
--
-- SELECT * FROM route_stops;
--
--
-- SELECT * FROM clients;
--
--
-- SELECT * FROM trips;
--
--
-- SELECT * FROM fares;
--
--
-- SELECT * FROM bookings;
--
--
-- SELECT * FROM seat_occupancies;
--


-- 1. Поиск доступных рейсов по маршруту и дате
WITH target_route AS (
    SELECT r.id AS route_id,
           r.route_number,
           r.company_id,
           r.bus_capacity,
           r.departure_times,
           s_from.id AS from_stop_id,
           s_to.id AS to_stop_id,
           s_from.stop_index AS from_idx,
           s_to.stop_index AS to_idx
    FROM routes r
    JOIN route_stops s_from ON r.id = s_from.route_id
    JOIN route_stops s_to ON r.id = s_to.route_id
    WHERE s_from.stop_name = 'Санкт-Петербург'
      AND s_to.stop_name = 'Москва (автовокзал)'
      AND s_from.stop_index < s_to.stop_index
),
available_trips AS (
    SELECT t.id AS trip_id,
           t.scheduled_departure,
           tr.route_number,
           c.name AS company_name,
           tr.bus_capacity,
           -- количество свободных мест
           tr.bus_capacity - COALESCE((
               SELECT COUNT(DISTINCT s.seat_id)
               FROM seat_occupancies s
               JOIN bookings b ON s.booking_id = b.id
               WHERE b.trip_id = t.id
                 AND b.status != 'cancelled'
                 AND s.seat_id IN (SELECT id FROM seats WHERE route_id = tr.route_id)
                 AND NOT (
                     (SELECT stop_index FROM route_stops WHERE id = s.to_stop_id) <= tr.from_idx
                     OR (SELECT stop_index FROM route_stops WHERE id = s.from_stop_id) >= tr.to_idx
                 )
           ), 0) AS free_seats
    FROM trips t
    JOIN target_route tr ON t.route_id = tr.route_id
    JOIN companies c ON tr.company_id = c.id
    WHERE t.scheduled_departure::date = '2026-03-09'
)
SELECT trip_id,
       to_char(scheduled_departure AT TIME ZONE 'UTC', 'DD.MM.YYYY HH24:MI') AS departure_time,
       route_number,
       company_name,
       free_seats
FROM available_trips
WHERE free_seats > 0
ORDER BY scheduled_departure;

-- 2. Детальная информация о конкретном рейсе
SELECT t.id AS trip_id,
       r.route_number,
       r.route_description,
       t.scheduled_departure AS departure_from_first_stop,
       json_agg(
           json_build_object(
               'stop_name', rs.stop_name,
               'offset_minutes', rs.offset_minutes,
               'arrival_time', t.scheduled_departure + (rs.offset_minutes || ' minutes')::interval,
               'stop_index', rs.stop_index
           ) ORDER BY rs.stop_index
       ) AS stops,
       (
           SELECT json_agg(
               json_build_object(
                   'from_stop', (SELECT stop_name FROM route_stops WHERE id = f.from_stop_id),
                   'to_stop', (SELECT stop_name FROM route_stops WHERE id = f.to_stop_id),
                   'price', f.price,
                   'travel_time_minutes', f.travel_time_minutes
               )
           ) FROM fares f WHERE f.route_id = r.id
       ) AS fares
FROM trips t
JOIN routes r ON t.route_id = r.id
JOIN route_stops rs ON r.id = rs.route_id
WHERE t.id = 1
GROUP BY t.id, r.id, r.route_number, r.route_description, t.scheduled_departure;

-- 3. Проверка занятости конкретного места на рейсе
SELECT b.id AS booking_id,
       c.full_name AS client_name,
       from_stop.stop_name AS from_stop,
       to_stop.stop_name AS to_stop,
       b.status,
       b.seat_number
FROM bookings b
JOIN clients c ON b.client_id = c.id
JOIN route_stops from_stop ON b.from_stop_id = from_stop.id
JOIN route_stops to_stop ON b.to_stop_id = to_stop.id
WHERE b.trip_id = 1 AND b.seat_number = 'A1' AND b.status != 'cancelled';

-- 4. Отчёт по продажам за 9-15 марта 2026
SELECT r.route_number,
       r.route_description,
       COUNT(b.id) AS total_bookings,
       SUM(CASE WHEN b.status = 'paid' THEN 1 ELSE 0 END) AS paid_bookings,
       SUM(b.price) AS total_revenue_paid,
       AVG(b.price) AS avg_ticket_price
FROM bookings b
JOIN trips t ON b.trip_id = t.id
JOIN routes r ON t.route_id = r.id
WHERE t.scheduled_departure >= '2026-03-09' AND t.scheduled_departure < '2026-03-16' AND b.status != 'cancelled'
GROUP BY r.id, r.route_number, r.route_description
ORDER BY total_revenue_paid DESC NULLS LAST;

-- 5. Активные бронирования конкретного клиента
SELECT b.id AS booking_number,
       r.route_number,
       r.route_description,
       to_char(t.scheduled_departure, 'DD.MM.YYYY HH24:MI') AS departure,
       from_stop.stop_name AS from_stop,
       to_stop.stop_name AS to_stop,
       b.seat_number,
       b.price,
       b.status,
       b.created_at AS booking_date
FROM bookings b
JOIN trips t ON b.trip_id = t.id
JOIN routes r ON t.route_id = r.id
JOIN route_stops from_stop ON b.from_stop_id = from_stop.id
JOIN route_stops to_stop ON b.to_stop_id = to_stop.id
WHERE b.client_id = 1 AND b.status IN ('booked', 'paid')  -- только активные
ORDER BY t.scheduled_departure;

-- 6. Свободные места на конкретном рейсе для заданного отрезка
WITH target_trip AS (
    SELECT 1 AS trip_id,
           (SELECT id FROM route_stops WHERE route_id = (SELECT route_id FROM trips WHERE id = 1) AND stop_index = 0) AS from_stop_id,
           (SELECT id FROM route_stops WHERE route_id = (SELECT route_id FROM trips WHERE id = 1) AND stop_index = 2) AS to_stop_id
),
occupied_seats AS (
    SELECT DISTINCT so.seat_id
    FROM seat_occupancies so
    JOIN bookings b ON so.booking_id = b.id
    CROSS JOIN target_trip tt
    WHERE b.trip_id = tt.trip_id
      AND b.status != 'cancelled'
      AND NOT (
          (SELECT stop_index FROM route_stops WHERE id = so.to_stop_id) <= (SELECT stop_index FROM route_stops WHERE id = tt.from_stop_id)
          OR (SELECT stop_index FROM route_stops WHERE id = so.from_stop_id) >= (SELECT stop_index FROM route_stops WHERE id = tt.to_stop_id)
      )
)
SELECT s.seat_number,
       s.seat_type,
       CASE WHEN os.seat_id IS NULL THEN 'free' ELSE 'occupied' END AS status
FROM seats s
LEFT JOIN occupied_seats os ON s.id = os.seat_id
WHERE s.route_id = (SELECT route_id FROM trips WHERE id = 1)
ORDER BY s.seat_number;

-- 7. Поиск рейсов по направлению с фильтрацией по цене и времени
WITH target_route AS (
    SELECT r.id AS route_id,
           s_from.id AS from_stop_id,
           s_to.id AS to_stop_id,
           s_from.stop_index AS from_idx,
           s_to.stop_index AS to_idx
    FROM routes r
    JOIN route_stops s_from ON r.id = s_from.route_id
    JOIN route_stops s_to ON r.id = s_to.route_id
    WHERE s_from.stop_name = 'Москва (автовокзал)'
      AND s_to.stop_name = 'Казань'
      AND s_from.stop_index < s_to.stop_index
),
fare_info AS (
    SELECT f.route_id,
           f.price,
           f.travel_time_minutes
    FROM fares f
    JOIN target_route tr ON f.route_id = tr.route_id
    WHERE f.from_stop_id = tr.from_stop_id
      AND f.to_stop_id = tr.to_stop_id
      AND f.price <= 3000
)
SELECT t.id AS trip_id,
       r.route_number,
       r.route_description,
       to_char(t.scheduled_departure, 'DD.MM.YYYY HH24:MI') AS departure_time,
       fi.price,
       fi.travel_time_minutes,
       c.name AS company
FROM trips t
JOIN routes r ON t.route_id = r.id
JOIN fare_info fi ON t.route_id = fi.route_id
JOIN companies c ON r.company_id = c.id
WHERE t.scheduled_departure::date = '2026-03-11'
  AND t.scheduled_departure::time >= '12:00'::time
ORDER BY t.scheduled_departure;

-- 8. Получить все бронирования с конфликтами (не должно быть)
SELECT b.id, b.trip_id, b.seat_number, b.from_stop_id, b.to_stop_id, b.status
FROM bookings b
WHERE b.status != 'cancelled'
  AND EXISTS (
      SELECT 1
      FROM bookings b2
      WHERE b2.trip_id = b.trip_id
        AND b2.id != b.id
        AND b2.status != 'cancelled'
        AND b2.seat_number = b.seat_number
        AND seat_segment_conflicts(b.trip_id, b.seat_number, b.from_stop_id, b.to_stop_id)
  );
