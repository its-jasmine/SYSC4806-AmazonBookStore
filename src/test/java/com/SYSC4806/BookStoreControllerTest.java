package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void updateBookStock() throws Exception{
        String ISBN = "1234567890";
        String numCopies = "34";
        // Mock a book in the repository
        Book book = new Book(ISBN, "Some Title", "Some Author", "Some Publisher", 19.99, Book.Genre.Fiction, 1);
        when(bookRepository.findByISBN(ISBN)).thenReturn(Optional.of(book));

        mockMvc.perform(post("/update-books")
                        .param("ISBN", ISBN)
                        .param("numCopies", numCopies))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/inventory"));

        // Verify that the book’s stock count was updated
        verify(bookRepository).save(argThat(b -> b.getISBN().equals(ISBN) &&
                b.getNumCopiesInStock() == Integer.parseInt(numCopies)));
    }

    @Test
    void showBookManagementPage_Admin() throws Exception {
        AppUser adminUser = new Admin("admin", "adminPassword");
        when(appUserRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        mockMvc.perform(get("/book-management")
                        .sessionAttr("username", adminUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(view().name("book-management"));
    }

    @Test
    void showBookManagementPage_Customer() throws Exception {
        AppUser customerUser = new Customer("customer", "customerPassword");
        when(appUserRepository.findByUsername(customerUser.getUsername())).thenReturn(Optional.of(customerUser));
        mockMvc.perform(get("/book-management")
                        .sessionAttr("username", customerUser.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void showInventoryPage_NoUser() throws Exception {
        mockMvc.perform(get("/inventory"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void showInventoryPage_Admin() throws Exception {
        AppUser adminUser = new Admin("admin", "adminPassword");
        when(appUserRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        mockMvc.perform(get("/inventory")
                        .sessionAttr("username", adminUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory-page"));
    }

    @Test
    void showInventoryPage_Customer() throws Exception {
        AppUser customerUser = new Customer("customer", "customerPassword");
        when(appUserRepository.findByUsername(customerUser.getUsername())).thenReturn(Optional.of(customerUser));
        mockMvc.perform(get("/inventory")
                        .sessionAttr("username", customerUser.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void showBookManagementPage_NoUser() throws Exception {
        mockMvc.perform(get("/book-management"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void showBookDetailsPage() throws Exception {
        String ISBN = "1234567890";
        Book book = new Book(ISBN, "Some Title", "Some Author", "Some Publisher", 19.99, Book.Genre.Fiction, 1);
        book.setWorkId("/works/OL45804W");
        when(bookRepository.findByISBN("1234567890")).thenReturn(Optional.of(book));

        // Verifying that accessing the book details url will successfully show the book details page
        mockMvc.perform(get("/book-details").param("ISBN", ISBN))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("description"))
                .andExpect(view().name("book-details"));
    }

    @Test
    public void testAddReview_BookExists() throws Exception {
        Book testBook = new Book();
        // Arrange
        String isbn = "1234567890"; // Example ISBN
        int rating = 5;
        String review = "Great book!";
        when(bookRepository.findByISBN(isbn)).thenReturn(Optional.of(testBook));

        // Act & Assert
        mockMvc.perform(post("/add-review")
                        .param("rating", String.valueOf(rating))
                        .param("review", review)
                        .param("ISBN", isbn)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book-details?ISBN=" + isbn));

        // Verify that the repository's save method was called
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    public void testAddReview_BookDoesNotExist() throws Exception {
        // Arrange
        String isbn = "1234567890"; // Example ISBN
        int rating = 5;
        String review = "Great book!";
        when(bookRepository.findByISBN(isbn)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/add-review")
                        .param("rating", String.valueOf(rating))
                        .param("review", review)
                        .param("ISBN", isbn)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home-page"));

        // Verify that the repository's save method was not called
        verify(bookRepository, times(0)).save(any(Book.class));
    }

}
