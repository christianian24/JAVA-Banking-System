package com.aureobank.dao;

public interface ConfigDao {
    void saveReserveBase(double base);
    double getReserveBase();
    void saveReserveThresholdPercent(double percent);
    double getReserveThresholdPercent();
    void saveCurrentReserve(double current);
    double getCurrentReserve();
}
