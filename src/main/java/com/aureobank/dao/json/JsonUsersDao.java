package com.aureobank.dao.json;

import com.aureobank.dao.UsersDao;
import com.aureobank.model.User;
import com.aureobank.util.JsonStore;
import java.util.*;

public class JsonUsersDao implements UsersDao {
    private final JsonStore store;

    public JsonUsersDao(JsonStore store) {
        this.store = store;
    }

    private Map<String, User> load() {
        return store.readMap("users.json", User.class);
    }

    private void persist(Map<String, User> map) {
        store.writeMap("users.json", map);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(load().get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return load().values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void save(User user) {
        Map<String, User> map = load();
        if (user.getId() == null) user.setId(UUID.randomUUID().toString());
        map.put(user.getId(), user);
        persist(map);
    }

    @Override
    public void delete(String id) {
        Map<String, User> map = load();
        map.remove(id);
        persist(map);
    }
}
