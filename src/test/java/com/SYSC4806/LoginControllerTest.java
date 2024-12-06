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
    private CustomerRepository customerRepository;
    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private LoginService loginService;

    @Test
    void showLoginPage() throws Exception {
        // Verifying that accessing the login url will successfully show the login page
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-page"));
    }

    /**
     * Successful Customer login redirects to the home page
     * @throws Exception
     */
    @Test
    void loginCustomer() throws Exception {
        when(loginService.authenticate("customer1", "password1")).thenReturn(LoginService.Response.CUSTOMER_LOGIN_SUCCESS);
        mockMvc.perform(post("/login")
                        .param("username", "customer1")
                        .param("password", "password1"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));
    }

    /**
     * Successful Admin login redirects to the inventory page
     * @throws Exception
     */
    @Test
    void loginAdmin() throws Exception {
        // Verifying that logging in with valid admin credentials will redirect the admin to the inventory page
        when(loginService.authenticate("admin1", "password1")).thenReturn(LoginService.Response.ADMIN_LOGIN_SUCCESS);
        mockMvc.perform(post("/login")
                        .param("username", "admin1")
                        .param("password", "password1"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to the inventory
                .andExpect(redirectedUrl("/inventory"));
    }

    @Test
    void loginInvalidCredentials() throws Exception {
        // Verifying that logging in with invalid user credentials will redirect the user to the login page
        when(loginService.authenticate("invalidUser", "invalidPass")).thenReturn(LoginService.Response.INVALID_CREDENTIALS);
        mockMvc.perform(post("/login")
                        .param("username", "invalidUser")
                        .param("password", "invalidPass"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-page"));
    }

}

