package com.aureobank.service;

import com.aureobank.dao.AccountsDao;
import com.aureobank.dao.AuditDao;
import com.aureobank.dao.TransactionsDao;
import com.aureobank.model.Account;
import com.aureobank.model.Transaction;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {
    private final AccountsDao accountsDao;
    private final TransactionsDao transactionsDao;
    private final ReserveService reserveService;
    private final AuditDao auditDao;
    private final NotificationService notificationService;

    public TransactionService(AccountsDao accountsDao, TransactionsDao transactionsDao, ReserveService reserveService, AuditDao auditDao, NotificationService notificationService) {
        this.accountsDao = accountsDao; this.transactionsDao = transactionsDao; this.reserveService = reserveService; this.auditDao = auditDao; this.notificationService = notificationService;
    }

    public void deposit(String toAccountId, double amount, String actorUserId) {
        validateAmount(amount);
        Account to = requireAccount(toAccountId);
        reserveService.ensureCanDeposit(amount);
        to.setBalance(to.getBalance() + amount);
        accountsDao.save(to);
        reserveService.onDeposit(amount);
        record("DEPOSIT", null, toAccountId, amount, actorUserId, "Cash deposit");
        notificationService.notifyUser(to.getUserId(), "Deposit of ₱" + amount + " successful.");
    }

    public void withdraw(String fromAccountId, double amount, String actorUserId) {
        validateAmount(amount);
        Account from = requireAccount(fromAccountId);
        if (from.getBalance() < amount) throw new IllegalArgumentException("Insufficient funds");
        from.setBalance(from.getBalance() - amount);
        accountsDao.save(from);
        reserveService.onWithdraw(amount);
        record("WITHDRAWAL", fromAccountId, null, amount, actorUserId, "Cash withdrawal");
        notificationService.notifyUser(from.getUserId(), "Withdrawal of ₱" + amount + " successful.");
    }

    public void transfer(String fromAccountId, String toAccountId, double amount, String actorUserId) {
        validateAmount(amount);
        if (fromAccountId.equals(toAccountId)) throw new IllegalArgumentException("Cannot transfer to same account");
        Account from = requireAccount(fromAccountId);
        Account to = requireAccount(toAccountId);
        if (from.getBalance() < amount) throw new IllegalArgumentException("Insufficient funds");
        // Reserve: withdrawal then deposit. Ensure reserve allows the deposit leg.
        reserveService.ensureCanDeposit(amount);
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        accountsDao.save(from);
        accountsDao.save(to);
        // reserve doesn't change net on internal transfers: +amount (withdraw) then -amount (deposit)
        record("TRANSFER", fromAccountId, toAccountId, amount, actorUserId, "Internal transfer");
        notificationService.notifyUser(from.getUserId(), "Transfer of ₱" + amount + " sent.");
        notificationService.notifyUser(to.getUserId(), "Transfer of ₱" + amount + " received.");
    }

    public void billPay(String fromAccountId, String biller, double amount, String actorUserId) {
        validateAmount(amount);
        Account from = requireAccount(fromAccountId);
        if (from.getBalance() < amount) throw new IllegalArgumentException("Insufficient funds");
        from.setBalance(from.getBalance() - amount);
        accountsDao.save(from);
        reserveService.onWithdraw(amount);
        record("BILLPAY", fromAccountId, null, amount, actorUserId, "Bill pay to " + biller);
        notificationService.notifyUser(from.getUserId(), "Bill payment of ₱" + amount + " to " + biller + " successful.");
    }

    private void validateAmount(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }

    private Account requireAccount(String id) {
        Optional<Account> oa = accountsDao.findById(id);
        if (oa.isEmpty()) throw new IllegalArgumentException("Account not found");
        return oa.get();
    }

    private void record(String type, String fromId, String toId, double amount, String actorUserId, String note) {
        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setType(type);
        t.setFromAccountId(fromId);
        t.setToAccountId(toId);
        t.setAmount(amount);
        t.setTimestamp(Instant.now());
        t.setActorUserId(actorUserId);
        t.setNote(note);
        transactionsDao.save(t);
        auditDao.record(actorUserId, type, note, Instant.now());
    }
}
