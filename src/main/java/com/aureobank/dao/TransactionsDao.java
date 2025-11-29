package com.aureobank.dao;

import com.aureobank.model.Transaction;
import java.util.List;

public interface TransactionsDao {
    List<Transaction> findAll();
    List<Transaction> findByAccount(String accountId);
    void save(Transaction t);
}
