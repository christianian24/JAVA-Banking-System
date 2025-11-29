package com.aureobank.service;

import com.aureobank.dao.AuditDao;
import com.aureobank.dao.UsersDao;
import com.aureobank.model.User;
import com.aureobank.security.MFAService;
import com.aureobank.security.PasswordHasher;
import java.time.Instant;
import java.util.Optional;

public class AuthService {
    private final UsersDao usersDao;
    private final PasswordHasher hasher;
    private final MFAService mfa;
    private final AuditDao auditDao;

    public AuthService(UsersDao usersDao, PasswordHasher hasher, MFAService mfa, AuditDao auditDao) {
        this.usersDao = usersDao; this.hasher = hasher; this.mfa = mfa; this.auditDao = auditDao;
    }

    public enum LoginResult { SUCCESS_ADMIN, SUCCESS_USER, MFA_REQUIRED, FAIL }

    public LoginResult login(String username, String password, String code) {
        Optional<User> ou = usersDao.findByUsername(username);
        if (ou.isEmpty()) return LoginResult.FAIL;
        User u = ou.get();
        if (!u.isActive()) return LoginResult.FAIL;
        if (!hasher.verify(password, u.getPasswordHash())) return LoginResult.FAIL;
        if (u.isMfaEnabled()) {
            if (code == null || code.isBlank()) return LoginResult.MFA_REQUIRED;
            if (!mfa.verify(u.getId(), code)) return LoginResult.FAIL;
        }
        u.addLoginEvent(Instant.now(), "local");
        usersDao.save(u);
        auditDao.record(u.getId(), "LOGIN", "User logged in", Instant.now());
        return u.getRole().name().equals("ADMIN") ? LoginResult.SUCCESS_ADMIN : LoginResult.SUCCESS_USER;
    }

    public void setPassword(String userId, String newPassword) {
        usersDao.findById(userId).ifPresent(u -> {
            u.setPasswordHash(hasher.hash(newPassword));
            usersDao.save(u);
            auditDao.record(userId, "PASSWORD_CHANGE", "User changed password", Instant.now());
        });
    }
}
