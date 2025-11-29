package com.aureobank.dao.json;

import com.aureobank.dao.ApplicationsDao;
import com.aureobank.model.ApplicationRequest;
import com.aureobank.util.JsonStore;
import java.util.*;

public class JsonApplicationsDao implements ApplicationsDao {
    private final JsonStore store;

    public JsonApplicationsDao(JsonStore store) { this.store = store; }

    private Map<String, ApplicationRequest> load() { return store.readMap("applications.json", ApplicationRequest.class); }
    private void persist(Map<String, ApplicationRequest> map) { store.writeMap("applications.json", map); }

    @Override
    public Optional<ApplicationRequest> findById(String id) { return Optional.ofNullable(load().get(id)); }

    @Override
    public List<ApplicationRequest> findAll() { return new ArrayList<>(load().values()); }

    @Override
    public void save(ApplicationRequest app) {
        Map<String, ApplicationRequest> map = load();
        if (app.getId() == null) app.setId(UUID.randomUUID().toString());
        map.put(app.getId(), app);
        persist(map);
    }
}
