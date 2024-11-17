package com.SYSC4806;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

            Book[] demoBooks = {
                    new Book("Book1", "author1", "pub1", Book.Genre.Mystery, 1),
                    new Book("Book2", "author2", "pub2", Book.Genre.Fantasy, 4),
                    new Book("Book3", "author3", "pub3", Book.Genre.NonFiction, 6)
            };
            Arrays.stream(demoBooks).forEach(book -> bookRepository.save(book));

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
            AppUser[] demoAdminAccounts = {
                    new Admin("admin1", "password"),
                    new Admin("admin2", "password1")

            };
            Customer[] demoCustomerAccounts = {
                    new Customer("customer1", "password2"),
                    new Customer("customer2", "password3")
            };
            Arrays.stream(demoAdminAccounts).forEach(account -> userRepository.save(account));

            /* // TODO when saving customer books works properly (currently, it causes an error because it tries to save book again)
            List<Book> books = Arrays.asList(demoBooks[0], demoBooks[1]);
            Customer customer = demoCustomerAccounts[0];
            customer.setBooks(books);

            books = Arrays.asList(demoBooks[2]);
            customer = demoCustomerAccounts[1];
            customer.setBooks(books);*/

            Arrays.stream(demoCustomerAccounts).forEach(account -> userRepository.save(account));

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
            }
        };

    }
}
