package com.aureobank.dao;

import com.aureobank.model.User;
import java.util.List;
import java.util.Optional;

public interface UsersDao {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void save(User user);
    void delete(String id);
}
