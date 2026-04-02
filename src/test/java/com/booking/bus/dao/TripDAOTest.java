package com.booking.bus.dao;

import com.booking.bus.entity.*;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.testng.Assert.*;

public class TripDAOTest extends BaseTest {

    private TripDAO tripDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;
    private BookingDAO bookingDAO;
    private ClientDAO clientDAO;

    private Integer routeId;
    private Integer tripId;
    private Integer fromStopId;
    private Integer toStopId;

    @BeforeMethod
    public void init() {
        tripDAO = new TripDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);
        bookingDAO = new BookingDAO(sessionFactory);
        clientDAO = new ClientDAO(sessionFactory);
        SeatDAO seatDAO = new SeatDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("TripTest Company");
            session.persist(company);

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("TR1");
            route.setBusCapacity(5);
            route.setDepartureTimes(new String[]{"10:00"});
            session.persist(route);
            routeId = route.getId();

            RouteStop stop1 = new RouteStop();
            stop1.setRoute(route);
            stop1.setStopIndex(0);
            stop1.setStopName("A");
            stop1.setOffsetMinutes(0);
            session.save(stop1);
            session.flush();
            fromStopId = stop1.getId();

            RouteStop stop2 = new RouteStop();
            stop2.setRoute(route);
            stop2.setStopIndex(1);
            stop2.setStopName("B");
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

            Trip trip = new Trip();
            trip.setRoute(route);
            trip.setScheduledDeparture(ZonedDateTime.now().plusDays(1));
            session.persist(trip);
            tripId = trip.getId();

            Client client = new Client();
            client.setFullName("Test Client");
            client.setEmail("test@example.com");
            session.persist(client);

            Booking booking = new Booking();
            booking.setTrip(trip);
            booking.setClient(client);
            booking.setFromStop(stop1);
            booking.setToStop(stop2);
            booking.setSeatNumber("1");
            booking.setPrice(BigDecimal.TEN);
            session.persist(booking);
        });
    }

    @Test
    public void testFindByRouteAndDate() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Trip> trips = tripDAO.findByRouteAndDate(routeId, date);
        assertEquals(trips.size(), 1);
        assertEquals(trips.get(0).getId(), tripId);
    }

    @Test
    public void testFindTripsByDateRange() {
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = ZonedDateTime.now().plusDays(2);
        List<Trip> trips = tripDAO.findTripsByDateRange(start, end);
        assertFalse(trips.isEmpty());
    }

    @Test
    public void testFindUpcomingTrips() {
        List<Trip> trips = tripDAO.findUpcomingTrips(10);
        assertFalse(trips.isEmpty());
    }

    @Test
    public void testGetAvailableSeats() {
        List<String> available = tripDAO.getAvailableSeats(tripId, fromStopId, toStopId);
        assertFalse(available.contains("1"));
        assertTrue(available.contains("2"));
        assertEquals(available.size(), 4);
    }
}