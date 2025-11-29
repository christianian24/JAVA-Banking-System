package com.aureobank.service;

import com.aureobank.dao.AccountsDao;
import com.aureobank.dao.AuditDao;
import com.aureobank.dao.UsersDao;
import com.aureobank.model.Account;
import com.aureobank.model.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private final AccountsDao accountsDao;
    private final UsersDao usersDao;
    private final AuditDao auditDao;

    public AccountService(AccountsDao accountsDao, UsersDao usersDao, AuditDao auditDao) {
        this.accountsDao = accountsDao; this.usersDao = usersDao; this.auditDao = auditDao;
    }

    public List<Account> getUserAccounts(String userId) { return accountsDao.findByUser(userId); }

    public Account openAccount(String userId, String type, String actorUserId) {
        Optional<User> ou = usersDao.findById(userId);
        if (ou.isEmpty()) throw new IllegalArgumentException("User not found");
        Account acc = new Account();
        acc.setId(UUID.randomUUID().toString());
        acc.setUserId(userId);
        acc.setType(type);
        acc.setAccountNumber("AB-" + System.currentTimeMillis());
        acc.setBalance(0.0);
        accountsDao.save(acc);
        auditDao.record(actorUserId, "OPEN_ACCOUNT", "Opened " + type + " for user " + userId, Instant.now());
        return acc;
    }
}
