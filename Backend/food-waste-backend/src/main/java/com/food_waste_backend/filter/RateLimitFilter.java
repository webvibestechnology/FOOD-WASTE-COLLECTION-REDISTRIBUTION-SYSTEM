package com.food_waste_backend.filter;

import com.food_waste_backend.config.RateLimitConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;

    @Value("${rate.limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate.limit.login.attempts:60}")
    private int loginMaxRequests;

    @Value("${rate.limit.register.attempts:30}")
    private int registerMaxRequests;

    @Value("${rate.limit.general.attempts:300}")
    private int generalMaxRequests;

    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        String clientIP = getClientIP(request);
        String key = clientIP + ":" + requestURI;

        boolean allowed = true;
        int maxRequests = generalMaxRequests;
        long windowMinutes = 1;

        // Apply specific rate limits for auth endpoints
        if (requestURI.contains("/api/auth/login")) {
            maxRequests = isLoopback(clientIP) ? loginMaxRequests * 2 : loginMaxRequests;
            windowMinutes = 15;
            allowed = rateLimitConfig.tryConsume(key, maxRequests, windowMinutes);
        } else if (requestURI.contains("/api/auth/register")) {
            maxRequests = isLoopback(clientIP) ? registerMaxRequests * 2 : registerMaxRequests;
            windowMinutes = 60;
            allowed = rateLimitConfig.tryConsume(key, maxRequests, windowMinutes);
        } else {
            // General API rate limit
            allowed = rateLimitConfig.tryConsume(key, maxRequests, windowMinutes);
        }

        if (allowed) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    private boolean isLoopback(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || "localhost".equals(ip);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
