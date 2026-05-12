package com.booking.bus.controller;

import com.booking.bus.dao.BookingDAO;
import com.booking.bus.dao.ClientDAO;
import com.booking.bus.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired private ClientDAO clientDAO;
    @Autowired private BookingDAO bookingDAO;

    @GetMapping
    public String listClients(@RequestParam(required = false) String search, Model model) {
        List<Client> clients;
        if (search != null && !search.isEmpty()) {
            clients = clientDAO.searchClients(search);
        } else {
            clients = clientDAO.findAll();
        }
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/{id}")
    public String clientDetails(@PathVariable Integer id, Model model) {
        Client client = clientDAO.findById(id).orElse(null);
        if (client == null) return "redirect:/clients";
        model.addAttribute("client", client);
        model.addAttribute("bookings", bookingDAO.findBookingsByClient(id));
        return "client-profile";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("client", new Client());
        return "register";
    }

    @PostMapping("/register")
    public String registerClient(@ModelAttribute Client client) {
        clientDAO.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Client client = clientDAO.findById(id).orElse(null);
        if (client == null) return "redirect:/clients";
        model.addAttribute("client", client);
        return "editClient";
    }

    @PostMapping("/{id}/edit")
    public String updateClient(@PathVariable Integer id, @ModelAttribute Client updatedClient) {
        Client client = clientDAO.findById(id).orElse(null);
        if (client == null) return "redirect:/clients";
        client.setFullName(updatedClient.getFullName());
        client.setAddress(updatedClient.getAddress());
        client.setPhone(updatedClient.getPhone());
        client.setEmail(updatedClient.getEmail());
        clientDAO.update(client);
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Integer id) {
        clientDAO.deleteById(id);
        return "redirect:/clients";
    }
}