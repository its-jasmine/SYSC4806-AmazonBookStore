package com.SYSC4806;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private LoginService loginService;

    public LoginController(@Autowired LoginService loginService) {
        this.loginService = loginService;
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

        if (loginService.authenticate(username, password)) {
            session.setAttribute("username", username);
            return "redirect:/home"; // TODO /inventory for admin, /home for customers
        }
        return "redirect:/login";
    }
}
