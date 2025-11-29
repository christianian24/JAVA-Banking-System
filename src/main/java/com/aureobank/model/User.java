package com.aureobank.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String username;
    private String displayName;
    private String passwordHash;
    private Role role;
    private boolean active;
    private boolean mfaEnabled;
    private List<String> loginHistory = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }

    public List<String> getLoginHistory() { return loginHistory; }
    public void setLoginHistory(List<String> loginHistory) { this.loginHistory = loginHistory; }

    public void addLoginEvent(Instant when, String ip) {
        loginHistory.add(when.toString() + "|" + ip);
    }
}
