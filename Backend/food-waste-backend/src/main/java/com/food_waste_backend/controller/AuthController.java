package com.food_waste_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.LoginRequest;
import com.food_waste_backend.dto.LoginResponse;
import com.food_waste_backend.dto.RegisterRequest;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.service.AuthService;
import com.food_waste_backend.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        UserResponse response = authService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body != null ? body.get("email") : null;
        authService.handleForgotPassword(email);
        return ResponseEntity.ok(ResponseUtil.success("If an account exists for this email, password reset instructions have been logged.", null));
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> contact(@RequestBody Map<String, String> body) {
        String name = body != null ? body.get("name") : null;
        String email = body != null ? body.get("email") : null;
        String subject = body != null ? body.get("subject") : null;
        String message = body != null ? body.get("message") : null;

        authService.handleContactInquiry(name, email, subject, message);
        return ResponseEntity.ok(ResponseUtil.success("Thank you for contacting us. Your message has been received by platform administrators.", null));
    }
}
