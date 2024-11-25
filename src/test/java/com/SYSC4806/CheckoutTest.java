package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")

@SpringBootTest
public class CheckoutTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testAddingToCart(){
        // Test parameters
        LocalDateTime baseDate = LocalDateTime.now();
        Book b1 = new Book("123456789", "Book1", "Author1", "Publisher1", 20.00, Book.Genre.Fiction, 5 );
        Book b2 = new Book("223456789", "Book2", "Author2", "Publisher2", 24.00, Book.Genre.Mystery, 6 );
        Book b3 = new Book("323456789", "Book3", "Author3", "Publisher3", 25.00, Book.Genre.Fantasy, 20);
        Book b4 = new Book("423456789", "Book4", "Author4", "Publisher4", 28.00, Book.Genre.Fiction, 15 );




    }
}
