package com.SYSC4806;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Controller class responsible for handling web requests and responses for endpoints relating to the book filtering.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Controller
public class BookFilteringController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * Handles the GET request to show a filtered list of books in a particular genre.
     *
     * @return template name for search-results page
     */
    @GetMapping("/search-results")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        // Search for books by title & author
        List<Book> booksTitle = bookRepository.findByTitleContainingIgnoreCase(query);
        List<Book> booksAuthor = bookRepository.findByAuthorContainingIgnoreCase(query);

        List<Book> books = Stream.concat(booksTitle.stream(), booksAuthor.stream()).distinct().collect(Collectors.toList());

        model.addAttribute("searchResults", books);
        model.addAttribute("filter", query);

        // Return the search results page
        return "search-results";
    }

    /**
     * Handles the GET request to show a filtered list of books in a particular genre.
     *
     * @return template name for search-results page
     */
    @GetMapping("/browse-by-genre")
    public String showBooksInGenrePage(@RequestParam(name="genre") Book.Genre genre, Model model) {
        List<Book> booksInGenre = bookRepository.findByGenre(genre);
        model.addAttribute("searchResults", booksInGenre);
        model.addAttribute("filter", genre);
        return "search-results";
    }
}
