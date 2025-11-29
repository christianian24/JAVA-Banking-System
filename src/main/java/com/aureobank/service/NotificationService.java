package com.aureobank.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {
    private final Map<String, List<String>> inbox = new HashMap<>();

    public void notifyUser(String userId, String message) {
        inbox.computeIfAbsent(userId, k -> new ArrayList<>()).add(Instant.now() + ": " + message);
    }

    public List<String> getUserNotifications(String userId) {
        return inbox.getOrDefault(userId, List.of());
    }

    public void clearUserNotifications(String userId) {
        inbox.remove(userId);
    }
}
