package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.RouteStop;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class StopDAOTest extends BaseTest {

    private StopDAO stopDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;

    private Integer routeId;

    @BeforeMethod
    public void init() {
        stopDAO = new StopDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("StopTest Company");
            session.save(company);
            session.flush();

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("STOP1");
            route.setBusCapacity(40);
            session.save(route);
            session.flush();
            routeId = route.getId();

            RouteStop stop1 = new RouteStop();
            stop1.setRoute(route);
            stop1.setStopIndex(0);
            stop1.setStopName("Central Station");
            stop1.setOffsetMinutes(0);
            session.save(stop1);
            session.flush();

            RouteStop stop2 = new RouteStop();
            stop2.setRoute(route);
            stop2.setStopIndex(1);
            stop2.setStopName("Airport");
            stop2.setOffsetMinutes(30);
            session.save(stop2);
            session.flush();

            RouteStop stop3 = new RouteStop();
            stop3.setRoute(route);
            stop3.setStopIndex(2);
            stop3.setStopName("Downtown");
            stop3.setOffsetMinutes(45);
            session.save(stop3);
            session.flush();
        });
    }

    @Test
    public void testFindStopsByRoute() {
        List<RouteStop> stops = stopDAO.findStopsByRoute(routeId);
        assertEquals(stops.size(), 3);
        assertEquals(stops.get(0).getStopName(), "Central Station");
        assertEquals(stops.get(2).getStopName(), "Downtown");
    }

    @Test
    public void testFindAllUniqueStopNames() {
        List<String> names = stopDAO.findAllUniqueStopNames();
        assertTrue(names.contains("Central Station"));
        assertTrue(names.contains("Airport"));
        assertTrue(names.contains("Downtown"));
    }

    @Test
    public void testFindStopsByName() {
        List<RouteStop> stops = stopDAO.findStopsByName("Station");
        assertEquals(stops.size(), 1);
        assertEquals(stops.get(0).getStopName(), "Central Station");
    }
}