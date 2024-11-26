package com.SYSC4806;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private LoginService loginService;
    private final MeterRegistry meterRegistry;
    private Counter invalidCredentials;


    public LoginController(@Autowired LoginService loginService, MeterRegistry meterRegistry) {
        this.loginService = loginService;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Initializes the invalidCredentials counter after the dependencies are injected.
     */
    @PostConstruct
    public void init() {
        invalidCredentials = meterRegistry.counter("login.invalid.credentials");
    }


    /**
     * Handles the GET request to display the login page.
     *
     * Returns the name of the template for the login page, allowing the user
     * to enter their credentials.
     *
     * @return template name for the login page
     */
    @GetMapping("/login")
    public String showLoginPage() {return "login-page";}


    /**
     * Handles the POST request for user login by verifying the provided credentials.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return redirect to the home page if login is successful; otherwise, redirect to the login page.
     */
    @PostMapping("/login")
    public String login(@RequestParam(name="username")String username, @RequestParam(name="password")String password, HttpSession session) {

        LoginService.Response response = loginService.authenticate(username, password);
        switch (response) {
            case ADMIN_LOGIN_SUCCESS -> {
                session.setAttribute("username", username);
                return "redirect:/inventory";
            }
            case CUSTOMER_LOGIN_SUCCESS -> {
                session.setAttribute("username", username);
                return "redirect:/home";
            }
            default -> {
                // Datadog: increment invalid credentials counter
                invalidCredentials.increment();
                return "redirect:/login";
            }
        }
    }
}
