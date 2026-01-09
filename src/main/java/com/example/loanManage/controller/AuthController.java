package com.example.loanManage.controller;

import com.example.loanManage.dto.LoginRequest;
import com.example.loanManage.dto.LoginResponse;
import com.example.loanManage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController // Tells Spring this class handles REST API requests and returns data (JSON).
@RequestMapping("/api/auth") // Defines the base URL for authentication tasks like login or registration.
@CrossOrigin(origins = "*") // Permits the frontend (like a browser app) to access these login APIs.
public class AuthController {

    private final UserService userService; // Reference to the service that verifies user credentials.

    public AuthController(UserService userService) { // Constructor to link the UserService to this controller.
        this.userService = userService;
    }

    @PostMapping("/login") // Maps HTTP POST requests sent to "/api/auth/login" to this method.
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // @RequestBody takes the JSON (email/password) and turns it into a Java object.

        LoginResponse response = userService.login(request); // Asks the service to check if the email and password are correct.

        if (!response.isSuccess()) { // If the login failed (wrong password/email)...
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            // Returns a "401 Unauthorized" status code to the user.
        }

        return ResponseEntity.ok(response); // If login is successful, returns "200 OK" with user details or a token.
    }
}
