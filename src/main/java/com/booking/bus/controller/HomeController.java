package com.booking.bus.controller;

import com.booking.bus.dao.StopDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private StopDAO stopDAO;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stops", stopDAO.findAllUniqueStopNames());
        return "index";
    }

    @PostMapping("/")
    public String search(@RequestParam("fromStop") String fromStop,
                         @RequestParam("toStop") String toStop,
                         @RequestParam("date") String date) {
        return "redirect:/search?from=" + fromStop + "&to=" + toStop + "&date=" + date;
    }
}