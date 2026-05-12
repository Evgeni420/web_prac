package com.booking.bus.controller;

import com.booking.bus.dao.FareDAO;
import com.booking.bus.dao.RouteDAO;
import com.booking.bus.dao.StopDAO;
import com.booking.bus.entity.Fare;
import com.booking.bus.entity.Route;
import com.booking.bus.entity.RouteStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/fares")
public class FareController {

    @Autowired private FareDAO fareDAO;
    @Autowired private RouteDAO routeDAO;
    @Autowired private StopDAO stopDAO;

    @GetMapping("/route/{routeId}")
    public String listFares(@PathVariable Integer routeId, Model model) {
        Route route = routeDAO.findById(routeId).orElse(null);
        if (route == null) return "redirect:/routes";
        List<Fare> fares = fareDAO.findByRoute(routeId);
        List<RouteStop> stops = stopDAO.findStopsByRoute(routeId);
        model.addAttribute("route", route);
        model.addAttribute("fares", fares);
        model.addAttribute("stops", stops);
        return "manageFares";
    }

    @PostMapping("/route/{routeId}/add")
    public String addFare(@PathVariable Integer routeId,
                          @RequestParam Integer fromStopId,
                          @RequestParam Integer toStopId,
                          @RequestParam BigDecimal price,
                          @RequestParam Integer travelTimeMinutes) {
        Route route = routeDAO.findById(routeId).orElse(null);
        RouteStop fromStop = stopDAO.findById(fromStopId).orElse(null);
        RouteStop toStop = stopDAO.findById(toStopId).orElse(null);
        if (route == null || fromStop == null || toStop == null) {
            return "redirect:/fares/route/" + routeId;
        }
        Fare fare = new Fare();
        fare.setRoute(route);
        fare.setFromStop(fromStop);
        fare.setToStop(toStop);
        fare.setPrice(price);
        fare.setTravelTimeMinutes(travelTimeMinutes);
        fareDAO.save(fare);
        return "redirect:/fares/route/" + routeId;
    }

    @PostMapping("/{fareId}/delete")
    public String deleteFare(@PathVariable Integer fareId) {
        Fare fare = fareDAO.findById(fareId).orElse(null);
        if (fare == null) return "redirect:/routes";
        Integer routeId = fare.getRoute().getId();
        fareDAO.deleteById(fareId);
        return "redirect:/fares/route/" + routeId;
    }
}