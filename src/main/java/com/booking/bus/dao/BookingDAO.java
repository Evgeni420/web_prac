package com.booking.bus.dao;

import com.booking.bus.entity.Booking;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;

public class BookingDAO extends BaseDAO<Booking, Integer> {

    public BookingDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Booking.class);
    }

    public List<Booking> findBookingsByClient(Integer clientId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT DISTINCT b FROM Booking b " +
                         "LEFT JOIN FETCH b.trip t " +
                         "LEFT JOIN FETCH t.route r " +
                         "LEFT JOIN FETCH r.company " +
                         "LEFT JOIN FETCH b.fromStop " +
                         "LEFT JOIN FETCH b.toStop " +
                         "WHERE b.client.id = :clientId " +
                         "ORDER BY b.createdAt DESC";
            return session.createQuery(hql, Booking.class)
                    .setParameter("clientId", clientId)
                    .list();
        }
    }

    public List<Booking> findBookingsByTrip(Integer tripId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT b FROM Booking b " +
                         "LEFT JOIN FETCH b.client " +
                         "LEFT JOIN FETCH b.fromStop " +
                         "LEFT JOIN FETCH b.toStop " +
                         "WHERE b.trip.id = :tripId AND b.status != 'cancelled'";
            return session.createQuery(hql, Booking.class)
                    .setParameter("tripId", tripId)
                    .list();
        }
    }

    public List<Booking> findBookingsByStatus(Integer clientId, String status) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Booking b WHERE b.client.id = :clientId AND b.status = :status";
            return session.createQuery(hql, Booking.class)
                    .setParameter("clientId", clientId)
                    .setParameter("status", status)
                    .list();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isSeatAvailable(Integer tripId, String seatNumber, Integer fromStopId, Integer toStopId) {
        try (Session session = sessionFactory.openSession()) {
            Integer fromIndex = session.createQuery(
                "SELECT rs.stopIndex FROM RouteStop rs WHERE rs.id = :id", Integer.class)
                .setParameter("id", fromStopId)
                .uniqueResult();
            Integer toIndex = session.createQuery(
                "SELECT rs.stopIndex FROM RouteStop rs WHERE rs.id = :id", Integer.class)
                .setParameter("id", toStopId)
                .uniqueResult();

            if (fromIndex == null || toIndex == null) return true; // если остановок нет, место считается доступным

            String hql = "SELECT COUNT(b) FROM Booking b " +
                         "WHERE b.trip.id = :tripId " +
                         "AND b.seatNumber = :seatNumber " +
                         "AND b.status != 'cancelled' " +
                         "AND (b.fromStop.stopIndex < :toIndex AND b.toStop.stopIndex > :fromIndex)";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("tripId", tripId)
                    .setParameter("seatNumber", seatNumber)
                    .setParameter("fromIndex", fromIndex)
                    .setParameter("toIndex", toIndex)
                    .uniqueResult();
            return count == 0;
        }
    }

    public BigDecimal getFarePrice(Integer routeId, Integer fromStopId, Integer toStopId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT f.price FROM Fare f " +
                        "WHERE f.route.id = :routeId " +
                        "AND f.fromStop.id = :fromStopId " +
                        "AND f.toStop.id = :toStopId";

            return session.createQuery(hql, BigDecimal.class)
                    .setParameter("routeId", routeId)
                    .setParameter("fromStopId", fromStopId)
                    .setParameter("toStopId", toStopId)
                    .uniqueResult();
        }
    }

    public void cancelBooking(Integer bookingId) {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            Booking booking = session.get(Booking.class, bookingId);
            if (booking != null && !"cancelled".equals(booking.getStatus())) {
                booking.setStatus("cancelled");
                session.merge(booking);
            }
            tx.commit();
        }
    }
}
