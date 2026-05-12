package com.booking.bus.dao;

import com.booking.bus.entity.Client;
import org.hibernate.SessionFactory;

import java.util.List;

public class ClientDAO extends BaseDAO<Client, Integer> {

    public ClientDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Client.class);
    }

    @Override
    public List<Client> findAll() {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Client c ORDER BY c.createdAt DESC";
            return session.createQuery(hql, Client.class).list();
        }
    }

    public List<Client> searchClients(String searchTerm) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Client c WHERE LOWER(c.fullName) LIKE :search " +
                        "OR LOWER(c.email) LIKE :search " +
                        "OR LOWER(c.phone) LIKE :search";

            return session.createQuery(hql, Client.class)
                    .setParameter("search", "%" + searchTerm.toLowerCase() + "%")
                    .list();
        }
    }

    public List<Client> findClientsByTrip(Integer tripId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT DISTINCT b.client FROM Booking b WHERE b.trip.id = :tripId";
            return session.createQuery(hql, Client.class)
                    .setParameter("tripId", tripId)
                    .list();
        }
    }

    public List<Client> findClientsByCompany(Integer companyId) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT DISTINCT b.client FROM Booking b " +
                        "WHERE b.trip.route.company.id = :companyId";
            return session.createQuery(hql, Client.class)
                    .setParameter("companyId", companyId)
                    .list();
        }
    }

    public Client findByEmail(String email) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Client c WHERE c.email = :email";
            return session.createQuery(hql, Client.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }
}
