package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test") // Activates the "test" profile for this test class
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testSaveAndFindById() {
        // Arrange
        Book book = new Book("1234567890","Effective Java", "Joshua Bloch", "Programming", 19.99,Book.Genre.Fantasy, 100);
        book = bookRepository.save(book);

        // Act
        Optional<Book> retrievedBook = bookRepository.findById(book.getId());

        // Assert
        assertTrue(retrievedBook.isPresent());
        assertEquals("Effective Java", retrievedBook.get().getTitle());
        assertEquals("Joshua Bloch", retrievedBook.get().getAuthor());
    }

    @Test
    public void testFindByTitle() {
        // Arrange
        bookRepository.save(new Book("1234567890","Clean Code", "Robert C. Martin", "Programming", 19.99,Book.Genre.Mystery, 50));
        bookRepository.save(new Book("1234567891","Clean Code", "Someone Else", "Programming", 19.99,Book.Genre.NonFiction, 20));

        // Act
        Iterable<Book> books = bookRepository.findByTitle("Clean Code");

        // Assert
        int count = 0;
        for (Book book : books) {
            assertEquals("Clean Code", book.getTitle());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testFindTop10ByOrderByNumCopiesSoldDesc() {
        // Test parameters
        int numberOfBooks = 11; // Total books to create
        int topBooksLimit = 10; // Number of top books expected
        int copiesSoldIncrement = 5; // Increment for each book's copies sold
        String baseTitle = "Book"; // Base title prefix for books
        int baseISBN = 1000000000;

        // Prepare test data
        Book eleventhBestSeller = null;
        int copiesSold = 0;

        for (int i = 1; i <= numberOfBooks; i++) {
            Book book = new Book((baseISBN+i)+"",baseTitle + i, "author", "publisher", 19.99, Book.Genre.Memoirs, 1);
            book.setNumCopiesSold(copiesSold);
            if (i == 1) eleventhBestSeller = book; // The first book has the fewest copies sold
            bookRepository.save(book);
            copiesSold += copiesSoldIncrement;
        }

        // Execute the query
        List<Book> top10 = bookRepository.findTop10ByOrderByNumCopiesSoldDesc();

        // Assert results
        assertNotNull(top10);
        assertEquals(Math.min(topBooksLimit, numberOfBooks), top10.size());

        // Validate the first book (highest copies sold)
        Book firstBook = top10.get(0);
        assertEquals(baseTitle + numberOfBooks, firstBook.getTitle());
        assertEquals(copiesSoldIncrement * (numberOfBooks - 1), firstBook.getNumCopiesSold());

        // Validate the last book in the top 10
        Book lastBook = top10.get(top10.size() - 1);
        assertEquals(baseTitle + (numberOfBooks - topBooksLimit + 1), lastBook.getTitle());
        assertEquals(copiesSoldIncrement * (numberOfBooks - topBooksLimit), lastBook.getNumCopiesSold());

        // Validate the 11th book (excluded from the top 10)
        assertNotNull(eleventhBestSeller);
        assertEquals(baseTitle + 1, eleventhBestSeller.getTitle());
        assertEquals(0, eleventhBestSeller.getNumCopiesSold());
        assertFalse(top10.contains(eleventhBestSeller));
    }

    @Test
    public void testFindTop10ByOrderByDateAddedAsc() {
        // Test parameters
        String baseTitle = "Book";
        int numberOfBooks = 11; // Total books to create
        int topBooksLimit = 10; // Number of top books expected
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

        // Execute the query
        List<Book> top10 = bookRepository.findTop10ByOrderByDateAddedDesc();
        top10.forEach(b -> System.out.println(b));

        // Assert results
        assertNotNull(top10);
        assertEquals(Math.min(topBooksLimit, numberOfBooks), top10.size());

        // Validate the first book (latest date added)
        Book firstBook = top10.get(0);
        assertEquals(baseTitle + numberOfBooks, firstBook.getTitle());
        assertEquals(baseDate.plusDays(numberOfBooks), firstBook.getDateAdded());

        // Validate the last book (earliest date added within the top 10)
        Book lastBook = top10.get(top10.size() - 1);
        assertEquals(baseTitle + (numberOfBooks - topBooksLimit + 1), lastBook.getTitle());
        assertEquals(baseDate.plusDays(numberOfBooks - topBooksLimit + 1), lastBook.getDateAdded());
    }

    @Test
    public void testFindByTitleAndAuthor() {
        // Arrange
        bookRepository.save(new Book("1234567890","The Pragmatic Programmer", "Andrew Hunt", "Programming", 19.99, Book.Genre.Memoirs, 70));

        // Act
        Optional<Book> book = bookRepository.findByISBN("1234567890");

        // Assert
        assertTrue(book.isPresent());
        assertEquals("The Pragmatic Programmer", book.get().getTitle());
        assertEquals("Andrew Hunt", book.get().getAuthor());
    }

    @Test
    public void testFindByTitleAndAuthor_NotFound() {
        // Arrange
        bookRepository.save(new Book("1234567890","Book A", "Author A", "Category A", 19.99, Book.Genre.Fiction, 50));

        // Act
        Optional<Book> book = bookRepository.findByISBN("1234567891");

        // Assert
        assertFalse(book.isPresent());
    }
}
