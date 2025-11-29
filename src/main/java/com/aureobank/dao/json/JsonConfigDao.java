package com.aureobank.dao.json;

import com.aureobank.dao.ConfigDao;
import com.aureobank.util.JsonStore;

public class JsonConfigDao implements ConfigDao {
    private final JsonStore store;
    public JsonConfigDao(JsonStore store){ this.store = store; }

    private static class Config {
        double reserveBase = 1_000_000.0;
        double reserveThresholdPercent = 0.10;
        double currentReserve = 1_000_000.0;
    }

    private Config load() { return store.readObject("config.json", Config.class, new Config()); }
    private void persist(Config c) { store.writeObject("config.json", c); }

    @Override
    public void saveReserveBase(double base) { Config c = load(); c.reserveBase = base; persist(c); }

    @Override
    public double getReserveBase() { return load().reserveBase; }

    @Override
    public void saveReserveThresholdPercent(double percent) { Config c = load(); c.reserveThresholdPercent = percent; persist(c); }

    @Override
    public double getReserveThresholdPercent() { return load().reserveThresholdPercent; }

    @Override
    public void saveCurrentReserve(double current) { Config c = load(); c.currentReserve = current; persist(c); }

    @Override
    public double getCurrentReserve() { return load().currentReserve; }
}
