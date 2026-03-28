package com.booking.bus.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseTest {
    protected static SessionFactory sessionFactory;

    @BeforeSuite
    public static void setUp() {
        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .buildSessionFactory();
    }

    @AfterMethod
    public void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("DELETE FROM seat_occupancies").executeUpdate();
            session.createNativeQuery("DELETE FROM bookings").executeUpdate();
            session.createNativeQuery("DELETE FROM fares").executeUpdate();
            session.createNativeQuery("DELETE FROM seats").executeUpdate();
            session.createNativeQuery("DELETE FROM trips").executeUpdate();
            session.createNativeQuery("DELETE FROM route_stops").executeUpdate();
            session.createNativeQuery("DELETE FROM routes").executeUpdate();
            session.createNativeQuery("DELETE FROM clients").executeUpdate();
            session.createNativeQuery("DELETE FROM companies").executeUpdate();
            tx.commit();
        }
    }

    protected void inTransaction(Consumer<Session> work) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            work.accept(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    protected <R> R inTransactionResult(Function<Session, R> work) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            R result = work.apply(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
