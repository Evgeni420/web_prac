package com.booking.bus.dao;

import com.booking.bus.entity.Company;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class CompanyDAO extends BaseDAO<Company, Integer> {

    public CompanyDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Company.class);
    }

    @Override
    public Optional<Company> findById(Integer id) {
        try (var session = sessionFactory.openSession()) {
            String hql = "SELECT c FROM Company c " +
                         "LEFT JOIN FETCH c.routes " +
                         "WHERE c.id = :id";
            Company company = session.createQuery(hql, Company.class)
                                     .setParameter("id", id)
                                     .uniqueResult();
            return Optional.ofNullable(company);
        }
    }

    public Company findByName(String name) {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Company c WHERE c.name = :name";
            return session.createQuery(hql, Company.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    public List<Company> findAllWithRoutes() {
        try (var session = sessionFactory.openSession()) {
            String hql = "FROM Company c LEFT JOIN FETCH c.routes";
            return session.createQuery(hql, Company.class).list();
        }
    }
}
