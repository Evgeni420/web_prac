package com.booking.bus.dao;

import com.booking.bus.entity.*;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.testng.Assert.*;

public class BookingDAOTest extends BaseTest {
    private BookingDAO bookingDAO;
    private ClientDAO clientDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;

    private Client testClient;
    private Trip testTrip;
    private RouteStop fromStop;
    private RouteStop toStop;

    @BeforeMethod
    public void init() {
        bookingDAO = new BookingDAO(sessionFactory);
        clientDAO = new ClientDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("Test Company");
            companyDAO.save(company);

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("T1");
            route.setBusCapacity(40);
            routeDAO.save(route);

            fromStop = new RouteStop();
            fromStop.setRoute(route);
            fromStop.setStopIndex(0);
            fromStop.setStopName("Stop A");
            fromStop.setOffsetMinutes(0);
            session.save(fromStop);

            toStop = new RouteStop();
            toStop.setRoute(route);
            toStop.setStopIndex(1);
            toStop.setStopName("Stop B");
            toStop.setOffsetMinutes(60);
            session.save(toStop);

            testTrip = new Trip();
            testTrip.setRoute(route);
            testTrip.setScheduledDeparture(ZonedDateTime.now());
            session.save(testTrip);

            testClient = new Client();
            testClient.setFullName("Test Client");
            testClient.setEmail("test@example.com");
            testClient.setPhone("123");
            clientDAO.save(testClient);
        });
    }

    @Test
    public void testSaveAndFind() {
        Booking booking = new Booking();
        booking.setTrip(testTrip);
        booking.setClient(testClient);
        booking.setFromStop(fromStop);
        booking.setToStop(toStop);
        booking.setSeatNumber("1A");
        booking.setPrice(BigDecimal.valueOf(15.50));
        booking.setStatus("booked");

        Booking saved = inTransactionResult(session -> bookingDAO.save(booking));

        assertNotNull(saved.getId());
        assertEquals(saved.getSeatNumber(), "1A");
        assertEquals(saved.getPrice(), BigDecimal.valueOf(15.50));

        Optional<Booking> found = bookingDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getSeatNumber(), "1A");
    }

    // findBookingsByClient, findBookingsByTrip, isSeatAvailable, cancelBooking, getFarePrice
}