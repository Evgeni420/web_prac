package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.entity.Route;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class RouteDAOTest extends BaseTest {
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;

    @BeforeMethod
    public void init() {
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);
    }

    // default

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
}