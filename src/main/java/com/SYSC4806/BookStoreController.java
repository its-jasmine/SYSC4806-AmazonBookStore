package com.SYSC4806;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    BookRepository bookRepository;

    /**
     * Handles the GET request to display the login page.
     *
     * Returns the name of the template for the login page, allowing the user
     * to enter their credentials.
     *
     * @return template name for the login page
     */
    @GetMapping("/login")
    public String showLoginPage() {return "login-page";}


    /**
     * Handles the POST request for user login by verifying the provided credentials.
     *
     * Retrieves the user by username, checks if the provided password matches,
     * and redirects to the home page if successful. If the login fails, it redirects back
     * to the login page.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return redirect to the home page if login is successful; otherwise, redirect to the login page.
     */
    @PostMapping("/login")
    public String login(@RequestParam(name="username")String username, @RequestParam(name="password")String password) {

        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            return "redirect:/home"; // redirects to home page of
        }
        return "redirect:/login";
    }

    /**
     * Handles the GET request to show the home page, which displays a list of all books.
     *
     * Retrieves all books from the repository and adds them to the model
     * to be displayed on the home page.
     *
     * @param model the Model object to hold attributes for the view
     * @return template name for home page
     */
    @GetMapping("/home")
    public String showHomePage(Model model) {
        Iterable<Book> books = bookRepository.findAll();
        model.addAttribute("books",books);
        return "home-page";
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
    @PostMapping("/book")
    public String addBook(@RequestParam(name="title")String title, @RequestParam(name="author")String author,
                          @RequestParam(name="publisher")String publisher, @RequestParam(name="genre")String genre,
                          @RequestParam(name="numCopies")int numCopies) {

        Book newBook = new Book(title, author, publisher, genre, numCopies);
        bookRepository.save(newBook);

        return "redirect:/home";
    }
    /**
     * Handles the DELETE request to remove a book by its ID.
     *
     * Deletes the book with the specified ID from the repository
     * and then redirects to the home page.
     *
     * @param id The ID of the book to be removed.
     * @return redirect to home page template
     */
    @DeleteMapping("/book")
    public String removeBook(@RequestParam(name="id")Integer id) {
        bookRepository.deleteById(id);
        return "redirect:/home";
    }
}
