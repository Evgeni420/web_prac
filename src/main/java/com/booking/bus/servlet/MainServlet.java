package com.booking.bus.servlet;

import com.booking.bus.service.RouteService;
import com.booking.bus.service.SearchService;
import com.booking.bus.config.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/")
public class MainServlet extends HttpServlet {
    private RouteService routeService;
    private SearchService searchService;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Initializing MainServlet...");
            var sessionFactory = HibernateUtil.getSessionFactory();
            routeService = new RouteService(sessionFactory);
            searchService = new SearchService(sessionFactory);
            System.out.println("MainServlet initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize MainServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize services", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null) {
            showHomePage(req, resp);
        } else if (action.equals("search")) {
            searchRoutes(req, resp);
        } else {
            showHomePage(req, resp);
        }
    }

    private void showHomePage(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
    }

    private void searchRoutes(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            String fromStop = req.getParameter("fromStop");
            String toStop = req.getParameter("toStop");
            String dateStr = req.getParameter("date");

            if (fromStop != null && !fromStop.trim().isEmpty() && toStop != null && !toStop.trim().isEmpty() && dateStr != null && !dateStr.trim().isEmpty()) {

                LocalDate date = LocalDate.parse(dateStr);

                // Search for routes
                List<Map<String, Object>> searchResults = searchService.searchRoutes(fromStop, toStop, date);
                req.setAttribute("routes", searchResults);

                Map<String, String> searchParams = new HashMap<>();
                searchParams.put("fromStop", fromStop);
                searchParams.put("toStop", toStop);
                searchParams.put("date", dateStr);
                req.setAttribute("searchParams", searchParams);
            }

            req.getRequestDispatcher("/jsp/search-results.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("Error in searchRoutes: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Search failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
        }
    }
}