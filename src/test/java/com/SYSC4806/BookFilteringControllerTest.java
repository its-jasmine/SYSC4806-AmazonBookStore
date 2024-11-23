package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

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
        when(bookRepository.findByGenre(Book.Genre.Fiction)).thenReturn(new ArrayList<Book>());

        // Verifying that accessing the book details url will successfully show the browse by genre page
        mockMvc.perform(get("/browse-by-genre").param("genre", Book.Genre.Fiction.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("filtered-books-page"));
    }

    @Test
    void showSearchResultsPage() throws Exception {
        when(bookRepository.findByTitle("My Query")).thenReturn(new ArrayList<Book>());

        // Verifying that accessing the book details url will successfully show the search results page
        mockMvc.perform(get("/search").param("query", "My Query"))
                .andExpect(status().isOk())
                .andExpect(view().name("filtered-books-page"));
    }

}