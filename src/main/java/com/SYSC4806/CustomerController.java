package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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

        if (username != null) {
            model.addAttribute("username", username); // Add username to model
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
     * @param bookId The ID of the book to remove
     * @param session The current HTTP session to identify the customer.
     * @return redirect to the cart page after removal.
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("bookId") String bookId, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        // Retrieve customer by username
        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            return "redirect:/login"; // Redirect if customer not found
        }

        customer.removeFromCart(bookId);

        customerRepository.save(customer); // Save the updated customer/cart


        return "redirect:/cart"; // Redirect back to the cart page
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

    @PostMapping("/cart/update")
    public String updateCartQuantity(@RequestParam("bookId") String bookId,
                                     @RequestParam("quantity") int quantity,
                                     HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        // Retrieve customer by username
        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            return "redirect:/login"; // Redirect if customer not found
        }

        // Find the book in the cart and update its quantity
        Map<Book, Integer> cart = customer.getCart();
        Book bookToUpdate = cart.keySet()
                .stream()
                .filter(book -> book.getISBN().equals(bookId))
                .findFirst()
                .orElse(null);

        if (bookToUpdate != null && quantity > 0) {
            cart.put(bookToUpdate, quantity); // Update the quantity
            customerRepository.save(customer); // Save the updated customer/cart
        }

        return "redirect:/cart"; // Redirect back to the cart page
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }
        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            return "redirect:/login"; // Redirect if customer not found
        }

        model.addAttribute("customer", customer); // Add the customer object to the model
        model.addAttribute("purchaseHistory", customer.getPurchaseHistory()); // Add the purchase history
        return "profile";

    }


}