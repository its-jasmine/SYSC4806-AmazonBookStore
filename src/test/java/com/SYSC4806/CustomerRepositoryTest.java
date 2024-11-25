package com.SYSC4806;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test") // Activates the "test" profile for this test class
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testCreateAndRead() {
        Customer customer = new Customer("customer1", "password1");
        customerRepository.save(customer);

        Optional<Customer> retrievedAdmin = customerRepository.findCustomerByUsername("customer1");

        assertTrue(retrievedAdmin.isPresent());
        assertEquals("customer1", retrievedAdmin.get().getUsername());
        assertEquals("password1", retrievedAdmin.get().getPassword());
    }

    @Test
    public void testUpdate() {
        Customer customer = new Customer("customer1", "password1");
        customerRepository.save(customer);

        customer.setPassword("password2");
        customerRepository.save(customer);

        Optional<Customer> updatedCustomer = customerRepository.findCustomerByUsername("customer1");
        assertTrue(updatedCustomer.isPresent());
        assertEquals("password2", updatedCustomer.get().getPassword());
    }

    @Test
    public void testDelete() {
        Customer customer = new Customer("customer1", "password1");
        customerRepository.save(customer);

        customerRepository.delete(customer);

        Optional<Customer> retrievedCustomer = customerRepository.findCustomerByUsername("customer1");
        assertFalse(retrievedCustomer.isPresent());
    }

    @Test
    public void testFindAdminByNonExistentUsername() {
        Optional<Customer> retrievedCustomer = customerRepository.findCustomerByUsername("customer2");
        assertFalse(retrievedCustomer.isPresent());
    }

    @Test
    public void testAddBookToPurchaseHistory() {
        Customer customer = new Customer("customer1", "password1");
        Book book = new Book("1234567890","Effective Java", "Joshua Bloch", "Programming", 19.99,Book.Genre.Fantasy, 100);
        customer.addToHistory(book);
        customerRepository.save(customer);

        Optional<Customer> retrievedCustomer = customerRepository.findById(customer.getId());

        assertTrue(retrievedCustomer.isPresent());
        assertEquals(1, retrievedCustomer.get().getPurchaseHistory().size());
        assertTrue(retrievedCustomer.get().getPurchaseHistory().contains(book));
    }

    @Test
    public void testRemoveBookFromPurchaseHistory() {
        Customer customer = new Customer("customer1", "password1");
        Book book1 = new Book("1234567890","Effective Java", "Joshua Bloch", "Programming", 19.99,Book.Genre.Fantasy, 100);
        Book book2 = new Book("1234567891","Clean Code", "Someone Else", "Programming", 19.99,Book.Genre.NonFiction, 20);
        customer.addToHistory(book1);
        customer.addToHistory(book2);
        customerRepository.save(customer);

        customer.getPurchaseHistory().remove(book1);
        customerRepository.save(customer);

        Optional<Customer> retrievedCustomer = customerRepository.findById(customer.getId());
        assertTrue(retrievedCustomer.isPresent());
        assertEquals(1, retrievedCustomer.get().getPurchaseHistory().size());
        assertFalse(retrievedCustomer.get().getPurchaseHistory().contains(book1));
    }
}