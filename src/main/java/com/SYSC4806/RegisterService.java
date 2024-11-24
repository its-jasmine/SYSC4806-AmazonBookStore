package com.SYSC4806;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {

    public enum Response {
        SUCCESS,           // Registration successful for a customer
        INVALID_USERNAME,  // Registration failed since username is already in use
        FAILED             // Registration failed
    }

    final CustomerRepository customerRepository;

    public RegisterService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * If the provided username already exists, INVALID_USERNAME response is returned.
     * If the username and password are not null, SUCCESS response is returned.
     * Otherwise, the FAILED response is returned.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return Response.SUCCESS for successful registration, INVALID_USERNAME if username is in use, FAILED otherwise.
     */
    public Response register(String username, String password) {
        Optional<Customer> user = customerRepository.findCustomerByUsername(username);
        if (user.isPresent()) {
            return Response.INVALID_USERNAME;
        }
        if (username != null && password != null) {
            Customer customer = new Customer(username, password); //TODO Change to Customer object
            customerRepository.save(customer);
            return Response.SUCCESS;
        }
        return Response.FAILED;
    }

}
