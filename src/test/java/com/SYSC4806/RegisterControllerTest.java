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

@WebMvcTest(RegisterController.class)
public class RegisterControllerTest {
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

    @MockBean
    private RegisterService registerService;

    @Test
    void showRegisterPage() throws Exception {
        // Verifying that accessing the register url will successfully show the register page
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-page"));
    }

    /**
     * Successful Customer registration redirects to the home page
     * @throws Exception
     */
    @Test
    void registerCustomer() throws Exception {
        when(registerService.register("customer1", "password1")).thenReturn(RegisterService.Response.SUCCESS);
        mockMvc.perform(post("/register")
                        .param("username", "customer1")
                        .param("password", "password1"))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to home
                .andExpect(redirectedUrl("/home"));
    }

    /**
     * Registration page is returned if the username is already in use
     * @throws Exception
     */
    @Test
    void usernameInUse() throws Exception {
        when(registerService.register("customer1", "password1")).thenReturn(RegisterService.Response.INVALID_USERNAME);
        mockMvc.perform(post("/register")
                        .param("username", "customer1")
                        .param("password", "password1"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-page"));
    }

    /**
     * Registration page is returned if either username or password are null.
     * @throws Exception
     */
    @Test
    void nullCredentials() throws Exception {
        when(registerService.register("", "")).thenReturn(RegisterService.Response.FAILED);
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection()) // we are expecting a redirection to the register page
                .andExpect(redirectedUrl("/register"));
    }

}
