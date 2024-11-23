package com.SYSC4806;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {

    List<Book> findByTitle(String title);

    /**
     * Custom query for top 10 bestselling books in descending order.
     * @return top 10 best selling books
     */
    List<Book> findTop10ByOrderByNumCopiesSoldDesc();
    /**
     * Custom query for 10 books that have most recently been added to the book store.
     * @return the 10 newest releases
     */
    List<Book> findTop10ByOrderByDateAddedDesc();

    Optional<Book> findByISBN(String ISBN);
    List<Book> findByGenre(Book.Genre genre);

    /**
     * Find books where the title contains the specified substring (case insensitive).
     * @param title the substring to search for in book titles.
     * @return a list of books matching the criteria.
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Find books where the author's name contains the specified substring (case insensitive).
     * @param author the substring to search for in author names.
     * @return a list of books matching the criteria.
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);


}

