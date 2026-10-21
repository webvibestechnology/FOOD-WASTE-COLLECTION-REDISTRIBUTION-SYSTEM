package com.food_waste_backend.config;

import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class RateLimitConfig {

    // Simple in-memory rate limiting using ConcurrentHashMap
    // Key: "IP:endpoint", Value: List of timestamps
    private final ConcurrentMap<String, java.util.List<LocalDateTime>> requestCache = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int maxRequests, long windowMinutes) {
        String cacheKey = key;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(windowMinutes);

        // Get or create request list for this key
        java.util.List<LocalDateTime> requests = requestCache.computeIfAbsent(
            cacheKey,
            k -> new java.util.concurrent.CopyOnWriteArrayList<>()
        );

        // Remove old requests outside the time window
        requests.removeIf(timestamp -> timestamp.isBefore(windowStart));

        // Check if rate limit exceeded
        if (requests.size() >= maxRequests) {
            return false;
        }

        // Add current request
        requests.add(now);
        return true;
    }

    public void clear() {
        requestCache.clear();
    }
}
