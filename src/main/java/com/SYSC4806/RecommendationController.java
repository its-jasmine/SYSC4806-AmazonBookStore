package com.SYSC4806;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecommendationController {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BookRecommendation recommendation;

    /**
     * Constructor for RecommendationController
     * @param customerRepository The database of Customers
     */
    @Autowired
    public RecommendationController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Handles the GET request to display the recommendations for a user
     *
     * Returns the list of recommended books
     * @return list of recommended books
     */
    @GetMapping("/book-recommendation")
    public String showRecommendation(@RequestParam(name="username")String username, Model model) {
        this.recommendation = new BookRecommendation((List<Customer>) customerRepository.findAll());
        Customer customer = customerRepository.findCustomerByUsername(username).orElseThrow(
                () -> new RuntimeException("Customer not found")
        );
        model.addAttribute("recommendation", recommendation.getRecommendation(customer));
        model.addAttribute("customer", username);
        return "book-recommendation";
    }

}
