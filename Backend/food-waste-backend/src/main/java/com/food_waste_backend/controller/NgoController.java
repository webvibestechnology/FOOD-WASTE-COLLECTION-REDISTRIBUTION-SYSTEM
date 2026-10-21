package com.food_waste_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.NgoRequest;
import com.food_waste_backend.dto.NgoResponse;
import com.food_waste_backend.dto.UserResponse;
import com.food_waste_backend.service.NgoService;

@RestController
@RequestMapping("/api/ngos")
@CrossOrigin(origins = "http://localhost:4200")
public class NgoController {

    private final NgoService ngoService;

    public NgoController(NgoService ngoService) {
        this.ngoService = ngoService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerAsNgo(
            @RequestBody NgoRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(ngoService.registerAsNgo(email, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<NgoResponse> getNgoProfile(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(ngoService.getNgoProfile(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<NgoResponse> updateNgoProfile(
            @RequestBody NgoRequest request,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(ngoService.updateNgoProfile(email, request));
    }

    @GetMapping
    public ResponseEntity<List<NgoResponse>> getAllNgos() {
        return ResponseEntity.ok(ngoService.getAllNgos());
    }

    @GetMapping("/verified")
    public ResponseEntity<List<NgoResponse>> getVerifiedNgos() {
        return ResponseEntity.ok(ngoService.getVerifiedNgos());
    }
}
