package com.aureobank.dao.json;

import com.aureobank.dao.AuditDao;
import com.aureobank.model.AuditLog;
import com.aureobank.util.JsonStore;
import java.time.Instant;
import java.util.*;

public class JsonAuditDao implements AuditDao {
    private final JsonStore store;

    public JsonAuditDao(JsonStore store) { this.store = store; }

    private List<AuditLog> load() { return store.readList("audit.json", AuditLog.class); }
    private void persist(List<AuditLog> list) { store.writeList("audit.json", list); }

    @Override
    public void record(String actorUserId, String action, String details, Instant when) {
        List<AuditLog> list = load();
        AuditLog log = new AuditLog();
        log.setId(UUID.randomUUID().toString());
        log.setActorUserId(actorUserId);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(when);
        list.add(log);
        persist(list);
    }
}
