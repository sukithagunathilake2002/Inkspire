package com.inkspire.inkspire.controller;

import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.payload.AuthResponse;
import com.inkspire.inkspire.payload.LoginRequest;
import com.inkspire.inkspire.payload.SignupRequest;
import com.inkspire.inkspire.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            // Map SignupRequest to User entity
            User user = new User();
            user.setName(signupRequest.getName());
            user.setEmail(signupRequest.getEmail());
            user.setPhoneNumber(signupRequest.getPhoneNumber());
            user.setPassword(signupRequest.getPassword());

            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", registeredUser.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail());
            
            // Add debug logging
            System.out.println("User data during login:");
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Phone: " + user.getPhoneNumber());
            
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setType("Bearer");
            
            // Log the response object
            System.out.println("Auth Response: " + response);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            
            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Email and new password are required");
            }
            
            boolean success = userService.resetPassword(email, newPassword);
            if (success) {
                return ResponseEntity.ok("Password reset successfully");
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> updates) {
        try {
            // Get the current user
            User user = userService.getUserByEmail(userDetails.getUsername());
            
            // Update only allowed fields (name and phone number)
            if (updates.containsKey("name")) {
                user.setName(updates.get("name"));
            }
            if (updates.containsKey("phoneNumber")) {
                // Validate phone number format
                String phoneNumber = updates.get("phoneNumber");
                if (!phoneNumber.matches("^[0-9]{10}$")) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Phone number must be 10 digits"));
                }
                // Check if phone number is already in use by another user
                if (!user.getPhoneNumber().equals(phoneNumber) && 
                    userService.isPhoneNumberTaken(phoneNumber)) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Phone number is already in use"));
                }
                user.setPhoneNumber(phoneNumber);
            }
            
            // Save the updated user
            User updatedUser = userService.updateUser(user);
            
            // Return the updated user info
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", Map.of(
                    "id", updatedUser.getId(),
                    "name", updatedUser.getName(),
                    "email", updatedUser.getEmail(),
                    "phoneNumber", updatedUser.getPhoneNumber()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}