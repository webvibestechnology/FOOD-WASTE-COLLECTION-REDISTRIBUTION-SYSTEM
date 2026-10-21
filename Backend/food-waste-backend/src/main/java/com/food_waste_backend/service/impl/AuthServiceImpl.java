package com.food_waste_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.food_waste_backend.Entity.Donor;
import com.food_waste_backend.Entity.Ngo;
import com.food_waste_backend.Entity.Notification;
import com.food_waste_backend.Entity.User;
import com.food_waste_backend.Entity.Volunteer;
import com.food_waste_backend.dto.LoginRequest;
import com.food_waste_backend.dto.LoginResponse;
import com.food_waste_backend.dto.RegisterRequest;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.enums.NotificationType;
import com.food_waste_backend.enums.UserRole;
import com.food_waste_backend.exception.BadRequestException;
import com.food_waste_backend.repository.DonorRepository;
import com.food_waste_backend.repository.NgoRepository;
import com.food_waste_backend.repository.NotificationRepository;
import com.food_waste_backend.repository.UserRepository;
import com.food_waste_backend.repository.VolunteerRepository;
import com.food_waste_backend.security.CustomUserDetailsService;
import com.food_waste_backend.security.JwtService;
import com.food_waste_backend.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DonorRepository donorRepository;
    private final NgoRepository ngoRepository;
    private final VolunteerRepository volunteerRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthServiceImpl(
            UserRepository userRepository,
            DonorRepository donorRepository,
            NgoRepository ngoRepository,
            VolunteerRepository volunteerRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService) {

        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.ngoRepository = ngoRepository;
        this.volunteerRepository = volunteerRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
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

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        if (user.getRole() != null) {
            response.setRole(user.getRole().name());
        }

        return response;
    }

    @Override
    public UserResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        UserRole userRole = UserRole.DONOR;
        if (registerRequest.getRole() != null && !registerRequest.getRole().isBlank()) {
            userRole = UserRole.valueOf(registerRequest.getRole().toUpperCase().trim());
        }
        user.setRole(userRole);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Automatically create matching role profile
        if (savedUser.getRole() == UserRole.DONOR) {
            Donor donor = new Donor();
            donor.setUser(savedUser);
            donor.setOrganizationName(savedUser.getName());
            donor.setDescription("Food Donor Account");
            donor.setCreatedAt(LocalDateTime.now());
            donorRepository.save(donor);
        } else if (savedUser.getRole() == UserRole.NGO) {
            Ngo ngo = new Ngo();
            ngo.setUser(savedUser);
            ngo.setNgoName(savedUser.getName());
            ngo.setRegistrationNumber("REG-" + savedUser.getId() + "-" + System.currentTimeMillis() % 10000);
            ngo.setDescription("Registered Food Redistribution NGO");
            ngo.setContactPerson(savedUser.getName());
            ngo.setVerified(false); // Requires admin approval for trust and safety
            ngo.setRegisteredAt(LocalDateTime.now());
            ngoRepository.save(ngo);
        } else if (savedUser.getRole() == UserRole.VOLUNTEER) {
            Volunteer volunteer = new Volunteer();
            volunteer.setUser(savedUser);
            volunteer.setVehicleType("Standard Vehicle");
            volunteer.setOperatingArea("Local Area");
            volunteer.setAvailable(true);
            volunteerRepository.save(volunteer);
        }

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        if (savedUser.getRole() != null) {
            response.setRole(savedUser.getRole().name());
        }
        response.setCreatedAt(savedUser.getCreatedAt());
        return response;
    }

    @Override
    public void handleForgotPassword(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        userRepository.findByEmail(email.trim()).ifPresent(user -> {
            Notification n = new Notification();
            n.setUser(user);
            n.setTitle("Password Reset Request");
            n.setMessage("A password recovery request was received for your account. If you requested this, please contact support or an administrator to reset your credentials.");
            n.setType(NotificationType.SYSTEM);
            n.setIsRead(false);
            notificationRepository.save(n);
        });
    }

    @Override
    public void handleContactInquiry(String name, String email, String subject, String message) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || message == null || message.isBlank()) {
            throw new BadRequestException("Name, email, and message are required fields");
        }

        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        for (User admin : admins) {
            Notification n = new Notification();
            n.setUser(admin);
            n.setTitle("New Contact Message: " + (subject != null ? subject : "General Inquiry"));
            n.setMessage("From: " + name + " (" + email + ")\n" + message);
            n.setType(NotificationType.SYSTEM);
            n.setIsRead(false);
            notificationRepository.save(n);
        }
    }
}
