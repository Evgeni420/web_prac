package com.booking.bus.controller;

import com.booking.bus.dao.RouteDAO;
import com.booking.bus.entity.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private RouteDAO routeDAO;

    @PostMapping("/search")
    public String searchResultsPost(@RequestParam String fromStop,
                                    @RequestParam String toStop,
                                    @RequestParam String date,
                                    Model model) {
        LocalDate searchDate = LocalDate.parse(date);
        List<Route> routes = routeDAO.findRoutesByStopsAndDate(fromStop, toStop, searchDate);
        model.addAttribute("routes", routes);
        model.addAttribute("fromStop", fromStop);
        model.addAttribute("toStop", toStop);
        model.addAttribute("date", date);
        return "search-results";
    }
}