package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

public class BaseDAOTest extends BaseTest {

    private CompanyDAO companyDAO;

    @BeforeMethod
    public void init() {
        companyDAO = new CompanyDAO(sessionFactory);
    }

    @Test
    public void testSaveAndFindById() {
        Company company = new Company();
        company.setName("Test Company");
        Company saved = companyDAO.save(company);
        assertNotNull(saved.getId());

        Optional<Company> found = companyDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), "Test Company");
    }

    @Test
    public void testFindAll() {
        Company c1 = new Company(); c1.setName("C1");
        Company c2 = new Company(); c2.setName("C2");
        companyDAO.save(c1);
        companyDAO.save(c2);

        assertEquals(companyDAO.findAll().size(), 2);
    }

    @Test
    public void testUpdate() {
        Company company = new Company();
        company.setName("Old Name");
        company = companyDAO.save(company);

        company.setName("New Name");
        companyDAO.update(company);

        Optional<Company> updated = companyDAO.findById(company.getId());
        assertEquals(updated.get().getName(), "New Name");
    }

    @Test
    public void testDelete() {
        Company company = new Company();
        company.setName("To Delete");
        company = companyDAO.save(company);
        Integer id = company.getId();

        companyDAO.delete(company);
        Optional<Company> found = companyDAO.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteById() {
        Company company = new Company();
        company.setName("To Delete By Id");
        company = companyDAO.save(company);
        Integer id = company.getId();

        companyDAO.deleteById(id);
        Optional<Company> found = companyDAO.findById(id);
        assertFalse(found.isPresent());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSave_shouldThrowExceptionWhenConstraintViolation() {
        Company invalid = new Company();
        companyDAO.save(invalid);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testUpdate_shouldThrowExceptionWhenConstraintViolation() {
        Company company = new Company();
        company.setName("Valid");
        company = companyDAO.save(company);
        company.setName(null);
        companyDAO.update(company);
    }
}
