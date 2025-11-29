package com.aureobank.dao.json;

import com.aureobank.dao.AccountsDao;
import com.aureobank.model.Account;
import com.aureobank.util.JsonStore;
import java.util.*;

public class JsonAccountsDao implements AccountsDao {
    private final JsonStore store;

    public JsonAccountsDao(JsonStore store) { this.store = store; }

    private Map<String, Account> load() { return store.readMap("accounts.json", Account.class); }
    private void persist(Map<String, Account> map) { store.writeMap("accounts.json", map); }

    @Override
    public Optional<Account> findById(String id) { return Optional.ofNullable(load().get(id)); }

    @Override
    public List<Account> findByUser(String userId) {
        List<Account> out = new ArrayList<>();
        for (Account a : load().values()) if (a.getUserId().equals(userId)) out.add(a);
        return out;
    }

    @Override
    public List<Account> findAll() { return new ArrayList<>(load().values()); }

    @Override
    public void save(Account acc) {
        Map<String, Account> map = load();
        if (acc.getId() == null) acc.setId(UUID.randomUUID().toString());
        map.put(acc.getId(), acc);
        persist(map);
    }

    @Override
    public void delete(String id) {
        Map<String, Account> map = load();
        map.remove(id);
        persist(map);
    }
}
