package com.booking.bus.dao;

import com.booking.bus.entity.Fare;
import org.hibernate.SessionFactory;

import java.util.List;

public class FareDAO extends BaseDAO<Fare, Integer> {

    public FareDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Fare.class);
    }

    public Fare findByRouteAndStops(Integer routeId, Integer fromStopId, Integer toStopId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Fare f WHERE f.route.id = :routeId " +
                         "AND f.fromStop.id = :fromStopId " +
                         "AND f.toStop.id = :toStopId";
            return session.createQuery(hql, Fare.class)
                    .setParameter("routeId", routeId)
                    .setParameter("fromStopId", fromStopId)
                    .setParameter("toStopId", toStopId)
                    .uniqueResult();
        }
    }

    public List<Fare> findByRoute(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Fare f WHERE f.route.id = :routeId";
            return session.createQuery(hql, Fare.class)
                    .setParameter("routeId", routeId)
                    .list();
        }
    }
}