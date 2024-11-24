package com.SYSC4806;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    private Book getBookById(Integer id, BookRepository bookRepository) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Book with ID " + id + " not found"));
    }

    @Bean
    @Profile("!test") // This bean will not run when the "test" profile is active
    public CommandLineRunner demo(BookRepository bookRepository, CustomerRepository customerRepository, AdminRepository adminRepository) {
        return (args) -> {
            // Generating 100 random books to populate the book store
            // Bounds were selected such that some books may have the same author, publisher, and/or genre.
            // Book names however, are all unique (however this isn't strictly required by the system, i.e., the combination of all book values is what must be unique)
            Random random = new Random();
            List<Book> demoBooks = new ArrayList<>();
            Book.Genre[] genres = Book.Genre.values();
            int baseISBN = 1000000000;
            double cents = 0.99;
            Book demoBook;
            for (int i = 0; i<100; i++) {
                demoBook = new Book((baseISBN+i)+"","Book" + i, "author" + random.nextInt(90), "pub" + random.nextInt(5), cents + ((random.nextInt(40) + 10)),genres[random.nextInt(genres.length)], random.nextInt(100));
                demoBook.setNumCopiesSold(random.nextInt(10000));
                demoBook.setDateAdded(LocalDateTime.now().plusDays(random.nextInt(365)));
                demoBooks.add(demoBook);
            }
            demoBooks.add(new Book((baseISBN+400+""), "Harry Potter", "J.K Rowling", "publisher", 45.98, genres[5], 17));
            demoBooks.forEach(book -> bookRepository.save(book));

            // Fetch all books
            log.info("Books found with findAll():");
            log.info("-------------------------------");
            bookRepository.findAll().forEach(book -> log.info(book.toString()));
            log.info("");

            // Fetch an individual book  by ID
            bookRepository.findById(2).ifPresent(book -> {
                log.info("Book found with findById(2):");
                log.info("---------------------------------");
                log.info(book.toString());
                log.info("");
            });

            // Fetch book by title
            log.info("Buddy found with findByName('Book1'):");
            log.info("--------------------------------------------");
            bookRepository.findByTitle("Book1").forEach(book -> log.info(book.toString()));
            log.info("");

            // Fetch top 10 best seller books
            log.info("Top 10 Bestseller Books found with findTop10ByOrderByNumCopiesSoldDesc():");
            log.info("-------------------------------");
            bookRepository.findTop10ByOrderByNumCopiesSoldDesc().forEach(book -> log.info(book.toString()));
            log.info("");


            ////////////////////////////////////////////////////////////////////////////////////
            Admin[] demoAdminAccounts = {
                    new Admin("admin1", "password"),
                    new Admin("admin2", "password1")

            };
            Customer[] demoCustomerAccounts = {
                    new Customer("customer1", "password2"),
                    new Customer("customer2", "password3"),
                    new Customer("customer3", "password4")
            };

            try {
                demoCustomerAccounts[0].addToHistory(getBookById(1, bookRepository));
                demoCustomerAccounts[0].addToHistory(getBookById(2, bookRepository));
                demoCustomerAccounts[0].addToHistory(getBookById(4, bookRepository));
                demoCustomerAccounts[0].addToHistory(getBookById(6, bookRepository));

                demoCustomerAccounts[1].addToHistory(getBookById(1, bookRepository));
                demoCustomerAccounts[1].addToHistory(getBookById(3, bookRepository));
                demoCustomerAccounts[1].addToHistory(getBookById(4, bookRepository));
                demoCustomerAccounts[1].addToHistory(getBookById(6, bookRepository));
                demoCustomerAccounts[1].addToHistory(getBookById(7, bookRepository));
                demoCustomerAccounts[1].addToHistory(getBookById(9, bookRepository));

                demoCustomerAccounts[2].addToHistory(getBookById(2, bookRepository));
                demoCustomerAccounts[2].addToHistory(getBookById(3, bookRepository));
                demoCustomerAccounts[2].addToHistory(getBookById(6, bookRepository));
                demoCustomerAccounts[2].addToHistory(getBookById(10, bookRepository));
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage()); // Log missing book
            }

            //test recommendation
            BookRecommendation bookRecommendation = new BookRecommendation(Arrays.stream(demoCustomerAccounts).toList());
            log.info("These are recommendations for customer 1");
            for (Book book: demoCustomerAccounts[0].getPurchaseHistory()) {
                log.info(book.toString());
            }
            log.info("should be 3 7 9");
            for (Book book: bookRecommendation.getRecommendation(demoCustomerAccounts[0])) {
                log.info(book.toString());
            }

            adminRepository.saveAll(Arrays.asList(demoAdminAccounts));


            /* // TODO when saving customer books works properly (currently, it causes an error because it tries to save book again)
            List<Book> books = Arrays.asList(demoBooks[0], demoBooks[1]);
            Customer customer = demoCustomerAccounts[0];
            customer.setBooks(books);

            books = Arrays.asList(demoBooks[2]);
            customer = demoCustomerAccounts[1];
            customer.setBooks(books);*/

//            customerRepository.save(demoCustomerAccounts[0]);
            customerRepository.saveAll(Arrays.asList(demoCustomerAccounts));


            // Fetch all users
            log.info("Customers found with findAll():");
            log.info("-------------------------------");
            customerRepository.findAll().forEach(user-> log.info(user.toString()));
            log.info("");

            // Fetch all users
            log.info("Admins found with findAll():");
            log.info("-------------------------------");
            adminRepository.findAll().forEach(user-> log.info(user.toString()));
            log.info("");

            // Fetch an individual user by ID
            adminRepository.findById(2).ifPresent(user-> {
                log.info("User found with findById(2):");
                log.info("--------------------------------");
                log.info(user.toString());
                log.info("");
            });

            // Fetch user by name
            log.info("user found with findByUsername('admin1'):");
            log.info("--------------------------------------------");
            Optional<Admin> user = adminRepository.findAdminByUsername("admin1");
            if (user.isPresent()) {
                log.info(user.get().toString());
                log.info("");
            }

            log.info("user found with findByUsername('customer1'):");
            log.info("--------------------------------------------");
            Optional<Customer> user2 = customerRepository.findCustomerByUsername("customer1");
            if (user2.isPresent()) {
                log.info(user2.get().toString());
                log.info("");
            }
        };

    }
}
