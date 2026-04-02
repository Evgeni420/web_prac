package com.booking.bus.dao;

import com.booking.bus.entity.*;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.List;

import static org.testng.Assert.*;

public class BookingDAOTest extends BaseTest {
    private BookingDAO bookingDAO;
    private ClientDAO clientDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;
    private SeatDAO seatDAO;

    private Client testClient;
    private Trip testTrip;
    private RouteStop fromStop;
    private RouteStop toStop;
    private Integer testClientId;
    private Integer testTripId;
    private Integer fromStopId;
    private Integer toStopId;
    private Integer routeId;

    @BeforeMethod
    public void init() {
        bookingDAO = new BookingDAO(sessionFactory);
        clientDAO = new ClientDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);
        seatDAO = new SeatDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("Test Company");
            session.persist(company);

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("T1");
            route.setBusCapacity(40);
            session.save(route);
            session.flush();
            routeId = route.getId();

            RouteStop fromStop = new RouteStop();
            fromStop.setRoute(route);
            fromStop.setStopIndex(0);
            fromStop.setStopName("Stop A");
            fromStop.setOffsetMinutes(0);
            session.save(fromStop);
            session.flush();
            fromStopId = fromStop.getId();

            RouteStop toStop = new RouteStop();
            toStop.setRoute(route);
            toStop.setStopIndex(1);
            toStop.setStopName("Stop B");
            toStop.setOffsetMinutes(60);
            session.save(toStop);
            session.flush();
            toStopId = toStop.getId();

            for (int i = 1; i <= route.getBusCapacity(); i++) {
                Seat seat = new Seat();
                seat.setRoute(route);
                seat.setSeatNumber(String.valueOf(i));
                seat.setSeatType("standard");
                session.persist(seat);
            }
            session.flush();

            Trip testTrip = new Trip();
            testTrip.setRoute(route);
            testTrip.setScheduledDeparture(ZonedDateTime.now());
            session.save(testTrip);
            testTripId = testTrip.getId();

            Client testClient = new Client();
            testClient.setFullName("Test Client");
            testClient.setEmail("test@example.com");
            testClient.setPhone("123");
            session.save(testClient);
            session.flush();
            testClientId = testClient.getId();

            Booking booking = new Booking();
            booking.setTrip(testTrip);
            booking.setClient(testClient);
            booking.setFromStop(fromStop);
            booking.setToStop(toStop);
            booking.setSeatNumber("1A");
            booking.setPrice(BigDecimal.TEN);
            booking.setStatus("booked");
            session.persist(booking);
        });
    }

    @Test
    public void testSaveAndFind() {
        Booking saved = inTransactionResult(session -> {
            Trip trip = session.get(Trip.class, testTripId);
            Client client = session.get(Client.class, testClientId);
            RouteStop from = session.get(RouteStop.class, fromStopId);
            RouteStop to = session.get(RouteStop.class, toStopId);

            Booking booking = new Booking();
            booking.setTrip(trip);
            booking.setClient(client);
            booking.setFromStop(from);
            booking.setToStop(to);
            booking.setSeatNumber("1A");
            booking.setPrice(BigDecimal.valueOf(15.50));
            booking.setStatus("booked");

            session.persist(booking);
            return booking;
        });

        assertNotNull(saved.getId());
        assertEquals(saved.getSeatNumber(), "1A");
        assertEquals(0, saved.getPrice().compareTo(BigDecimal.valueOf(15.50)));

        Optional<Booking> found = bookingDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getSeatNumber(), "1A");
    }

    @Test
    public void testFindBookingsByClient() {
        List<Booking> bookings = bookingDAO.findBookingsByClient(testClientId);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getSeatNumber(), "1A");
    }

    @Test
    public void testFindBookingsByTrip() {
        List<Booking> bookings = bookingDAO.findBookingsByTrip(testTripId);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getSeatNumber(), "1A");
    }

    @Test
    public void testFindBookingsByStatus() {
        List<Booking> booked = bookingDAO.findBookingsByStatus(testClientId, "booked");
        assertEquals(booked.size(), 1);
    }

    @Test
    public void testCancelBooking() {
        Booking saved = inTransactionResult(session -> {
            Trip trip = session.get(Trip.class, testTripId);
            Client client = session.get(Client.class, testClientId);
            RouteStop from = session.get(RouteStop.class, fromStopId);
            RouteStop to = session.get(RouteStop.class, toStopId);

            Booking booking = new Booking();
            booking.setTrip(trip);
            booking.setClient(client);
            booking.setFromStop(from);
            booking.setToStop(to);
            booking.setSeatNumber("3A");
            booking.setPrice(BigDecimal.TEN);
            booking.setStatus("booked");
            session.persist(booking);
            return booking;
        });

        bookingDAO.cancelBooking(saved.getId());
        Booking cancelled = bookingDAO.findById(saved.getId()).get();
        assertEquals(cancelled.getStatus(), "cancelled");
    }

    @Test
    public void testIsSeatAvailable() {
        boolean available = bookingDAO.isSeatAvailable(testTripId, "1A", fromStopId, toStopId);
        assertFalse(available);
        boolean available2 = bookingDAO.isSeatAvailable(testTripId, "2A", fromStopId, toStopId);
        assertTrue(available2);
    }

    @Test
    public void testGetFarePrice() {
        Fare fare = inTransactionResult(session -> {
            Fare f = new Fare();
            f.setRoute(session.get(Route.class, routeId));
            f.setFromStop(session.get(RouteStop.class, fromStopId));
            f.setToStop(session.get(RouteStop.class, toStopId));
            f.setPrice(BigDecimal.valueOf(25.50));
            f.setTravelTimeMinutes(60);
            session.persist(f);
            return f;
        });

        BigDecimal price = bookingDAO.getFarePrice(routeId, fromStopId, toStopId);
        assertEquals(0, price.compareTo(BigDecimal.valueOf(25.50)));
    }

    @Test
    public void testGetFarePrice_whenNotFound_shouldReturnNull() {
        BigDecimal price = bookingDAO.getFarePrice(routeId, 999, 998);
        assertNull(price);
    }


    @Test
    public void testIsSeatAvailable_whenStopsNotFound_shouldReturnTrue() {
        boolean available = bookingDAO.isSeatAvailable(testTripId, "1A", 999, 998);
        assertTrue(available);
    }

    @Test
    public void testCancelBooking_whenBookingNotFound_shouldDoNothing() {
        bookingDAO.cancelBooking(999);
    }
}