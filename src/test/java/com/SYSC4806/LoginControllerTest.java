package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private LoginService loginService;

    @Test
    void showLoginPage() throws Exception {
        // Verifying that accessing the login url will successfully show the login page
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-page"));
    }

    @Test
    void login() throws Exception {
        // Verifying that logging in with valid user credentials will redirect the user to the home page

        when(loginService.authenticate("validUser", "validPass")).thenReturn(true); // mock successful authentication
        when(loginService.authenticate("invalidUser", "invalidPass")).thenReturn(false); // mock failed authentication

        mockMvc.perform(post("/login")
                        .param("username", "validUser")
                        .param("password", "validPass"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));

        // Verifying that logging in with invalid user credentials will redirect the user to the login page
        mockMvc.perform(post("/login")
                        .param("username", "invalidUser")
                        .param("password", "invalidPass"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to login
                .andExpect(redirectedUrl("/login"));
    }

}

