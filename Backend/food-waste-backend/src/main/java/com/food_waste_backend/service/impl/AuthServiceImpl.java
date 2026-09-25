package com.food_waste_backend.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.User;
import com.food_waste_backend.dto.LoginRequest;
import com.food_waste_backend.dto.LoginResponse;
import com.food_waste_backend.dto.RegisterRequest;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.exception.BadRequestException;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.service.AuthService;
import com.food_waste_backend.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("User not found")
                );

        String token = jwtService.generateToken(user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }

    @Override
    public UserResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());

        user.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );

        user.setPhone(registerRequest.getPhone());

        if (registerRequest.getRole() != null
                && !registerRequest.getRole().isBlank()) {

            user.setRole(
                    com.food_waste_backend.enums.UserRole
                            .valueOf(registerRequest.getRole().toUpperCase())
            );
        }

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole() != null
                        ? savedUser.getRole().name()
                        : null)
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}