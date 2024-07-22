package com.revpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.User;
import com.revpay.repository.UserRepository;
import com.revpay.utility.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            response.put("message", "Username cannot be null or empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("message", "password cannot be null or empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            response.put("message", "User already exists");
            response.put("status", HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        String encodedSha = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedSha);
        userRepo.save(user);

        response.put("message", "User registered successfully");
        response.put("status", HttpStatus.CREATED.value());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User loginRequest) {
        Map<String, Object> response = new HashMap<>();

        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
            response.put("message", "Username cannot be null or empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            response.put("message", "password cannot be null or empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOpt = userRepo.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());

                response.put("message", "Login successful");
                response.put("jwt_token", token);
                response.put("status", HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        response.put("message", "Invalid username or password");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                                @RequestBody User loginRequest) {
        Map<String, Object> response = new HashMap<>();

        // Check if username is provided
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username cannot be empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check if password is provided
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            response.put("message", "Password cannot be empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check if token is provided
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("message", "Token cannot be empty");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Extract token
        String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : "";

        // Validate Token
        if (!jwtUtil.isTokenValid(token, loginRequest.getUsername())) {
            response.put("message", "Invalid token");
            response.put("status", HttpStatus.FORBIDDEN.value());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Check if user exists
        Optional<User> userOpt = userRepo.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Validate password
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                response.put("message", "User is authenticated");
                response.put("status", HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Invalid password");
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }

        response.put("message", "Invalid username");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
