package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
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

    BookRecommendation recommendation;

    /**
     * Handles the GET request to show the home page
     *
     * @return template name for home page
     */
    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }

        model.addAttribute("genres", Book.Genre.values());

        Iterable<Book> books = bookRepository.findTop10ByOrderByDateAddedDesc();
        model.addAttribute("newReleases",books);

        books = bookRepository.findTop10ByOrderByNumCopiesSoldDesc(); // top 10 best sellers
        model.addAttribute("bestSellers", books);

        if (username != null) {
            this.recommendation = new BookRecommendation((List<Customer>) customerRepository.findAll());
            Optional<Customer> optionalCustomer = customerRepository.findCustomerByUsername(username);

            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                model.addAttribute("recSize", recommendation.getRecommendation(customer).size());
                model.addAttribute("recommendation", recommendation.getRecommendation(customer));
            }
        }

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
    public String showInventoryPage(Model model, HttpSession session) {
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
                          @RequestParam(name="numCopies")int numCopies, RedirectAttributes redirectAttributes) { // TODO make use of form object

        // Find the book by title and author, if exists simply update copies
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        Book bookToSave;

        if (book.isPresent()) {
            // If the book exists, add a flash message and redirect
            redirectAttributes.addFlashAttribute("errorMessage", "Book already exists in the inventory.");
            return "redirect:/book-management";
        }

        bookToSave = new Book(ISBN, title, author, publisher, price, genre, numCopies);
        bookRepository.save(bookToSave);
        return "redirect:/inventory";
    }

    /**
     * Handles the POST request to remove a book by its ID.
     *
     * Deletes the book with the specified ID from the repository
     * and then redirects to the home page.
     *
     * @param ISBN The ISBN of the book to be removed
     * @return redirect to home page template
     */
    @PostMapping("/remove-books")
    public String removeBook(@RequestParam(name="ISBN") String ISBN, RedirectAttributes redirectAttributes) {
        // Find the book by title and author and delete it if exists
        Optional<Book> bookToRemove = bookRepository.findByISBN(ISBN);
        if (bookToRemove.isPresent()) {
            bookRepository.delete(bookToRemove.get());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found, cannot remove.");
            return "redirect:/book-management";
        }
        return "redirect:/inventory";
    }

    /**
     * Handles the POST request to update a book by its ID.
     *
     * Updates the book with the specified ID from the repository
     *
     * @param ISBN The ISBN of the book to be updated
     * @return inventory page
     */
    @PostMapping("/update-books")
    public String updateBook(@RequestParam(name="ISBN") String ISBN, @RequestParam(name="numCopies")int numCopies,
                             RedirectAttributes redirectAttributes) {
        // Find the book by title and author and delete it if exists
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setNumCopiesInStock(numCopies);
            bookRepository.save(existingBook);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found, cannot remove.");
            return "redirect:/book-management";
        }
        return "redirect:/inventory";
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
    /**
     * Handles the GET request to show page with the details for a selected book and allow user to add to cart.
     *
     * @return template name for book-details
     */
    @GetMapping("/book-details")
    public String showBookDetailsPage(@RequestParam(name="ISBN")String ISBN, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }
        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("reviews", book.get().getReviews());
            return "book-details";
        }
        return "home-page";

    }

    @PostMapping("/add-review")
    public String addReview(@RequestParam(name="rating")int rating, @RequestParam(name="review")String review, @RequestParam(name="ISBN")String ISBN, HttpSession session, Model model) {
        String username = (session.getAttribute("username") != null) ? (String) session.getAttribute("username") : "Anonymous";

        Optional<Book> book = bookRepository.findByISBN(ISBN);
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.addReview(review, rating, username);
            bookRepository.save(existingBook);
        } else {
            return "redirect:/home-page";
        }
        return "redirect:/book-details?ISBN=" + ISBN;
    }

}
