package com.booking.bus.controller;

import com.booking.bus.dao.StopDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AutocompleteController {

    @Autowired
    private StopDAO stopDAO;

    @GetMapping("/stops")
    public List<String> autocompleteStops(@RequestParam("term") String term) {
        return stopDAO.findStopNamesContaining(term);
    }
}
