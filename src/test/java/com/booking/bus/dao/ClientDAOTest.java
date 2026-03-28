package com.booking.bus.dao;

import com.booking.bus.entity.Client;
import com.booking.bus.test.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class ClientDAOTest extends BaseTest {
    private ClientDAO clientDAO;

    @BeforeMethod
    public void init() {
        clientDAO = new ClientDAO(sessionFactory);
    }

    // default

    @Test
    public void testSaveAndFind() {
        Client client = new Client();
        client.setFullName("John Doe");
        client.setEmail("john@example.com");
        client.setPhone("123456");
        Client saved = inTransactionResult(session -> clientDAO.save(client));

        assertNotNull(saved.getId());
        assertEquals(saved.getFullName(), "John Doe");

        Optional<Client> found = clientDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getEmail(), "john@example.com");
    }

    @Test
    public void testFindAll() {
        Client client1 = new Client();
        client1.setFullName("Alice Smith");
        client1.setEmail("alice@example.com");
        Client client2 = new Client();
        client2.setFullName("Bob Johnson");
        client2.setEmail("bob@example.com");
        inTransaction(session -> {
            clientDAO.save(client1);
            clientDAO.save(client2);
        });

        List<Client> all = clientDAO.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    public void testUpdate() {
        Client client = new Client();
        client.setFullName("Old Name");
        client.setEmail("old@example.com");
        client.setPhone("111");
        Client saved = inTransactionResult(session -> clientDAO.save(client));

        saved.setFullName("New Name");
        saved.setPhone("999");
        Client updated = inTransactionResult(session -> clientDAO.update(saved));

        assertEquals(updated.getFullName(), "New Name");
        assertEquals(updated.getPhone(), "999");

        Optional<Client> found = clientDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get().getFullName(), "New Name");
    }

    @Test
    public void testDelete() {
        Client client = new Client();
        client.setFullName("To Delete");
        client.setEmail("delete@example.com");
        client.setPhone("000");
        Client saved = inTransactionResult(session -> clientDAO.save(client));

        inTransaction(session -> clientDAO.delete(saved));

        Optional<Client> found = clientDAO.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
