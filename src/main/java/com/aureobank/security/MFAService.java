package com.aureobank.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MFAService {
    private static class Code {
        String code; Instant expires;
    }
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Code> codes = new HashMap<>();

    public String issue(String userId) {
        Code c = new Code();
        c.code = String.format("%06d", random.nextInt(1_000_000));
        c.expires = Instant.now().plusSeconds(300);
        codes.put(userId, c);
        return c.code; // In real app, send via SMS/Email
    }

    public boolean verify(String userId, String input) {
        Code c = codes.get(userId);
        if (c == null) return false;
        if (Instant.now().isAfter(c.expires)) return false;
        return c.code.equals(input);
    }
}
