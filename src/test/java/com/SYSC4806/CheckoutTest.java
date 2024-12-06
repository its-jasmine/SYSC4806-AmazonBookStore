package com.SYSC4806;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CheckoutTest {

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private CheckoutService checkoutService;

    // Define mock objects for Customer and Book
    private Customer mockCustomer;
    private Book mockBook;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer("testUser", "password"); // Set up your mock customer here
        mockBook = new Book(); // Set up your mock book here
    }

    @Test
    void testCheckout_Success() {
        // Given
        String username = "testUser";
        List<String> books = List.of("ISBN123");
        String cardNumber = "1234567812345678";
        String expiry = "12/25";
        String cvv = "123";

        // Set up mockBook and mockCustomer
        mockBook.setISBN("ISBN123");
        mockBook.setNumCopiesInStock(5);

        mockCustomer.setUsername(username);
        mockCustomer.getCart().put(mockBook, 1);

        // Mock repository behavior
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.of(mockCustomer));
        when(bookRepository.findByISBN("ISBN123")).thenReturn(Optional.of(mockBook));

        // Simulate checkout
        boolean result = checkoutService.checkout(username, books, cardNumber, expiry, cvv);

        // Assert the result
        assertTrue(result, "Checkout should succeed");
        verify(customerRepository).save(mockCustomer);
        assertEquals(4, mockBook.getNumCopiesInStock(), "Book stock should be reduced by 1");
        assertTrue(mockCustomer.getCart().isEmpty(), "Cart should be empty after checkout");
    }

    @Test
    void testCheckout_Failure_CustomerNotFound() {
        // Given
        String username = "unknownUser";
        List<String> books = List.of("ISBN123");
        String cardNumber = "1234567812345678";
        String expiry = "12/25";
        String cvv = "123";

        // Mock customer not found
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.empty());

        // Simulate checkout
        boolean result = checkoutService.checkout(username, books, cardNumber, expiry, cvv);

        // Assert the result
        assertFalse(result); // Should return false since customer is not found
    }

    @Test
    void testCheckout_Failure_BookNotFound() {
        // Given
        String username = "testUser";
        List<String> books = List.of("ISBN999");  // Assuming ISBN999 doesn't exist
        String cardNumber = "1234567812345678";
        String expiry = "12/25";
        String cvv = "123";

        mockCustomer.setUsername(username);

        // Mock customer found, but book not found
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.of(mockCustomer));
        when(bookRepository.findByISBN("ISBN999")).thenReturn(Optional.empty());

        // Simulate checkout
        boolean result = checkoutService.checkout(username, books, cardNumber, expiry, cvv);

        // Assert the result
        assertFalse(result); // Should return false since book is not found
    }

    @Test
    void testCheckout_Failure_NotEnoughCopies() {
        // Given
        String username = "testUser";
        List<String> books = List.of("ISBN123");
        String cardNumber = "1234567812345678";
        String expiry = "12/25";
        String cvv = "123";

        mockCustomer.setUsername(username);
        mockBook.setISBN("ISBN123");
        mockBook.setNumCopiesInStock(1);  // Set stock to 1

        // Mock repository behavior
        when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.of(mockCustomer));
        when(bookRepository.findByISBN("ISBN123")).thenReturn(Optional.of(mockBook));

        // Add more than 1 book to the cart (e.g., trying to purchase 2 books)
        mockCustomer.getCart().put(mockBook, 2);

        // Simulate checkout
        boolean result = checkoutService.checkout(username, books, cardNumber, expiry, cvv);

        // Assert the result
        assertFalse(result); // Should return false because there are not enough copies in stock
    }
}
