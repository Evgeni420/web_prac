package com.booking.bus.dao;

import com.booking.bus.entity.RouteStop;
import org.hibernate.SessionFactory;

import java.util.List;

public class StopDAO extends BaseDAO<RouteStop, Integer> {

    public StopDAO(SessionFactory sessionFactory) {
        super(sessionFactory, RouteStop.class);
    }

    public List<RouteStop> findStopsByRoute(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM RouteStop rs WHERE rs.route.id = :routeId ORDER BY rs.stopIndex";
            return session.createQuery(hql, RouteStop.class)
                    .setParameter("routeId", routeId)
                    .list();
        }
    }

    public List<String> findAllUniqueStopNames() {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT DISTINCT rs.stopName FROM RouteStop rs ORDER BY rs.stopName";
            return session.createQuery(hql, String.class).list();
        }
    }

    public List<RouteStop> findStopsByName(String stopName) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM RouteStop rs WHERE rs.stopName LIKE :stopName ORDER BY rs.stopName";
            return session.createQuery(hql, RouteStop.class)
                    .setParameter("stopName", "%" + stopName + "%")
                    .list();
        }
    }

    public List<String> findStopNamesContaining(String term) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT DISTINCT rs.stopName FROM RouteStop rs " +
                         "WHERE LOWER(rs.stopName) LIKE LOWER(:term) " +
                         "ORDER BY rs.stopName";
            return session.createQuery(hql, String.class)
                    .setParameter("term", term.toLowerCase() + "%")
                    .setMaxResults(10)
                    .list();
        }
    }
}
