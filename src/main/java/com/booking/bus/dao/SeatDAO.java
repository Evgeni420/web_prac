package com.booking.bus.dao;

import com.booking.bus.entity.Route;
import com.booking.bus.entity.Seat;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class SeatDAO extends BaseDAO<Seat, Integer> {

    public SeatDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Seat.class);
    }

    public List<Seat> findByRoute(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            String hql = "FROM Seat s WHERE s.route.id = :routeId ORDER BY s.seatNumber";
            List<Seat> result = session.createQuery(hql, Seat.class)
                    .setParameter("routeId", routeId)
                    .list();
            tx.commit();
            return result;
        }
    }

    public void generateSeatsForRoute(Integer routeId) {
        try (var session = sessionFactory.openSession()) {
            Route route = session.get(Route.class, routeId);
            if (route == null) return;
            Long count = session.createQuery("select count(s) from Seat s where s.route.id = :routeId", Long.class)
                    .setParameter("routeId", routeId)
                    .uniqueResult();
            if (count > 0) return;

            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= route.getBusCapacity(); i++) {
                Seat seat = new Seat();
                seat.setRoute(route);
                seat.setSeatNumber(String.valueOf(i));
                seat.setSeatType("standard");
                seats.add(seat);
            }
            var tx = session.beginTransaction();
            seats.forEach(session::persist);
            tx.commit();
        }
    }
}