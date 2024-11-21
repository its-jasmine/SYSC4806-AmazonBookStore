package com.SYSC4806;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    final AppUserRepository userRepository;

    public LoginService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the user by username, checks if the provided password matches.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            return true;
        }
        return false;
    }

}
