package com.rnab.rnab.controller;

import com.rnab.rnab.dto.LoginRequest;
import com.rnab.rnab.dto.LoginResponse;
import com.rnab.rnab.dto.RegisterUserRequest;
import com.rnab.rnab.dto.RegisterUserResponse;
import com.rnab.rnab.model.User;
import com.rnab.rnab.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/home")
    public String welcome() {
        return "Welcome";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        User newUser = authService.registerUser(request);
        RegisterUserResponse response = new RegisterUserResponse(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        String jwtToken = authService.loginUser(request);
        LoginResponse response = new LoginResponse(jwtToken);
        return ResponseEntity.ok(response);
    }

}
