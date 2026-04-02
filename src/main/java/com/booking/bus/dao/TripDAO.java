package com.booking.bus.dao;

import com.booking.bus.entity.Trip;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TripDAO extends BaseDAO<Trip, Integer> {

    public TripDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Trip.class);
    }

    public List<Trip> findByRouteAndDate(Integer routeId, LocalDate date) {
        try (var session = sessionFactory.openSession()) {
            ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault());

            String hql = "FROM Trip t WHERE t.route.id = :routeId " +
                        "AND t.scheduledDeparture BETWEEN :startDate AND :endDate " +
                        "ORDER BY t.scheduledDeparture";

            return session.createQuery(hql, Trip.class)
                    .setParameter("routeId", routeId)
                    .setParameter("startDate", startOfDay)
                    .setParameter("endDate", endOfDay)
                    .list();
        }
    }

    public List<Trip> findTripsByDateRange(ZonedDateTime start, ZonedDateTime end) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Trip t WHERE t.scheduledDeparture BETWEEN :start AND :end " +
                        "ORDER BY t.scheduledDeparture";

            return session.createQuery(hql, Trip.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .list();
        }
    }

    public List<Trip> findUpcomingTrips(int limit) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Trip t WHERE t.scheduledDeparture > :now " +
                        "ORDER BY t.scheduledDeparture";

            return session.createQuery(hql, Trip.class)
                    .setParameter("now", ZonedDateTime.now())
                    .setMaxResults(limit)
                    .list();
        }
    }

    public List<String> getAvailableSeats(Integer tripId, Integer fromStopId, Integer toStopId) {
        try (var session = sessionFactory.openSession()) {
            Integer routeId = session.createQuery(
                "SELECT t.route.id FROM Trip t WHERE t.id = :tripId", Integer.class)
                .setParameter("tripId", tripId)
                .uniqueResult();

            List<String> allSeats = session.createQuery(
                "SELECT s.seatNumber FROM Seat s WHERE s.route.id = :routeId", String.class)
                .setParameter("routeId", routeId)
                .list();

            List<String> available = new ArrayList<>();
            BookingDAO bookingDAO = new BookingDAO(sessionFactory);
            for (String seat : allSeats) {
                if (bookingDAO.isSeatAvailable(tripId, seat, fromStopId, toStopId)) {
                    available.add(seat);
                }
            }
            return available;
        }
    }
}
