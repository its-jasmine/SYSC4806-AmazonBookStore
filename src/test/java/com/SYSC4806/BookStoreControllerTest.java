package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(BookStoreController.class)
class BookStoreControllerTest {
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
    void showHomePage() throws Exception{
        // Verifying that accessing the home url will successfully show the home page
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home-page"));
    }

    @Test
    void addNewBooks() throws Exception {
        String title = "Some Book";
        String author = "Some Author";
        String publisher = "Some Publisher";
        String genre = "Some Genre";
        int numCopies = 2;

        when(bookRepository.findByTitleAndAuthor(title, author)).thenReturn(Optional.empty());

        // Verifying that adding a book will successfully redirect to home page
        mockMvc.perform(post("/books")
                        .param("title", title)
                        .param("author", author)
                        .param("publisher", publisher)
                        .param("genre", genre)
                        .param("numCopies", String.valueOf(numCopies)))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));

        // Verify that the book with specified values is saved in the repository
        verify(bookRepository).save(argThat(b -> b.getTitle().equals(title) &&
                b.getAuthor().equals(author) &&
                b.getPublisher().equals(publisher) &&
                b.getGenre().equals(genre) &&
                b.getNumCopies() == numCopies));
    }
    @Test
    public void addToExistingBooks() throws Exception{
        String title = "Some Book";
        String author = "Some Author";
        String publisher = "Some Publisher";
        String genre = "Some Genre";
        int existingNumCopies = 1;

        Book book = new Book(title, author, publisher, genre, existingNumCopies);
        when(bookRepository.findByTitleAndAuthor(title, author)).thenReturn(Optional.of(book));

        int numOfNewCopies = 1;
        // Verifying that adding copies to an existing book will successfully redirect to home page
        mockMvc.perform(post("/books")
                        .param("title", title)
                        .param("author", author)
                        .param("publisher", publisher)
                        .param("genre", genre)
                        .param("numCopies", String.valueOf(numOfNewCopies)))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));

        // Verify that the book’s stock count was updated
        verify(bookRepository).save(argThat(b -> b.getTitle().equals(title) &&
                b.getAuthor().equals(author) &&
                b.getPublisher().equals(publisher) &&
                b.getGenre().equals(genre) &&
                b.getNumCopies() == (existingNumCopies + numOfNewCopies)));
    }

    @Test
    public void removeBook() throws Exception{
        // Mock a book in the repository
        Book book = new Book("Some Book", "Some Author", "Some Publisher", "Some Genre", 1);
        when(bookRepository.findByTitleAndAuthor("Some Book", "Some Author")).thenReturn(Optional.of(book));

        mockMvc.perform(post("/remove-books")
                        .param("title", "Some Book")
                        .param("author", "Some Author"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));

        // Verify that the book’s stock count was updated
        verify(bookRepository).save(argThat(b -> b.getNumCopies() == 0));


    }

    @Test
    void showBookManagementPage() throws Exception {
        // Verifying that accessing the book management url will successfully show the book management page
        mockMvc.perform(get("/book-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-management"));
    }
}