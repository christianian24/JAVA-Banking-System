package com.aureobank.dao;

import java.time.Instant;

public interface AuditDao {
    void record(String actorUserId, String action, String details, Instant when);
}
