package com.SYSC4806;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    final AdminRepository adminRepository;

    final CustomerRepository customerRepository;

    public LoginService(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves the user by username, checks if the provided password matches.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        Optional<Admin> admin = adminRepository.findAdminByUsername(username);
        if (admin.isPresent() && password.equals(admin.get().getPassword())) {
            return true;
        }

        Optional<Customer> customer = customerRepository.findCustomerByUsername(username);
        if (customer.isPresent() && password.equals(customer.get().getPassword())) {
            return true;
        }

        return false;
    }

}
