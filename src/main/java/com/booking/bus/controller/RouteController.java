package com.booking.bus.controller;

import com.booking.bus.dao.*;
import com.booking.bus.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/routes")
public class RouteController {

    @Autowired private RouteDAO routeDAO;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private StopDAO stopDAO;
    @Autowired private FareDAO fareDAO;
    @Autowired private SeatDAO seatDAO;
    @Autowired private TripDAO tripDAO;

    @GetMapping
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeDAO.findAll());
        return "routes";
    }

    @GetMapping("/{id}")
    public String routeDetails(@PathVariable Integer id, Model model) {
        Route route = routeDAO.findById(id).orElse(null);
        if (route == null) return "redirect:/routes";
        List<RouteStop> stops = stopDAO.findStopsByRoute(id);
        List<Fare> fares = fareDAO.findByRoute(id);
        model.addAttribute("route", route);
        model.addAttribute("stops", stops);
        model.addAttribute("fares", fares);
        return "routeDetails";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("route", new Route());
        model.addAttribute("companies", companyDAO.findAll());
        model.addAttribute("stopsList", stopDAO.findAllUniqueStopNames());
        return "add-route";
    }

    @PostMapping("/new")
    public String addRoute(@ModelAttribute Route route,
                           @RequestParam("companyId") Integer companyId,
                           @RequestParam("departureTimes") String[] departureTimes,
                           @RequestParam("stopName") String[] stopNames,
                           @RequestParam("stopOffset") Integer[] stopOffsets,
                           Model model) {
        Company company = companyDAO.findById(companyId).orElse(null);
        if (company == null) return "redirect:/routes";
        Route existing = routeDAO.findByCompanyAndNumber(companyId, route.getRouteNumber());
        if (existing != null) {
            model.addAttribute("error", "Маршрут с номером " + route.getRouteNumber() + " уже существует для выбранной компании");
            model.addAttribute("route", route);
            model.addAttribute("companies", companyDAO.findAll());
            model.addAttribute("stopsList", stopDAO.findAllUniqueStopNames());
            return "add-route";
        }
        if (departureTimes == null || departureTimes.length == 0) {
            model.addAttribute("error", "Добавьте хотя бы одно время отправления");
            return "add-route";
        }
        if (stopNames == null || stopNames.length < 2) {
            model.addAttribute("error", "Добавьте хотя бы две остановки");
            return "add-route";
        }
        route.setCompany(company);
        route.setDepartureTimes(departureTimes);
        routeDAO.save(route);

        for (int i = 0; i < stopNames.length; i++) {
            if (stopNames[i] == null || stopNames[i].trim().isEmpty()) continue;
            RouteStop stop = new RouteStop();
            stop.setRoute(route);
            stop.setStopIndex(i);
            stop.setStopName(stopNames[i]);
            stop.setOffsetMinutes(stopOffsets[i]);
            stopDAO.save(stop);
        }

        seatDAO.generateSeatsForRoute(route.getId());
        tripDAO.generateTripsForWeek(LocalDate.now(), LocalDate.now().plusDays(7));
        return "redirect:/routes";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Route route = routeDAO.findById(id).orElse(null);
        if (route == null) return "redirect:/routes";
        model.addAttribute("route", route);
        model.addAttribute("companies", companyDAO.findAll());
        model.addAttribute("departureTimesStr", String.join(",", route.getDepartureTimes()));
        return "editRoute";
    }

    @PostMapping("/{id}/edit")
    public String updateRoute(@PathVariable Integer id,
                              @ModelAttribute Route updatedRoute,
                              @RequestParam("companyId") Integer companyId,
                              @RequestParam("departureTimes") String departureTimesStr) {
        Route route = routeDAO.findById(id).orElse(null);
        if (route == null) return "redirect:/routes";
        Company company = companyDAO.findById(companyId).orElse(null);
        if (company == null) return "redirect:/routes";
        route.setCompany(company);
        route.setRouteNumber(updatedRoute.getRouteNumber());
        route.setRouteDescription(updatedRoute.getRouteDescription());
        route.setBusCapacity(updatedRoute.getBusCapacity());
        route.setPublished(updatedRoute.getPublished());
        route.setDepartureTimes(departureTimesStr.split(","));
        routeDAO.update(route);
        return "redirect:/routes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Integer id) {
        routeDAO.deleteById(id);
        return "redirect:/routes";
    }
}