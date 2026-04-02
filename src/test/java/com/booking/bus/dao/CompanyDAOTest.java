package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.Trip;
import java.time.ZonedDateTime;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class CompanyDAOTest extends BaseTest {
    private CompanyDAO companyDAO;
    private RouteDAO routeDAO;
    private TripDAO tripDAO;

    @BeforeMethod
    public void init() {
        companyDAO = new CompanyDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        tripDAO = new TripDAO(sessionFactory);
    }

    @Test
    public void testSaveAndFind() {
        Company company = new Company();
        company.setName("Test Company");
        Company saved = inTransactionResult(session -> companyDAO.save(company));

        assertNotNull(saved.getId());
        assertEquals(saved.getName(), "Test Company");

        Optional<Company> found = companyDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), "Test Company");
    }

    @Test
    public void testFindAll() {
        inTransaction(session -> {
            Company c1 = new Company();
            c1.setName("Company A");
            companyDAO.save(c1);
            Company c2 = new Company();
            c2.setName("Company B");
            companyDAO.save(c2);
        });

        List<Company> all = companyDAO.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    public void testUpdate() {
        Company company = new Company();
        company.setName("Old Name");
        Company saved = inTransactionResult(session -> companyDAO.save(company));

        saved.setName("New Name");
        Company updated = inTransactionResult(session -> companyDAO.update(saved));

        assertEquals(updated.getName(), "New Name");

        Optional<Company> found = companyDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), "New Name");
    }

    @Test
    public void testDelete() {
        Company company = new Company();
        company.setName("To Delete");
        Company saved = inTransactionResult(session -> companyDAO.save(company));

        inTransaction(session -> companyDAO.delete(saved));

        Optional<Company> found = companyDAO.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindByName() {
        Company company = new Company();
        company.setName("UniqueName");
        inTransaction(session -> companyDAO.save(company));

        Company found = companyDAO.findByName("UniqueName");
        assertNotNull(found);
        assertEquals(found.getName(), "UniqueName");

        Company notFound = companyDAO.findByName("NonExistent");
        assertNull(notFound);
    }

    @Test
    public void testFindAllWithRoutes() {
        inTransaction(session -> {
            Company c = new Company();
            c.setName("WithRoutes");
            companyDAO.save(c);

            Route r = new Route();
            r.setCompany(c);
            r.setRouteNumber("R1");
            r.setBusCapacity(30);
            routeDAO.save(r);
        });

        List<Company> companies = companyDAO.findAllWithRoutes();
        assertTrue(companies.stream().anyMatch(c -> "WithRoutes".equals(c.getName())));
    }

}
