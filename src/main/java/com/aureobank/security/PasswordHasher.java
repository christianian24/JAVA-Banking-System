package com.aureobank.security;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private final SecureRandom random = new SecureRandom();

    public String hash(String password) {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Throwable t) {
            // fallback to SHA-256 + salt
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return "SHA256$" + Base64.getEncoder().encodeToString(salt) + "$" + sha256(password, salt);
        }
    }

    public boolean verify(String password, String stored) {
        if (stored == null) return false;
        if (stored.startsWith("$2a") || stored.startsWith("$2b") || stored.startsWith("$2y")) {
            return BCrypt.checkpw(password, stored);
        }
        if (stored.startsWith("SHA256$")) {
            String[] parts = stored.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            return ("SHA256$" + parts[1] + "$" + sha256(password, salt)).equals(stored);
        }
        return false;
    }

    private String sha256(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
