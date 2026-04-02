package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.Seat;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class SeatDAOTest extends BaseTest {

    private SeatDAO seatDAO;
    private RouteDAO routeDAO;
    private CompanyDAO companyDAO;

    private Integer routeId;

    @BeforeMethod
    public void init() {
        seatDAO = new SeatDAO(sessionFactory);
        routeDAO = new RouteDAO(sessionFactory);
        companyDAO = new CompanyDAO(sessionFactory);

        inTransaction(session -> {
            Company company = new Company();
            company.setName("SeatTest Company");
            session.save(company);
            session.flush();

            Route route = new Route();
            route.setCompany(company);
            route.setRouteNumber("ST1");
            route.setBusCapacity(10);
            session.save(route);
            session.flush();
            routeId = route.getId();

            for (int i = 1; i <= route.getBusCapacity(); i++) {
                Seat seat = new Seat();
                seat.setRoute(route);
                seat.setSeatNumber(String.valueOf(i));
                seat.setSeatType("standard");
                session.persist(seat);
            }
            session.flush();

        });

    }

    @Test
    public void testFindByRoute() {
        List<Seat> seats = inTransactionResult(session ->
            session.createQuery("FROM Seat s WHERE s.route.id = :routeId ORDER BY s.seatNumber", Seat.class)
                   .setParameter("routeId", routeId)
                   .list()
        );
        assertEquals(seats.size(), 10);
        assertEquals(seats.get(0).getSeatNumber(), "1");
        assertEquals(seats.get(1).getSeatNumber(), "10");
    }

    @Test
    public void testGenerateSeatsForRoute() {
        List<Seat> seats = seatDAO.findByRoute(routeId);
        assertEquals(seats.size(), 10);
    }

    @Test
    public void testSaveAndFind() {
        Route route = routeDAO.findById(routeId).get();
        Seat newSeat = new Seat();
        newSeat.setRoute(route);
        newSeat.setSeatNumber("VIP1");
        newSeat.setSeatType("vip");
        seatDAO.save(newSeat);

        Seat found = seatDAO.findById(newSeat.getId()).get();
        assertEquals(found.getSeatNumber(), "VIP1");
        assertEquals(found.getSeatType(), "vip");
    }

    //
    @Test
    public void testGenerateSeatsForRoute_whenNoSeats_shouldGenerate() {
        inTransaction(session -> {
            session.createQuery("DELETE FROM Seat WHERE route.id = :routeId")
                   .setParameter("routeId", routeId)
                   .executeUpdate();
        });
        seatDAO.generateSeatsForRoute(routeId);
        List<Seat> seats = seatDAO.findByRoute(routeId);
        assertEquals(seats.size(), 10);
    }

    @Test
    public void testGenerateSeatsForRoute_whenSeatsExist_shouldNotGenerateAgain() {
        seatDAO.generateSeatsForRoute(routeId);
        List<Seat> seats = seatDAO.findByRoute(routeId);
        assertEquals(seats.size(), 10);
    }

    @Test
    public void testGenerateSeatsForRoute_whenRouteNotFound_shouldDoNothing() {
        seatDAO.generateSeatsForRoute(999);
    }
}