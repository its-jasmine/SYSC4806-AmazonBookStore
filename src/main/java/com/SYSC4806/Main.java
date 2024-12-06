package com.SYSC4806;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import java.util.*;

@SpringBootApplication
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    @Profile("!test") // This bean will not run when the "test" profile is active
    public CommandLineRunner demo(BookRepository bookRepository, CustomerRepository customerRepository, AdminRepository adminRepository) {
        return (args) -> {

            List<Book> demoBooks = new BookGenerator().populateBooksFromOpenLibrary();
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

            demoCustomerAccounts[0].addToHistory(demoBooks.get(1));
            demoCustomerAccounts[0].addToHistory(demoBooks.get(2));
            demoCustomerAccounts[0].addToHistory(demoBooks.get(4));
            demoCustomerAccounts[0].addToHistory(demoBooks.get(6));

            demoCustomerAccounts[1].addToHistory(demoBooks.get(1));
            demoCustomerAccounts[1].addToHistory(demoBooks.get(3));
            demoCustomerAccounts[1].addToHistory(demoBooks.get(4));
            demoCustomerAccounts[1].addToHistory(demoBooks.get(6));
            demoCustomerAccounts[1].addToHistory(demoBooks.get(7));
            demoCustomerAccounts[1].addToHistory(demoBooks.get(9));

            demoCustomerAccounts[2].addToHistory(demoBooks.get(2));
            demoCustomerAccounts[2].addToHistory(demoBooks.get(3));
            demoCustomerAccounts[2].addToHistory(demoBooks.get(6));
            demoCustomerAccounts[2].addToHistory(demoBooks.get(10));


            //test recommendation
            BookRecommendation bookRecommendation = new BookRecommendation(Arrays.stream(demoCustomerAccounts).toList());
            log.info("These are recommendations for customer 1");

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

            //ADDING TO CART:
//            demoCustomerAccounts[0].addToCart(bookRepository.findByISBN("1000000000").get());
//            demoCustomerAccounts[0].addToCart(bookRepository.findByISBN("1000000000").get());
//            demoCustomerAccounts[0].addToCart(bookRepository.findByISBN("1000000001").get());
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
