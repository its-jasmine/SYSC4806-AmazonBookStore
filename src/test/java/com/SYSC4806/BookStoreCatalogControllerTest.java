package com.SYSC4806;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
public class BookStoreCatalogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void showBookCatalogPage() throws Exception {
        // Verifying that accessing the login url will successfully show the login page
        mockMvc.perform(get("/book-catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-catalog"));
    }

}
