package com.aureobank.dao.json;

import com.aureobank.dao.TransactionsDao;
import com.aureobank.model.Transaction;
import com.aureobank.util.JsonStore;
import java.util.*;

public class JsonTransactionsDao implements TransactionsDao {
    private final JsonStore store;

    public JsonTransactionsDao(JsonStore store) { this.store = store; }

    private List<Transaction> load() { return store.readList("transactions.json", Transaction.class); }
    private void persist(List<Transaction> list) { store.writeList("transactions.json", list); }

    @Override
    public List<Transaction> findAll() { return new ArrayList<>(load()); }

    @Override
    public List<Transaction> findByAccount(String accountId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : load()) {
            if (accountId.equals(t.getFromAccountId()) || accountId.equals(t.getToAccountId())) out.add(t);
        }
        return out;
    }

    @Override
    public void save(Transaction t) {
        List<Transaction> list = load();
        if (t.getId() == null) t.setId(UUID.randomUUID().toString());
        list.add(t);
        persist(list);
    }
}
