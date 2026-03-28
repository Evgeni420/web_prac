package com.booking.bus.dao;

import com.booking.bus.entity.Booking;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.List;

public class BookingDAO extends BaseDAO<Booking, Integer> {

    public BookingDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Booking.class);
    }

    public List<Booking> findBookingsByClient(Integer clientId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Booking b WHERE b.client.id = :clientId ORDER BY b.createdAt DESC";
            return session.createQuery(hql, Booking.class)
                    .setParameter("clientId", clientId)
                    .list();
        }
    }

    public List<Booking> findBookingsByTrip(Integer tripId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Booking b WHERE b.trip.id = :tripId AND b.status != 'cancelled'";
            return session.createQuery(hql, Booking.class)
                    .setParameter("tripId", tripId)
                    .list();
        }
    }

    public List<Booking> findBookingsByClientAndStatus(Integer clientId, String status) {
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
        try (var session = sessionFactory.openSession()) {
            String sql = "SELECT seat_segment_conflicts(:tripId, :seatNumber, :fromStopId, :toStopId)";
            var query = session.createNativeQuery(sql);
            query.setParameter("tripId", tripId);
            query.setParameter("seatNumber", seatNumber);
            query.setParameter("fromStopId", fromStopId);
            query.setParameter("toStopId", toStopId);

            Boolean result = (Boolean) query.getSingleResult();
            return !result;
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
}
