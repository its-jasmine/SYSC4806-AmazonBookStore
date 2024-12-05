package com.SYSC4806;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    /**
     * Handles the GET request to display the register page.
     *
     * Returns the name of the template for the register page, allowing the user
     * to enter their proposed credentials.
     *
     * @return template name for the register page
     */
    @GetMapping("/register")
    public String showRegisterPage() {return "register-page";}


    /**
     * Handles the POST request for user register by verifying the provided username does not already exist.
     *
     * If the provided username already exists, an error message becomes visible on the register page.
     * If the username and password are not null, the account gets added to the user repository,
     * and the user becomes logged-in and is redirected to the home page.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return redirect to the home page if registration is successful; otherwise, redirect to the register page.
     */
    @PostMapping("/register")
    public String register(@RequestParam(name="username")String username, @RequestParam(name="password")String password,
                           Model model, HttpSession session) {

        RegisterService.Response response = registerService.register(username, password);

        switch (response) {
            case FAILED -> {return "redirect:/register";}
            case INVALID_USERNAME -> {
                model.addAttribute("error", "Username already exists. Please choose another one.");
                return "register-page";
            }
            case SUCCESS -> {
                session.setAttribute("username", username);
                return "redirect:/home";
            }
            default ->  throw new RuntimeException("Unexpected response from registration service: " + response.name());
        }
    }
}
