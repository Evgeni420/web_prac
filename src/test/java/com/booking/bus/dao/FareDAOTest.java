package com.booking.bus.dao;

import com.booking.bus.entity.*;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.*;

public class FareDAOTest extends BaseTest {

    private FareDAO fareDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;
    private StopDAO stopDAO;

    private Integer routeId;
    private Integer fromStopId;
    private Integer toStopId;

    @BeforeMethod
    public void init() {
        fareDAO = new FareDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);
        stopDAO = new StopDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("FareTest Company");
            session.persist(company);

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("FT1");
            route.setBusCapacity(40);
            session.persist(route);
            routeId = route.getId();

            RouteStop stop1 = new RouteStop();
            stop1.setRoute(route);
            stop1.setStopIndex(0);
            stop1.setStopName("Start");
            stop1.setOffsetMinutes(0);
            session.save(stop1);
            session.flush();
            fromStopId = stop1.getId();

            RouteStop stop2 = new RouteStop();
            stop2.setRoute(route);
            stop2.setStopIndex(1);
            stop2.setStopName("End");
            stop2.setOffsetMinutes(60);
            session.save(stop2);
            session.flush();
            toStopId = stop2.getId();

            for (int i = 1; i <= route.getBusCapacity(); i++) {
                Seat seat = new Seat();
                seat.setRoute(route);
                seat.setSeatNumber(String.valueOf(i));
                seat.setSeatType("standard");
                session.persist(seat);
            }
            session.flush();

            Fare fare = new Fare();
            fare.setRoute(route);
            fare.setFromStop(stop1);
            fare.setToStop(stop2);
            fare.setPrice(BigDecimal.valueOf(25.50));
            fare.setTravelTimeMinutes(60);
            session.persist(fare);
        });
    }

    @Test
    public void testFindByRouteAndStops() {
        Fare fare = fareDAO.findByRouteAndStops(routeId, fromStopId, toStopId);
        assertNotNull(fare);
        assertEquals(0, fare.getPrice().compareTo(BigDecimal.valueOf(25.50)));
    }

    @Test
    public void testFindByRoute() {
        List<Fare> fares = fareDAO.findByRoute(routeId);
        assertEquals(fares.size(), 1);
        assertEquals(0, fares.get(0).getPrice().compareTo(BigDecimal.valueOf(25.50)));
    }

    @Test
    public void testSaveAndFind() {
        inTransaction(session -> {
            Route route = session.get(Route.class, routeId);
            RouteStop stop3 = new RouteStop();
            stop3.setRoute(route);
            stop3.setStopIndex(2);
            stop3.setStopName("Middle");
            stop3.setOffsetMinutes(30);
            session.save(stop3);
            session.flush();

            Fare fare2 = new Fare();
            fare2.setRoute(route);
            fare2.setFromStop(stop3);
            fare2.setToStop(session.get(RouteStop.class, toStopId));
            fare2.setPrice(BigDecimal.valueOf(15.00));
            fare2.setTravelTimeMinutes(30);
            session.persist(fare2);

        });

        List<Fare> fares = fareDAO.findByRoute(routeId);
        assertEquals(fares.size(), 2);
    }

    @Test
    public void testUpdate() {
        Fare fare = fareDAO.findByRouteAndStops(routeId, fromStopId, toStopId);
        fare.setPrice(BigDecimal.valueOf(30.00));
        fareDAO.update(fare);

        Fare updated = fareDAO.findByRouteAndStops(routeId, fromStopId, toStopId);
        assertEquals(0, updated.getPrice().compareTo(BigDecimal.valueOf(30.00)));
    }

    @Test
    public void testDelete() {
        Fare fare = fareDAO.findByRouteAndStops(routeId, fromStopId, toStopId);
        fareDAO.delete(fare);

        Fare deleted = fareDAO.findByRouteAndStops(routeId, fromStopId, toStopId);
        assertNull(deleted);
    }
}