package com.SYSC4806;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {

    Iterable<Object> findByTitle(String title);

    Optional<Book> findByTitleAndAuthor(String title, String author);
}

