package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test") // Activates the "test" profile for this test class
@WebMvcTest(BookFilteringController.class)
class BookFilteringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserRepository appUserRepository;
    @MockBean
    private CustomerRepository customerRepository;
    @MockBean
    private AdminRepository adminRepository;
    @MockBean
    private BookRepository bookRepository;

    @Test
    void showFilteredBooksByGenrePage() throws Exception {
        Book testBook = new Book("1234567890","Test Title", "Test Author", "Test Publisher", 19.99, Book.Genre.Fiction, 10);
        List<Book> genreBooks = List.of(testBook);

        when(bookRepository.findByGenre(Book.Genre.Fiction)).thenReturn(genreBooks);

        // Verifying that accessing the book details url will successfully show the browse by genre page
        mockMvc.perform(get("/browse-by-genre").param("genre", Book.Genre.Fiction.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("searchResults"))
                .andExpect(model().attribute("searchResults", genreBooks))
                .andExpect(model().attribute("filter", Book.Genre.Fiction));
    }

    @Test
    void showSearchResultsPage_SmallResultsSize() throws Exception {

        Book titleBook = new Book("1234567890", "My Query", "Author 1", "Publisher 1", 19.99, Book.Genre.NonFiction, 5);
        Book authorBook = new Book("0987654321", "Different Title", "My Query", "Publisher 2", 15.99, Book.Genre.Fiction, 3);

        List<Book> titleBooks = List.of(titleBook);
        List<Book> authorBooks = List.of(authorBook);

        when(bookRepository.findByTitleContainingIgnoreCase("My Query")).thenReturn(titleBooks);
        when(bookRepository.findByAuthorContainingIgnoreCase("My Query")).thenReturn(authorBooks);

        List<Book> expectedBooks = Stream.concat(titleBooks.stream(), authorBooks.stream())
                .collect(Collectors.toList());

        Book testBook = new Book("1234567890","Test Title", "Test Author", "Test Publisher", 19.99, Book.Genre.Fiction, 10);
        List<Book> genreBooks = List.of(testBook);

        when(bookRepository.findByTitle("My Query")).thenReturn(new ArrayList<Book>());

        // Verifying that accessing the book details url will successfully show the search results page
        mockMvc.perform(get("/search-results").param("query", "My Query"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("searchResults"))
                .andExpect(model().attribute("searchResults", expectedBooks))
                .andExpect(model().attribute("filter", "My Query"));
    }
    @Test
    public void showSearchResultsPage_LargerResultsSize() throws Exception {
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
        mockMvc.perform(get("/search-results?query=Book4"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("searchResults"));
    }

}