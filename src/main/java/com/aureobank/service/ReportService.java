package com.aureobank.service;

import com.aureobank.dao.AccountsDao;
import com.aureobank.dao.AuditDao;
import com.aureobank.dao.TransactionsDao;
import com.aureobank.model.Account;
import com.aureobank.model.Transaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {
    private final AccountsDao accountsDao;
    private final TransactionsDao transactionsDao;
    private final AuditDao auditDao;

    public ReportService(AccountsDao accountsDao, TransactionsDao transactionsDao, AuditDao auditDao) {
        this.accountsDao = accountsDao; this.transactionsDao = transactionsDao; this.auditDao = auditDao;
    }

    public Path exportStatement(String accountId) {
        try {
            Account a = accountsDao.findById(accountId).orElseThrow();
            List<Transaction> list = transactionsDao.findByAccount(accountId);
            StringBuilder sb = new StringBuilder();
            sb.append("AureoBank Statement\n");
            sb.append("Account: ").append(a.getAccountNumber()).append("\n\n");
            DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
            for (Transaction t : list) {
                sb.append(fmt.format(t.getTimestamp())).append(" ")
                  .append(t.getType()).append(" ₱").append(t.getAmount()).append(" ")
                  .append(t.getNote() == null ? "" : t.getNote()).append("\n");
            }
            Path out = Path.of("statements", a.getAccountNumber() + "-statement.txt");
            Files.createDirectories(out.getParent());
            Files.writeString(out, sb.toString());
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
