package com.SYSC4806;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private BookRepository bookRepository;

    private MockHttpSession session;
    private Customer customer;
    private Book wishlistBook, wishlistBook1;

    @BeforeEach
    void setUp() {
        // Initialize customer and session
        customer = new Customer();
        customer.setUsername("testCustomer");

        session = new MockHttpSession();
        session.setAttribute("username", customer.getUsername());

        // Initialize books for wishlist
        wishlistBook = new Book("1234567000", "Wishlist Book", "Author", "Publisher", 29.99, Book.Genre.NonFiction, 5);
        wishlistBook1 = new Book("1234567001", "Wishlist Book 1", "Author", "Publisher", 29.99, Book.Genre.NonFiction, 5);

        Set<Book> wishlist = new HashSet<>();
        wishlist.add(wishlistBook);
        wishlist.add(wishlistBook1);

        customer.setWishlist(wishlist);

        // Mock repository behavior
        when(customerRepository.findCustomerByUsername("testCustomer")).thenReturn(Optional.of(customer));
        when(bookRepository.findByISBN("1234567000")).thenReturn(Optional.of(wishlistBook));
    }

    @Test
    void showProfilePage() throws Exception {
        // Perform GET request and verify
        mockMvc.perform(get("/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer", customer))
                .andExpect(model().attribute("purchaseHistory", customer.getPurchaseHistory()))
                .andExpect(model().attribute("wishlist", customer.getWishlist()));
    }

    @Test
    void testLogOut() throws Exception {
        // Perform GET request for logout
        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testAddToWishlist() throws Exception {
        Book newBook = new Book("9876543210", "New Wishlist Book", "Author", "Publisher", 24.99, Book.Genre.Fiction, 5);

        // Setup mocks
        when(bookRepository.findByISBN("9876543210")).thenReturn(Optional.of(newBook));
        when(customerRepository.findCustomerByUsername("testCustomer")).thenReturn(Optional.of(customer));

        // Perform the POST request
        mockMvc.perform(post("/wishlist/add")
                        .param("bookId", "9876543210")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book-details?ISBN=9876543210"));

        // Verify the customer has the book in the wishlist
        assertTrue(customer.getWishlist().contains(newBook));  // Ensures book is in the wishlist
    }

    @Test
    void removeFromWishlist() throws Exception {
        // Setup mocks
        when(bookRepository.findByISBN("1234567000")).thenReturn(Optional.of(wishlistBook));
        when(customerRepository.findCustomerByUsername("testCustomer")).thenReturn(Optional.of(customer));

        // Perform the POST request
        mockMvc.perform(post("/wishlist/remove")
                        .param("bookId", wishlistBook.getISBN())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        // Verify the customer has the book removed from the wishlist
        assertFalse(customer.getWishlist().contains(wishlistBook));  // Ensures book is removed from wishlist
    }
}

