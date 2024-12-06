package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CustomerRepository customerRepository;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, CustomerRepository customerRepository) {
        this.checkoutService = checkoutService;
        this.customerRepository = customerRepository;
    }

    /**
     * Displays the checkout page with cart items.
     *
     * @param session The current HTTP session to identify the customer.
     * @param model   The model to pass data to the view.
     * @return template name for checkout page.
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }

        Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
        if (customer == null) {
            return "redirect:/login"; // Redirect if customer not found
        }

        Map<Book, Integer> cart = customer.getCart();
        double subTotal = cart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        double tax = subTotal * 0.13;
        double total = subTotal + tax;

        // Use BigDecimal for precise rounding
        model.addAttribute("cart", cart); // Pass the cart to the view
        model.addAttribute("cartSubTotal", new BigDecimal(subTotal).setScale(2, RoundingMode.HALF_UP)); // Pass the total price to the view
        model.addAttribute("tax", new BigDecimal(tax).setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("cartTotal", new BigDecimal(total).setScale(2, RoundingMode.HALF_UP));

        return "checkout-page";
    }


    /**
     * Processes the checkout request.
     *
     * @param bookids   List of books to purchase.
     * @param cardNumber The card number for payment.
     * @param expiry     The expiry date of the card.
     * @param cvv        The CVV code of the card.
     * @param session    The current HTTP session to identify the customer.
     * @return redirect to success page or back to checkout on failure.
     */
    @PostMapping("/checkout")
    public String handleCheckout(
            @RequestParam("bookIds") List<String> bookids,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("expiry") String expiry,
            @RequestParam("cvv") String cvv,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            model.addAttribute("error", "You must log in to complete the checkout.");
            return "redirect:/checkout?error=true";
        }

        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }



        boolean success = checkoutService.checkout(username, bookids, cardNumber, expiry, cvv);
        if (success) {
            Customer customer = customerRepository.findCustomerByUsername(username).orElse(null);
            if (customer != null) {
                customer.checkout();
                customerRepository.save(customer);
            }
            return "redirect:/checkout-success";
        }

        model.addAttribute("error", "Checkout failed. Please check your payment details or cart.");
        redirectAttributes.addFlashAttribute("error", "Checkout failed. Please check your payment details or cart.");
        return "redirect:/checkout?error=true";
    }


    @GetMapping("/checkout-success")
    public String showSuccessPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        if (username != null) {
            model.addAttribute("username", username); // Add username to model
        }
        return "checkout-success"; // Renders checkout-success.html
    }
}
