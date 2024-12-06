package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

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
        model.addAttribute("wishlist", customer.getWishlist()); //Add the wishlist
        return "profile";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session to log out the user
        session.invalidate();
        // Redirect the user to the login page after logging out
        return "redirect:/home";
    }

    @PostMapping("/wishlist/add")
    public String addToWishlist(@RequestParam("bookId") String bookISBN, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Book> book = bookRepository.findByISBN(bookISBN);
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if the user is not logged in
        }

        if (book.isPresent()) {
            Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);

            if (customer == null) {
                return "redirect:/login"; // Redirect if customer not found
            }

            // Add the book to the customer's wishlist
            if(customer.addToWishlist(book.get())){
                // Save the updated customer back to the database
                customerRepository.save(customer);

                // Add success message as a flash attribute
                redirectAttributes.addFlashAttribute("success", "Item added to wishlist successfully!");
                return "redirect:/book-details?ISBN=" + bookISBN; // Redirect to the book details page
            }
            else{
                // Add success message as a flash attribute
                redirectAttributes.addFlashAttribute("success", "Item already in wishlist!");
                return "redirect:/book-details?ISBN=" + bookISBN; // Redirect to the book details page
            }


        }

        // Add error message as a flash attribute if the book is not found
        redirectAttributes.addFlashAttribute("error", "Book not found.");
        return "redirect:/home"; // Redirect to the home page if the book doesn't exist
    }

    @PostMapping("/wishlist/remove")
    public String removeFromWishlist(@RequestParam("bookId") String bookISBN, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if the user is not logged in
        }

        Optional<Book> book = bookRepository.findByISBN(bookISBN);
        if (book.isPresent()) {
            Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);

            if (customer == null) {
                return "redirect:/login"; // Redirect if customer not found
            }

            // Remove the book from the customer's wishlist
            if (customer.removeFromWishlist(book.get())) {
                customerRepository.save(customer); // Save changes to the database
                redirectAttributes.addFlashAttribute("success", "Item removed from wishlist successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Item was not found in your wishlist!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
        }

        return "redirect:/profile"; // Redirect back to the profile page
    }



}