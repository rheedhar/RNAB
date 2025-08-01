package com.rnab.rnab.service;

import com.rnab.rnab.dto.LoginRequest;
import com.rnab.rnab.dto.RegisterUserRequest;
import com.rnab.rnab.exception.InvalidCredentialException;
import com.rnab.rnab.exception.UserAlreadyExistsException;
import com.rnab.rnab.exception.UserNotFoundException;
import com.rnab.rnab.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser(RegisterUserRequest registerRequest) {
        if(userService.userExists(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email: " + registerRequest.getEmail() + " already exists");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return userService.saveUser(newUser);
    }

    public String loginUser(LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());

            if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new InvalidCredentialException("Invalid email or password");
            }

            return jwtService.generateToken(user.getEmail());

        } catch (UserNotFoundException e) {
            throw new InvalidCredentialException("Invalid email or password");
        }
    }

    public User validateToken(String token) {
        String email = jwtService.extractEmail(token);

        try {
            User user = userService.findByEmail(email);

            if (!jwtService.isTokenValid(token, email)) {
                throw new InvalidCredentialException("Invalid token");
            }
            return user;

        } catch (UserNotFoundException e) {
            throw new InvalidCredentialException("User not found");
        }

    }


}
