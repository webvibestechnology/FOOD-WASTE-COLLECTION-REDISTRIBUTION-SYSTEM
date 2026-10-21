package com.food_waste_backend.service;

import java.util.List;

import com.food_waste_backend.dto.NotificationResponse;

public interface NotificationService {

    List<NotificationResponse> getNotificationsByUser(String email);

    List<NotificationResponse> getUnreadNotifications(String email);

    List<NotificationResponse> getAllNotifications();

    void markAsRead(Long id);

    void markAllAsRead(String email);
}
