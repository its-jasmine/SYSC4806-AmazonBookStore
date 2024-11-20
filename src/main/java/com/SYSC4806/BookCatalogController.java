package com.SYSC4806;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class responsible for handling web requests and responses for endpoints relating to searching and filtering within the book catalog.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Controller
public class BookCatalogController {
    @Autowired
    BookRepository bookRepository;
    /**
     * Handles the GET request to handle showing catalog of books page
     *
     * @return template name for book-browsing
     */
    @GetMapping("/book-catalog")
    public String showBookCatalogPage(Model model) {
        model.addAttribute("genres", Book.Genre.values());

        Iterable<Book> books = bookRepository.findTop10ByOrderByDateAddedDesc();
        model.addAttribute("newReleases",books);

        books = bookRepository.findTop10ByOrderByNumCopiesSoldDesc(); // top 10 best sellers
        model.addAttribute("bestSellers", books);
        return "book-catalog";
    }
}
