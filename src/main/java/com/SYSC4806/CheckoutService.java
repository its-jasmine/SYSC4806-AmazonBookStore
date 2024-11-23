package com.SYSC4806;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CheckoutService(CustomerRepository customerRepository, BookRepository bookRepository) {
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
    }

    public boolean checkout(String username, List<String> bookISBNs, String cardNumber, String expiry, String cvv) {
        System.out.println("Starting checkout process...");
        System.out.println("Username: " + username);
        System.out.println("Book IDs: " + bookISBNs);
        System.out.println("Card Details - Card Number: " + cardNumber + ", Expiry: " + expiry + ", CVV: " + cvv);

        // Validate payment info (mock implementation, replace with actual validation)
        if (cardNumber == null || expiry == null || cvv == null) {
            System.out.println("Validation failed: Card details are missing.");
            return false;
        }
        System.out.println("Card details validated successfully.");

        // Retrieve the customer
        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            System.out.println("Error: Customer not found for username: " + username);
            return false;
        }
        System.out.println("Customer retrieved successfully: " + customer);

        // Process the selected books and clear them from the cart
        for (String bookId : bookISBNs) {
            Book book = bookRepository.findByISBN(bookId).orElse(null);
            if (book != null) {
                customer.addToHistory(book);
                if (customer.getCart().containsKey(book)) {
                    customer.removeFromCart(bookId);
                } else {
                    System.out.println("Book not in cart, skipping removal: " + book);
                }
            } else {
                System.out.println("Error: Book not found for ID: " + bookId);
            }
        }

        // Save the updated customer state
        System.out.println("Saving updated customer state...");
        customerRepository.save(customer);
        System.out.println("Customer state saved successfully: " + customer);

        return true;
    }

}