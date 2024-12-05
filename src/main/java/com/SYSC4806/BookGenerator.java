package com.SYSC4806;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Class responsible for generating a list of books by fetching data from the Open Library API
 * and constructing {@link Book} objects from the response.
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
public class BookGenerator {
    /**
     * Fetches book data from the Open Library API and populates a list of {@link Book} objects.
     * @return a list of books populated with data from the Open Library API.
     */
    public List<Book> populateBooksFromOpenLibrary() {
        String subject = "young+adult";
        String sortFilter = "rating+desc";

        /// Generating 50 books from one query and picking random genres
         return parseResponse(OpenLibraryAPIService.fetchSearchResponse(subject, 50, sortFilter));

        /// Generating books for 10 books in each genre
        // Downside, more queries, slightly slower?
        /*
        int booksPerGenre = 10;
        String apiUrl;
        List<Book> books = new ArrayList<>();

        for (Book.Genre genre : Book.Genre.values()) {
            apiUrl = baseUrl+"?" + "subject=" + genre.name().toLowerCase() + "&" + "limit=" + booksPerGenre + "&" + fields + "&" + languageFilter + "&" + sortFilter;
            System.out.println(apiUrl);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            processResponse(books, genre, response);
            return books;
        }*/

    }
    /**
     * Parses the JSON response from the Open Library API to extract book information
     * and construct a list of books
     *
     * @param response containing the JSON from the Open Library API.
     * @return a list of books parsed from the response.
     */
    private List<Book> parseResponse(ResponseEntity<String> response){
        Book book;
        List<Book> books = new ArrayList<>();
        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            JsonNode docs = root.get("docs");

            if (docs != null) {
                for (JsonNode bookNode : docs) {
                    book = extractAndConstructBook(bookNode);
                    if (book != null) books.add(book);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
    /**
     * Extracts book data from JSON and constructs a book.
     * Only constructs the book if all required fields are present and valid.
     *
     * @param bookNode the JSON node representing a single book from the API response.
     * @return a book constructed from the data, or {@code null} if required fields are missing.
     */
    private Book extractAndConstructBook(JsonNode bookNode) {
        // Validate that all required fields are present
        if (bookNode.has("cover_i") && bookNode.has("key") && bookNode.has("isbn")
                && bookNode.has("title") && bookNode.has("author_name") && bookNode.has("publisher")) {

            // Extract required fields directly
            String isbn = bookNode.get("isbn").get(0).asText();
            String title = bookNode.get("title").asText();
            String author = bookNode.get("author_name").get(0).asText();
            String publisher = bookNode.get("publisher").get(0).asText();
            String imageId = bookNode.get("cover_i").asText();
            String workId = bookNode.get("key").asText();

            // Generate random values
            double price = 10.99 + Math.random() * 20; // Random price
            int stock = 10 + (int) (Math.random() * 50); // Random stock

            // Construct and populate the Book object

            Book book;
            try {
               book = new Book(isbn, title, author, publisher, price, getRandomGenre(), stock);
            } catch (IllegalArgumentException e){
                return null;
            }
            book.setNumCopiesSold((int) (Math.random() * 5000));
            book.setDateAdded(LocalDateTime.now().minusDays((int) (Math.random() * 365)));
            book.setImageId(imageId);
            book.setWorkId(workId);

            return book;
        } else {
            // If any required field is missing, return null or handle it appropriately
            return null;
        }
    }

    /**
     * Generates a random {@link Book.Genre} from the available genres.
     * @return a randomly selected {@link Book.Genre}.
     */
    private Book.Genre getRandomGenre() {
        Book.Genre[] genres = Book.Genre.values();
        return genres[(int) (Math.random() * genres.length)];
    }
}
