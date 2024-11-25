package com.SYSC4806;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test") // Activates the "test" profile for this test class
@DataJpaTest
public class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    public void testCreateAndRead() {
        Admin admin = new Admin("admin1", "password1");
        adminRepository.save(admin);

        Optional<Admin> retrievedAdmin = adminRepository.findAdminByUsername("admin1");

        assertTrue(retrievedAdmin.isPresent());
        assertEquals("admin1", retrievedAdmin.get().getUsername());
        assertEquals("password1", retrievedAdmin.get().getPassword());
    }

    @Test
    public void testUpdate() {
        Admin admin = new Admin("admin1", "password1");
        adminRepository.save(admin);

        admin.setPassword("password2");
        adminRepository.save(admin);

        Optional<Admin> updatedAdmin = adminRepository.findAdminByUsername("admin1");
        assertTrue(updatedAdmin.isPresent());
        assertEquals("password2", updatedAdmin.get().getPassword());
    }

    @Test
    public void testDelete() {
        Admin admin = new Admin("admin1", "password1");
        adminRepository.save(admin);

        adminRepository.delete(admin);

        Optional<Admin> retrievedAdmin = adminRepository.findAdminByUsername("admin1");
        assertFalse(retrievedAdmin.isPresent());
    }

    @Test
    public void testFindAdminByNonExistentUsername() {
        Optional<Admin> retrievedAdmin = adminRepository.findAdminByUsername("admin2");
        assertFalse(retrievedAdmin.isPresent());
    }

}
