package com.rnab.rnab.service;

import com.rnab.rnab.dto.RegisterUserRequest;
import com.rnab.rnab.exception.UserAlreadyExistsException;
import com.rnab.rnab.exception.UserNotFoundException;
import com.rnab.rnab.model.User;
import com.rnab.rnab.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " does not exist"));
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
