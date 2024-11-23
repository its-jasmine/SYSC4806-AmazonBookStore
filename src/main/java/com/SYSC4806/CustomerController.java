package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, BookRepository bookRepository) {
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
    }


    /**
     * Displays the customer's cart.
     *
     * @param session The current HTTP session to identify the customer.
     * @param model   The model to pass data to the view.
     * @return template name for cart page.
     */
    @GetMapping("/cart")
    public String showCartPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        // Retrieve customer by username
        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            return "redirect:/login"; // Redirect to login if customer not found
        }

        // Get the cart books from the customer
        model.addAttribute("cart", customer.getCart());
        return "cart";
    }


    /**
     * Removes a book from the customer's cart.
     *
     * @param bookISBN  The ID of the book to remove.
     * @param quantity The current quantity of the book
     * @param session The current HTTP session to identify the customer.
     * @return redirect to the cart page after removal.
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("bookId") String bookISBN, @RequestParam("quantity") Integer quantity, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer != null) {
            Book book = bookRepository.findByISBN(bookISBN).orElse(null);
            if (book != null) {
                customer.removeFromCart(book, quantity); // Remove or decrement quantity
                customerRepository.save(customer); // Save updated customer
            }
        }

        return "redirect:/cart";
    }

    /**
     * Add the requested book to the customer's cart
     * @param bookISBN the book to add
     * @param session the current HTTP session to identify the customer
     * @return the cart display page
     */
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("bookId") String bookISBN, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        Customer customer = customerRepository.findCustomerByUsername(username).orElseThrow();
        Book book = bookRepository.findByISBN(bookISBN).orElseThrow();

        customer.addToCart(book); // Adds the book or increments its quantity
        customerRepository.save(customer); // Persist changes to the cart
        return "redirect:/cart";
    }

}