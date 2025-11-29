package com.aureobank.dao;

import com.aureobank.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountsDao {
    Optional<Account> findById(String id);
    List<Account> findByUser(String userId);
    List<Account> findAll();
    void save(Account acc);
    void delete(String id);
}
