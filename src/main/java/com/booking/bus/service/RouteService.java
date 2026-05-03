package com.booking.bus.service;

import com.booking.bus.dao.RouteDAO;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.RouteStop;
import com.booking.bus.entity.Seat;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.util.List;

public class RouteService {
    private final RouteDAO routeDAO;
    private final SessionFactory sessionFactory;

    public RouteService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.routeDAO = new RouteDAO(sessionFactory);
    }

    public List<Route> getAllRoutes() {
        try (var session = sessionFactory.openSession()) {
            List<Route> routes = session.createQuery(
                "SELECT DISTINCT r FROM Route r " +
                "LEFT JOIN FETCH r.company " +
                "LEFT JOIN FETCH r.stops",
                Route.class
            ).list();

            return routes;
        } catch (Exception e) {
            System.err.println("Error fetching routes: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Route getRouteById(Integer id) {
        try (var session = sessionFactory.openSession()) {
            Route route = session.createQuery(
                "SELECT r FROM Route r " +
                "LEFT JOIN FETCH r.company " +
                "LEFT JOIN FETCH r.stops " +
                "WHERE r.id = :id",
                Route.class
            ).setParameter("id", id).uniqueResult();

            return route;
        }
    }

    public Route createRoute(Route route) {
        if (route.getRouteNumber() == null || route.getRouteNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Route number is required");
        }

        if (route.getBusCapacity() == null || route.getBusCapacity() <= 0) {
            throw new IllegalArgumentException("Bus capacity must be greater than 0");
        }

        if (route.getStops().size() < 2) {
            throw new IllegalArgumentException("Route must have at least 2 stops");
        }

        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            for (int i = 1; i <= route.getBusCapacity(); i++) {
                Seat seat = new Seat();
                seat.setRoute(route);
                seat.setSeatNumber(String.valueOf(i));
                seat.setSeatType("standard");
                route.getSeats().add(seat);
            }
            session.flush();

            session.persist(route);
            transaction.commit();
            return route;
        }
    }

    public Route updateRoute(Route route) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            Route merged = (Route) session.merge(route);
            transaction.commit();
            return merged;
        }
    }

    public void deleteRoute(Integer id) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            Route route = session.get(Route.class, id);
            if (route != null) {
                session.remove(route);
            }
            transaction.commit();
        }
    }

    public List<Route> searchRoutes(String fromStop, String toStop, LocalDate date) {
        return routeDAO.findRoutesByStopsAndDate(fromStop, toStop, date);
    }

    public List<RouteStop> getRouteStops(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM RouteStop rs WHERE rs.route.id = :routeId ORDER BY rs.stopIndex",
                RouteStop.class
            ).setParameter("routeId", routeId).list();
        }
    }
}