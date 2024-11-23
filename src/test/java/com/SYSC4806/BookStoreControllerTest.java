package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test") // Activates the "test" profile for this test class
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
    @InjectMocks
    private BookStoreController bookStoreController;

    @Test
    void showHomePage() throws Exception{
        // Verifying that accessing the home url will successfully show the home page
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home-page"));
    }

    @Test
    void addNewBooks() throws Exception {
        String ISBN = "1234567890";
        String title = "Some Book";
        String author = "Some Author";
        String publisher = "Some Publisher";
        double price = 19.99;
        Book.Genre genre = Book.Genre.Memoirs;
        int numCopies = 2;

        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.empty());

        // Verifying that adding a book will successfully redirect to home page
        mockMvc.perform(post("/books")
                        .param("ISBN", ISBN)
                        .param("title", title)
                        .param("author", author)
                        .param("publisher", publisher)
                        .param("price", price + "")
                        .param("genre", genre.name())
                        .param("numCopies", String.valueOf(numCopies)))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/inventory"));

        // Verify that the book with specified values is saved in the repository
        verify(bookRepository).save(argThat(b -> b.getISBN().equals(ISBN) &&
                b.getTitle().equals(title) &&
                b.getAuthor().equals(author) &&
                b.getPublisher().equals(publisher) &&
                b.getPrice() == price &&
                b.getGenre().equals(genre) &&
                b.getNumCopiesInStock() == numCopies));
    }
    @Test
    public void addToExistingBooks() throws Exception{
        String ISBN = "1234567890";
        String title = "Some Book";
        String author = "Some Author";
        String publisher = "Some Publisher";
        double price = 19.99;
        Book.Genre genre = Book.Genre.Fantasy;
        int existingNumCopies = 1;

        Book book = new Book(ISBN,title, author, publisher, price,genre, existingNumCopies);
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(book));

        int numOfNewCopies = 1;
        // Verifying that adding copies to an existing book will successfully redirect to home page
        mockMvc.perform(post("/books")
                        .param("ISBN", ISBN)
                        .param("title", title)
                        .param("author", author)
                        .param("publisher", publisher)
                        .param("price", price + "")
                        .param("genre", genre.name())
                        .param("numCopies", String.valueOf(numOfNewCopies)))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to book management
                .andExpect(redirectedUrl("/book-management"));

        // Verify that no save calls were made
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void removeBook() throws Exception{
        String ISBN = "1234567890";
        // Mock a book in the repository
        Book book = new Book(ISBN, "Some Title", "Some Author", "Some Publisher", 19.99, Book.Genre.Fiction, 1);
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(book));

        mockMvc.perform(post("/remove-books")
                        .param("ISBN", ISBN))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/inventory"));

        // Verify that the book’s stock count was updated
        verify(bookRepository).delete(book);


    }

    @Test
    void showBookManagementPage() throws Exception {
        // Verifying that accessing the book management url will successfully show the book management page
        mockMvc.perform(get("/book-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-management"));
    }
    @Test
    void showBookDetailsPage() throws Exception {
        String ISBN = "1234567890";
        Book book = new Book(ISBN, "Some Title", "Some Author", "Some Publisher", 19.99, Book.Genre.Fiction, 1);
        when(bookRepository.findByISBN("1234567890")).thenReturn(Optional.of(book));

        // Verifying that accessing the book details url will successfully show the book details page
        mockMvc.perform(get("/book-details").param("ISBN", ISBN))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"));
    }

    @Test
    public void searchBook() throws Exception {
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

        String query = "4";
        Model model = new ConcurrentModel();
        String viewName = bookStoreController.search(query, model);

        assertEquals("search-results", viewName);
        assertEquals(bookRepository.findAll(), model.getAttribute("searchResults"));
        verify(bookRepository, times(1)).findByTitleContaining(query);


    }
}