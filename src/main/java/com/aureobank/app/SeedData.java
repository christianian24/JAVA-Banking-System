package com.aureobank.app;

import com.aureobank.dao.AuditDao;
import com.aureobank.dao.ConfigDao;
import com.aureobank.dao.UsersDao;
import com.aureobank.model.Role;
import com.aureobank.model.User;
import com.aureobank.security.PasswordHasher;
import java.time.Instant;
import java.util.Optional;

public class SeedData {
    public static void ensureSeedAdmin(UsersDao usersDao, PasswordHasher hasher, AuditDao auditDao, ConfigDao configDao) {
        Optional<User> admin = usersDao.findByUsername("admin");
        if (admin.isEmpty()) {
            User u = new User();
            u.setId("U-ADMIN");
            u.setUsername("admin");
            u.setDisplayName("System Administrator");
            u.setRole(Role.ADMIN);
            u.setActive(true);
            u.setMfaEnabled(false);
            String hash = hasher.hash("Admin@123");
            u.setPasswordHash(hash);
            usersDao.save(u);
            auditDao.record("system", "SEED_ADMIN", "Created default admin user", Instant.now());
        }
        // Seed config - reserve base amount
        configDao.saveReserveBase(1_000_000.00);
        configDao.saveReserveThresholdPercent(0.10);
    }
}
