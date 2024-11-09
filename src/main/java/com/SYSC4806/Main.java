package com.SYSC4806;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
import java.util.Optional;

@SpringBootApplication
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner demo(BookRepository bookRepository, AppUserRepository userRepository) {
        return (args) -> {
            bookRepository.save(new Book("Book1", "author1", "pub1", "mystery", 1));
            bookRepository.save(new Book("Book2", "author2", "pub2", "fantasy", 4));
            bookRepository.save(new Book("Book3", "author3", "pub3", "horror", 6));

            // Fetch all books
            log.info("Books found with findAll():");
            log.info("-------------------------------");
            bookRepository.findAll().forEach(book -> log.info(book.toString()));
            log.info("");

            // Fetch an individual book  by ID
            bookRepository.findById(2).ifPresent(book -> {
                log.info("Book found with findById(2):");
                log.info("--------------------------------");
                log.info(book.toString());
                log.info("");
            });

            // Fetch book by title
            log.info("Buddy found with findByName('Book1'):");
            log.info("--------------------------------------------");
            bookRepository.findByTitle("Book1").forEach(book -> log.info(book.toString()));
            log.info("");


            ////////////////////////////////////////////////////////////////////////////////////
            userRepository.save(new AppUser("admin1", "password"));
            userRepository.save(new Admin("admin2", "password1"));
            Customer customer = new Customer("customer1", "password2");

            ArrayList<Book> books = new ArrayList<>();
            books.add(new Book("Book1", "author1", "pub1", "mystery", 1));
            books.add(new Book("Book2", "author2", "pub2", "fantasy", 6));
            customer.setBooks(books);

            userRepository.save(customer);

            // Fetch all users
            log.info("User found with findAll():");
            log.info("-------------------------------");
            userRepository.findAll().forEach(user-> log.info(user.toString()));
            log.info("");

            // Fetch an individual user by ID
            userRepository.findById(2).ifPresent(user-> {
                log.info("User found with findById(2):");
                log.info("--------------------------------");
                log.info(user.toString());
                log.info("");
            });

            // Fetch user by name
            log.info("user found with findByUsername('admin1'):");
            log.info("--------------------------------------------");
            Optional<AppUser> user = userRepository.findByUsername("admin1");
            if (user.isPresent()) {
                log.info(user.get().toString());
                log.info("");
            } else {
                throw new RuntimeException("Expected to find user with wusername 'admin1'.");
            }




        };

    }
}
