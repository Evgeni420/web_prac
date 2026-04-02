package com.booking.bus.dao;

import com.booking.bus.entity.Route;
import com.booking.bus.entity.RouteStop;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class RouteDAO extends BaseDAO<Route, Integer> {

    public RouteDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Route.class);
    }

    public List<Route> findRoutesByStopsAndDate(String fromStop, String toStop, LocalDate date) {
        try (var session = sessionFactory.openSession()) {
            ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault());

            String hql = "SELECT DISTINCT r FROM Route r " +
                        "WHERE EXISTS (SELECT 1 FROM RouteStop s1 WHERE s1.route = r AND s1.stopName = :fromStop) " +
                        "AND EXISTS (SELECT 1 FROM RouteStop s2 WHERE s2.route = r AND s2.stopName = :toStop) " +
                        "AND (SELECT s1.stopIndex FROM RouteStop s1 WHERE s1.route = r AND s1.stopName = :fromStop) < " +
                        "(SELECT s2.stopIndex FROM RouteStop s2 WHERE s2.route = r AND s2.stopName = :toStop) " +
                        "AND r.published = true " +
                        "AND EXISTS (SELECT t FROM Trip t WHERE t.route = r AND t.scheduledDeparture BETWEEN :start AND :end)";

            return session.createQuery(hql, Route.class)
                    .setParameter("fromStop", fromStop)
                    .setParameter("toStop", toStop)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .list();
        }
    }

    public List<Route> findRoutesByCompany(Integer companyId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Route r LEFT JOIN FETCH r.company WHERE r.company.id = :companyId AND r.published = true";
            return session.createQuery(hql, Route.class)
                    .setParameter("companyId", companyId)
                    .list();
        }
    }

    public List<RouteStop> findStopsByRoute(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM RouteStop rs WHERE rs.route.id = :routeId ORDER BY rs.stopIndex";
            return session.createQuery(hql, RouteStop.class)
                    .setParameter("routeId", routeId)
                    .list();
        }
    }
}
