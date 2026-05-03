package com.booking.bus.service;

import com.booking.bus.dao.RouteDAO;
import com.booking.bus.dao.TripDAO;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.Trip;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchService {
    private final RouteDAO routeDAO;
    private final TripDAO tripDAO;
    private final SessionFactory sessionFactory;

    public SearchService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.routeDAO = new RouteDAO(sessionFactory);
        this.tripDAO = new TripDAO(sessionFactory);
    }

    public List<Map<String, Object>> searchRoutes(String fromStop, String toStop, LocalDate date) {
        List<Route> routes = routeDAO.findRoutesByStopsAndDate(fromStop, toStop, date);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Route route : routes) {
            List<Trip> trips = tripDAO.findByRouteAndDate(route.getId(), date);

            for (Trip trip : trips) {
                Map<String, Object> result = new HashMap<>();
                result.put("route", route);
                result.put("trip", trip);
                result.put("fromStop", fromStop);
                result.put("toStop", toStop);

                // Get price for this segment
                BigDecimal price = getPriceForSegment(route.getId(), fromStop, toStop);
                result.put("price", price != null ? price : BigDecimal.ZERO);

                results.add(result);
            }
        }

        return results;
    }

    private BigDecimal getPriceForSegment(Integer routeId, String fromStop, String toStop) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT f.price FROM Fare f " +
                        "JOIN f.fromStop fs " +
                        "JOIN f.toStop ts " +
                        "WHERE f.route.id = :routeId " +
                        "AND fs.stopName = :fromStop " +
                        "AND ts.stopName = :toStop";

            return session.createQuery(hql, BigDecimal.class)
                    .setParameter("routeId", routeId)
                    .setParameter("fromStop", fromStop)
                    .setParameter("toStop", toStop)
                    .uniqueResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<Map<String, Object>> searchRoutesWithPrice(String fromStop, String toStop, LocalDate date) {
        return searchRoutes(fromStop, toStop, date);
    }
}