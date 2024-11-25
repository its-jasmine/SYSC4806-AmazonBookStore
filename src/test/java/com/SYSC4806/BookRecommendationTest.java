package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test") // Activates the "test" profile for this test class
@SpringBootTest
public class BookRecommendationTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testRecommendation() {
        // Test parameters
        String baseTitle = "Book";
        int numberOfBooks = 20; // Total books to create
        int baseISBN = 1000000000;
        LocalDateTime baseDate = LocalDateTime.now(); // Base date for "dateAdded"

        // Prepare test data
        for (int i = 1; i <= numberOfBooks; i++) {
            Book book = new Book(
                    (baseISBN+i)+"",
                    baseTitle + i,
                    "author",
                    "publisher",
                    19.99,
                    Book.Genre.Fiction,
                    1
            );
            // Set different "dateAdded" values
            book.setDateAdded(baseDate.plusDays(i));
            bookRepository.save(book);
        }

        String baseUsername = "customer";
        String basePassword = "password";
        int numberOfCustomers = 8;

        for (int i = 1; i <= numberOfCustomers; i++) {
            Customer customer = new Customer(
                    baseUsername+i,
                    basePassword+i
            );

            for (int j = 0; j < 5; j++) {
                customer.addToHistory(bookRepository.findByISBN(baseISBN+(i+j)+"").get());
            }
            customerRepository.save(customer);
        }

        BookRecommendation bookRecommendation = new BookRecommendation((List<Customer>) customerRepository.findAll());
        ArrayList<Book> recommendedBooks = (ArrayList<Book>) bookRecommendation.getRecommendation(customerRepository.findCustomerByUsername("customer1").get());

        assert(recommendedBooks.size() == 4);
        assert(recommendedBooks.contains(bookRepository.findByISBN("1000000006").get()));
        assert(recommendedBooks.contains(bookRepository.findByISBN("1000000007").get()));
        assert(recommendedBooks.contains(bookRepository.findByISBN("1000000008").get()));
        assert(recommendedBooks.contains(bookRepository.findByISBN("1000000009").get()));

        customerRepository.deleteAll();
        bookRepository.deleteAll();

    }

    @Test
    public void testRecommendationSameCustomer() {
        // Test parameters
        String baseTitle = "Book";
        int numberOfBooks = 20; // Total books to create
        int baseISBN = 1000000000;
        LocalDateTime baseDate = LocalDateTime.now(); // Base date for "dateAdded"

        // Prepare test data
        for (int i = 1; i <= numberOfBooks; i++) {
            Book book = new Book(
                    (baseISBN+i)+"",
                    baseTitle + i,
                    "author",
                    "publisher",
                    19.99,
                    Book.Genre.Fiction,
                    1
            );
            // Set different "dateAdded" values
            book.setDateAdded(baseDate.plusDays(i));
            bookRepository.save(book);
        }

        String baseUsername = "customer";
        String basePassword = "password";
        int numberOfCustomers = 8;

        for (int i = 1; i <= numberOfCustomers; i++) {
            Customer customer = new Customer(
                    baseUsername+i,
                    basePassword+i
            );

            for (int j = 1; j <= 5; j++) {
                customer.addToHistory(bookRepository.findByISBN(baseISBN+j+"").get());
            }
            customerRepository.save(customer);
        }

        BookRecommendation bookRecommendation = new BookRecommendation((List<Customer>) customerRepository.findAll());
        ArrayList<Book> recommendedBooks = (ArrayList<Book>) bookRecommendation.getRecommendation(customerRepository.findCustomerByUsername("customer1").get());

        assert (recommendedBooks.isEmpty());

        customerRepository.deleteAll();
        bookRepository.deleteAll();
    }
}
