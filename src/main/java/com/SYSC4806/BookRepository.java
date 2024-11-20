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

    Optional<Book> findByTitleAndAuthor(String title, String author);
}

