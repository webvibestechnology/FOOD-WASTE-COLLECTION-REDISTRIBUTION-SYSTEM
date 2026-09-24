package com.food_waste_backend.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static Map<String, Object> success(Object data, String message) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", message);
        response.put("data", data);

        return response;
    }

    public static Map<String, Object> error(String message) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", message);

        return response;
    }
}