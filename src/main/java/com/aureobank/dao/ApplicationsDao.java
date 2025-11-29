package com.aureobank.dao;

import com.aureobank.model.ApplicationRequest;
import java.util.List;
import java.util.Optional;

public interface ApplicationsDao {
    Optional<ApplicationRequest> findById(String id);
    List<ApplicationRequest> findAll();
    void save(ApplicationRequest app);
}
