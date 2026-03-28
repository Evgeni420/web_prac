package com.booking.bus.dao;

import com.booking.bus.entity.Trip;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public class TripDAO extends BaseDAO<Trip, Integer> {

    public TripDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Trip.class);
    }

    public List<Trip> findByRouteAndDate(Integer routeId, LocalDate date) {
        try (var session = sessionFactory.openSession()) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

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
}
