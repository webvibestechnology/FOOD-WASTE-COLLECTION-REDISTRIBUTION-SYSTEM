package com.food_waste_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.food_waste_backend.dto.NotificationResponse;
import com.food_waste_backend.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(notificationService.getNotificationsByUser(email));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotificationsByEmail(
            @RequestParam(required = false) String email,
            Authentication authentication) {
        String targetEmail = (email != null && !email.isBlank()) ? email : (authentication != null ? authentication.getName() : null);
        if (targetEmail != null && !targetEmail.isBlank()) {
            return ResponseEntity.ok(notificationService.getNotificationsByUser(targetEmail));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @RequestParam(required = false) String email,
            Authentication authentication) {
        String targetEmail = (email != null && !email.isBlank()) ? email : (authentication != null ? authentication.getName() : null);
        if (targetEmail != null && !targetEmail.isBlank()) {
            return ResponseEntity.ok(notificationService.getUnreadNotifications(targetEmail));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam(required = false) String email,
            Authentication authentication) {
        String targetEmail = (email != null && !email.isBlank()) ? email : (authentication != null ? authentication.getName() : null);
        if (targetEmail != null && !targetEmail.isBlank()) {
            notificationService.markAllAsRead(targetEmail);
        }
        return ResponseEntity.ok().build();
    }
}
