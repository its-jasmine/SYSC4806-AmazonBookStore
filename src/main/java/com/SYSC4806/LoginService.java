package com.SYSC4806;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    public enum Response {
        ADMIN_LOGIN_SUCCESS,
        CUSTOMER_LOGIN_SUCCESS,
        INVALID_CREDENTIALS
    }
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
    public Response authenticate(String username, String password) {
        Optional<Admin> admin = adminRepository.findAdminByUsername(username);
        if (admin.isPresent() && password.equals(admin.get().getPassword())) {
            return Response.ADMIN_LOGIN_SUCCESS;
        }

        Optional<Customer> customer = customerRepository.findCustomerByUsername(username);
        if (customer.isPresent() && password.equals(customer.get().getPassword())) {
            return Response.CUSTOMER_LOGIN_SUCCESS;
        }

        return Response.INVALID_CREDENTIALS;
    }

}
