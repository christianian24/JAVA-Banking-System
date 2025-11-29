package com.aureobank;

import com.aureobank.model.User;
import com.aureobank.security.PasswordHasher;
import com.aureobank.util.JsonStore;
import com.aureobank.util.LoggerSetup;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LoggerSetup.init();
        System.out.println("AureoBank System Initializing...");

        // Setup data storage
        JsonStore store = new JsonStore(Paths.get("data"));

        // For demonstration, let's check if we have any users.
        // If not, create a default admin user.
        Map<String, User> users = store.readMap("users.json", User.class);
        if (users.isEmpty()) {
            System.out.println("No users found. Creating a default admin user.");
            PasswordHasher hasher = new PasswordHasher();
            // In a real app, you would handle user creation securely.
            System.out.println("This is a simplified example for demonstration.");
        } else {
            System.out.println("Loaded " + users.size() + " users.");
        }
        System.out.println("AureoBank System Ready.");
    }
}