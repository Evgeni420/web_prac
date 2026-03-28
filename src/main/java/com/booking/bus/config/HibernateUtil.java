package com.booking.bus.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    static {
        try {
            Configuration configuration = new Configuration();

            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.booking.bus.entity.Company.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Route.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.RouteStop.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Trip.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Client.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Fare.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Seat.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.Booking.class);
            configuration.addAnnotatedClass(com.booking.bus.entity.SeatOccupancy.class);

            sessionFactory = configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            System.err.println("WARNING: SessionFactory is null");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("SessionFactory closed");
        }
    }
}
