package com.booking.bus.controller;

import com.booking.bus.dao.CompanyDAO;
import com.booking.bus.dao.RouteDAO;
import com.booking.bus.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    @Autowired private CompanyDAO companyDAO;
    @Autowired private RouteDAO routeDAO;

    @GetMapping
    public String listCompanies(Model model) {
        model.addAttribute("companies", companyDAO.findAll());
        return "companyList";
    }

    @GetMapping("/{id}")
    public String companyDetails(@PathVariable Integer id, Model model) {
        Company company = companyDAO.findById(id).orElse(null);
        if (company == null) return "redirect:/companies";
        model.addAttribute("company", company);
        model.addAttribute("routes", routeDAO.findRoutesByCompany(id));
        return "companyDetails";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Company company = companyDAO.findById(id).orElse(null);
        if (company == null) return "redirect:/companies";
        model.addAttribute("company", company);
        return "editCompany";
    }

    @PostMapping("/{id}/edit")
    public String updateCompany(@PathVariable Integer id, @ModelAttribute Company updatedCompany) {
        Company company = companyDAO.findById(id).orElse(null);
        if (company == null) return "redirect:/companies";
        company.setName(updatedCompany.getName());
        companyDAO.update(company);
        return "redirect:/companies/" + id;
    }
}