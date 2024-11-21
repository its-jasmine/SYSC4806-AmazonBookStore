package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller class responsible for handling web requests and responses for endpoints relating to the book store web application.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Controller
public class BookStoreController {
    @Autowired
    AppUserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Handles the GET request to show the home page
     *
     * @return template name for home page
     */
    @GetMapping("/home")
    public String showHomePage() {
        return "home-page";
    }

    /**
     * Handles the GET request to show the inventory page, which displays a list of all books.
     *
     * Retrieves all books from the repository and adds them to the model
     * to be displayed on the home page.
     *
     * @param model the Model object to hold attributes for the view
     * @return template name for home page
     */
    @GetMapping("/inventory")
    public String showHomePage(Model model, HttpSession session) {
        Iterable<Book> books = bookRepository.findAll();
        model.addAttribute("books",books);

        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }
        return "inventory-page";
    }

    /**
     * Handles the POST request to add a new book to the repository.
     * This method receives book details as parameters, creates a new Book object,
     * saves it to the repository, and then redirects to the home page.
     *
     * @param title of the new book
     * @param author of the new book
     * @param publisher of the new book
     * @param genre of the new book
     * @param numCopies in stock
     * @return redirect to home page template
     */
    @PostMapping("/books")
    public String addBook(@RequestParam(name="ISBN")String ISBN, @RequestParam(name="title")String title,
                          @RequestParam(name="author")String author, @RequestParam(name="publisher")String publisher,
                          @RequestParam(name="price")double price, @RequestParam(name="genre") Book.Genre genre,
                          @RequestParam(name="numCopies")int numCopies) { // TODO make use of form object

        // Find the book by title and author, if exists simply update copies
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        Book bookToSave;
        if (book.isPresent()){
            Book existingBook = book.get();
            existingBook.setNumCopiesInStock(existingBook.getNumCopiesInStock() + numCopies);
            bookToSave = existingBook;
        } else {
            bookToSave = new Book(ISBN,title, author, publisher, price, genre, numCopies);
        }

        bookRepository.save(bookToSave);
        return "redirect:/home";
    }

    /**
     * Handles the DELETE request to remove a book by its ID.
     *
     * Deletes the book with the specified ID from the repository
     * and then redirects to the home page.
     *
     * @param ISBN The ISBN of the book to be removed
     * @return redirect to home page template
     */
    @PostMapping("/remove-books")
    public String removeBook(@RequestParam(name="ISBN") String ISBN) {
        // Find the book by title and author and delete it if exists
        Optional<Book> bookToRemove = bookRepository.findByISBN(ISBN);
        if (bookToRemove.isPresent()) {
            Book book = bookToRemove.get();
            if (book.getNumCopiesInStock() > 0) {
                book.setNumCopiesInStock(book.getNumCopiesInStock() - 1);
                bookRepository.save(book);
            }
        }

        return "redirect:/home";
    }

    /**
     * Handles the GET request to handle showing the adding/removing books page
     *
     * Shows a form for adding a new book to the system as well as removing books
     *
     * @return template name for book-management
     */
    @GetMapping("/book-management")
    public String showBookManagementPage(Model model) {
        model.addAttribute("genres", Book.Genre.values());
        return "book-management";
    }
}
