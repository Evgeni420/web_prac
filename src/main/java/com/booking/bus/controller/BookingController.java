package com.booking.bus.controller;

import com.booking.bus.dao.*;
import com.booking.bus.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired private TripDAO tripDAO;
    @Autowired private RouteDAO routeDAO;
    @Autowired private ClientDAO clientDAO;
    @Autowired private BookingDAO bookingDAO;
    @Autowired private StopDAO stopDAO;

    @GetMapping("/{tripId}")
    public String showBookingForm(@PathVariable Integer tripId,
                                  @RequestParam("fromStopId") Integer fromStopId,
                                  @RequestParam("toStopId") Integer toStopId,
                                  Model model) {
        Trip trip = tripDAO.findById(tripId).orElse(null);
        if (trip == null) return "redirect:/";

        String formattedDate = trip.getScheduledDeparture().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        RouteStop fromStop = stopDAO.findById(fromStopId).orElse(null);
        RouteStop toStop = stopDAO.findById(toStopId).orElse(null);
        BigDecimal price = bookingDAO.getFarePrice(trip.getRoute().getId(), fromStopId, toStopId);
        List<String> availableSeats = tripDAO.getAvailableSeats(tripId, fromStopId, toStopId);

        model.addAttribute("trip", trip);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("fromStop", fromStop);
        model.addAttribute("toStop", toStop);
        model.addAttribute("price", price);
        model.addAttribute("availableSeats", availableSeats);
        model.addAttribute("booking", new Booking());
        return "booking";
    }

    @GetMapping("/chooseTrip")
public String chooseTrip(@RequestParam Integer routeId,
                         @RequestParam Integer fromStopId,
                         @RequestParam Integer toStopId,
                         @RequestParam String date,
                         Model model) {
    LocalDate selectedDate = LocalDate.parse(date);
    String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    Route route = routeDAO.findById(routeId).orElse(null);
    if (route == null) return "redirect:/";

    List<Trip> trips = tripDAO.findByRouteAndDate(routeId, selectedDate);
    BigDecimal price = bookingDAO.getFarePrice(routeId, fromStopId, toStopId);
    if (price == null) {
        model.addAttribute("error", "Для выбранных остановок не установлена цена.");
        return "bookingError";
    }
    model.addAttribute("route", route);
    model.addAttribute("trips", trips);
    model.addAttribute("price", price);
    model.addAttribute("fromStopId", fromStopId);
    model.addAttribute("toStopId", toStopId);
    model.addAttribute("formattedDate", formattedDate);
    return "choose-trip";
}

    @GetMapping("/prepare")
    public String prepareBooking(@RequestParam Integer routeId,
                                 @RequestParam Integer fromStopId,
                                 @RequestParam Integer toStopId,
                                 Model model) {

        Route route = routeDAO.findById(routeId).orElse(null);
        if (route == null) return "redirect:/";

        List<Trip> trips = tripDAO.findByRouteAndDate(routeId, LocalDate.now());
        if (trips.isEmpty()) {
            model.addAttribute("error", "Нет доступных поездок на ближайшие даты");
            return "bookingError";
        }
        Trip trip = trips.get(0);

        return "redirect:/booking/" + trip.getId() + "?fromStopId=" + fromStopId + "&toStopId=" + toStopId;
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Integer tripId,
                                 @RequestParam Integer fromStopId,
                                 @RequestParam Integer toStopId,
                                 @RequestParam String clientName,
                                 @RequestParam String clientPhone,
                                 @RequestParam String clientEmail,
                                 @RequestParam String seatNumber,
                                 Model model) {
        Client client = clientDAO.findByEmail(clientEmail);
        if (client == null) {
            client = new Client();
            client.setFullName(clientName);
            client.setPhone(clientPhone);
            client.setEmail(clientEmail);
            clientDAO.save(client);
        }
        Trip trip = tripDAO.findById(tripId).orElse(null);
        RouteStop fromStop = stopDAO.findById(fromStopId).orElse(null);
        RouteStop toStop = stopDAO.findById(toStopId).orElse(null);
        BigDecimal price = bookingDAO.getFarePrice(trip.getRoute().getId(), fromStopId, toStopId);

        if (!bookingDAO.isSeatAvailable(tripId, seatNumber, fromStopId, toStopId)) {
            model.addAttribute("error", "Место уже занято на выбранном участке");
            return "bookingError";
        }

        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setClient(client);
        booking.setFromStop(fromStop);
        booking.setToStop(toStop);
        booking.setSeatNumber(seatNumber);
        booking.setPrice(price);
        booking.setStatus("booked");
        try {
            bookingDAO.save(booking);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            String errorMsg = "Ошибка при бронировании. Попробуйте другое место или обратитесь в поддержку.";
            if (cause != null) {
                String msg = cause.getMessage();
                if (msg != null && (msg.contains("already occupied") || msg.contains("Seat"))) {
                    errorMsg = "Место " + seatNumber + " уже занято на выбранном участке. Пожалуйста, выберите другое место.";
                } else if (msg != null && msg.contains("not found on route")) {
                    errorMsg = "Место " + seatNumber + " не существует на данном маршруте.";
                }
            }
            BigDecimal tprice = bookingDAO.getFarePrice(trip.getRoute().getId(), fromStopId, toStopId);
            if (tprice == null) {
                model.addAttribute("error", "Цена для выбранного маршрута не найдена.");
                return "bookingError";
            }
            model.addAttribute("error", errorMsg);
            return "bookingError";
        }
        model.addAttribute("booking", booking);
        model.addAttribute("seatNumber", seatNumber);
        model.addAttribute("trip", trip);
        model.addAttribute("companyName", trip.getRoute().getCompany().getName());
        model.addAttribute("routeNumber", trip.getRoute().getRouteNumber());
        String formattedDeparture = trip.getScheduledDeparture().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        model.addAttribute("formattedDeparture", formattedDeparture);
        return "bookingSuccess";
    }
}