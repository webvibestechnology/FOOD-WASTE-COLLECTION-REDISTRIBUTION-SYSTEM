package com.food_waste_backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static boolean isExpired(LocalDateTime expiryTime) {

        return LocalDateTime.now().isAfter(expiryTime);
    }

    public static String formatDateTime(LocalDateTime dateTime) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return dateTime.format(formatter);
    }
}