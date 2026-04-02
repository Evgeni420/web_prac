package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.entity.Route;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.booking.bus.entity.RouteStop;
import com.booking.bus.entity.Trip;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class RouteDAOTest extends BaseTest {
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;
    private TripDAO tripDAO;
    private SeatDAO seatDAO;

    @BeforeMethod
    public void init() {
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);
        tripDAO = new TripDAO(sessionFactory);
        seatDAO = new SeatDAO(sessionFactory);
    }

    @Test
    public void testSaveAndFind() {
        Company company = new Company();
        company.setName("Route Company");
        inTransaction(session -> companyDAO.save(company));

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("100");
        route.setBusCapacity(50);
        route.setDepartureTimes(new String[]{"08:00", "12:00"});
        route.setPublished(true);

        Route saved = inTransactionResult(session -> routeDAO.save(route));
        assertNotNull(saved.getId());
        assertEquals(saved.getRouteNumber(), "100");

        Optional<Route> found = routeDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getRouteNumber(), "100");
    }

    @Test
    public void testFindAll() {
        Company company = new Company();
        company.setName("Parent Company");
        inTransaction(session -> companyDAO.save(company));

        Route route1 = new Route();
        route1.setCompany(company);
        route1.setRouteNumber("R1");
        route1.setBusCapacity(30);
        inTransaction(session -> routeDAO.save(route1));

        Route route2 = new Route();
        route2.setCompany(company);
        route2.setRouteNumber("R2");
        route2.setBusCapacity(40);
        inTransaction(session -> routeDAO.save(route2));

        List<Route> all = routeDAO.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    public void testUpdate() {
        Company company = new Company();
        company.setName("Update Co");
        inTransaction(session -> companyDAO.save(company));

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("U1");
        route.setBusCapacity(35);
        Route saved = inTransactionResult(session -> routeDAO.save(route));

        saved.setRouteDescription("Updated description");
        saved.setBusCapacity(50);
        Route updated = inTransactionResult(session -> routeDAO.update(saved));

        assertEquals(updated.getRouteDescription(), "Updated description");
        assertEquals(updated.getBusCapacity(), Integer.valueOf(50));

        Optional<Route> found = routeDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getRouteDescription(), "Updated description");
    }

    @Test
    public void testDelete() {
        Company company = new Company();
        company.setName("Delete Co");
        inTransaction(session -> companyDAO.save(company));

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("D1");
        route.setBusCapacity(20);
        Route saved = inTransactionResult(session -> routeDAO.save(route));

        inTransaction(session -> routeDAO.delete(saved));

        Optional<Route> found = routeDAO.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindRoutesByCompany() {
        Company company = new Company();
        company.setName("CompanyForRoutes");
        inTransaction(session -> session.persist(company));

        Route route1 = new Route();
        route1.setCompany(company);
        route1.setRouteNumber("C1");
        route1.setBusCapacity(30);
        routeDAO.save(route1);

        Route route2 = new Route();
        route2.setCompany(company);
        route2.setRouteNumber("C2");
        route2.setBusCapacity(40);
        routeDAO.save(route2);

        List<Route> routes = routeDAO.findRoutesByCompany(company.getId());
        assertEquals(routes.size(), 2);
    }

    @Test
    public void testFindStopsByRoute() {
        Company company = new Company();
        company.setName("StopRouteCo");
        inTransaction(session -> companyDAO.save(company));

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("StopTest");
        route.setBusCapacity(30);
        routeDAO.save(route);

        RouteStop stop1 = new RouteStop();
        stop1.setRoute(route);
        stop1.setStopIndex(0);
        stop1.setStopName("Stop1");
        stop1.setOffsetMinutes(0);
        inTransaction(session -> session.save(stop1));

        RouteStop stop2 = new RouteStop();
        stop2.setRoute(route);
        stop2.setStopIndex(1);
        stop2.setStopName("Stop2");
        stop2.setOffsetMinutes(30);
        inTransaction(session -> session.save(stop2));

        List<RouteStop> stops = routeDAO.findStopsByRoute(route.getId());
        assertEquals(stops.size(), 2);
        assertEquals(stops.get(0).getStopName(), "Stop1");
        assertEquals(stops.get(1).getStopName(), "Stop2");
    }

    @Test
    public void testFindRoutesByStopsAndDate() {
        LocalDate date = LocalDate.now().plusDays(1);
        Company company = new Company();
        company.setName("SearchCo");
        inTransaction(session -> companyDAO.save(company));

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("SearchRoute");
        route.setBusCapacity(40);
        route.setPublished(true);
        routeDAO.save(route);

        RouteStop fromStop = new RouteStop();
        fromStop.setRoute(route);
        fromStop.setStopIndex(0);
        fromStop.setStopName("StartPoint");
        fromStop.setOffsetMinutes(0);
        inTransaction(session -> session.save(fromStop));

        RouteStop toStop = new RouteStop();
        toStop.setRoute(route);
        toStop.setStopIndex(1);
        toStop.setStopName("EndPoint");
        toStop.setOffsetMinutes(60);
        inTransaction(session -> session.save(toStop));

        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setScheduledDeparture(ZonedDateTime.now().plusDays(1).withHour(10));
        tripDAO.save(trip);

        List<Route> found = routeDAO.findRoutesByStopsAndDate("StartPoint", "EndPoint", date);
        assertEquals(found.size(), 1);
        assertEquals(found.get(0).getId(), route.getId());
    }
}