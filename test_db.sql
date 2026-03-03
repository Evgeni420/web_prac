
-- 1. Показать все активные бронирования с информацией о клиентах
SELECT
    b.id as booking_id,
    c.full_name as client,
    r.route_number,
    rs_from.stop_name as from_stop,
    rs_to.stop_name as to_stop,
    b.seat_number,
    b.price,
    b.status,
    b.created_at
FROM bookings b
JOIN clients c ON b.client_id = c.id
JOIN routes r ON b.route_id = r.id
JOIN route_stops rs_from ON b.from_stop_id = rs_from.id
JOIN route_stops rs_to ON b.to_stop_id = rs_to.id
WHERE b.status != 'cancelled'
ORDER BY b.created_at DESC;

-- 2. Показать занятые места на маршруте 101
SELECT
    s.seat_number,
    s.seat_type,
    rs_from.stop_name as segment_from,
    rs_to.stop_name as segment_to,
    c.full_name as occupied_by,
    b.status
FROM seat_occupancies so
JOIN seats s ON so.seat_id = s.id
JOIN bookings b ON so.booking_id = b.id
JOIN clients c ON b.client_id = c.id
JOIN route_stops rs_from ON so.from_stop_id = rs_from.id
JOIN route_stops rs_to ON so.to_stop_id = rs_to.id
WHERE s.route_id = 1 AND b.status != 'cancelled'
ORDER BY s.seat_number, rs_from.stop_index;

-- 3. Показать свободные места на конкретном сегменте маршрута 101
-- (например, от остановки 1 до остановки 3)
SELECT s.seat_number, s.seat_type
FROM seats s
WHERE s.route_id = 1
    AND NOT EXISTS (
        SELECT 1
        FROM seat_occupancies so
        JOIN bookings b ON so.booking_id = b.id
        JOIN route_stops sf ON so.from_stop_id = sf.id
        JOIN route_stops st ON so.to_stop_id = st.id
        WHERE so.seat_id = s.id
            AND b.status != 'cancelled'
            AND sf.stop_index < 3  -- to_stop_index (остановка 3)
            AND st.stop_index > 1  -- from_stop_index (остановка 1)
    )
ORDER BY s.seat_number;