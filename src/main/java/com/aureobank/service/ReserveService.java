package com.aureobank.service;

import com.aureobank.dao.AuditDao;
import com.aureobank.dao.ConfigDao;
import java.time.Instant;

public class ReserveService {
    private final ConfigDao configDao;
    private final AuditDao auditDao;

    public ReserveService(ConfigDao configDao, AuditDao auditDao) {
        this.configDao = configDao; this.auditDao = auditDao;
        if (configDao.getCurrentReserve() == 0.0) {
            configDao.saveCurrentReserve(configDao.getReserveBase());
        }
    }

    public double getReserve() { return configDao.getCurrentReserve(); }
    public double getBase() { return configDao.getReserveBase(); }
    public double getThreshold() { return configDao.getReserveThresholdPercent(); }

    public void ensureCanDeposit(double amount) {
        double after = getReserve() - amount;
        if (after < 0) throw new IllegalStateException("Reserve would become negative");
    }

    public void onDeposit(double amount) {
        double after = getReserve() - amount;
        configDao.saveCurrentReserve(after);
        if (after < getBase() * getThreshold())
            auditDao.record("system", "LOW_RESERVE", "Reserve below threshold: " + after, Instant.now());
    }

    public void onWithdraw(double amount) {
        double after = getReserve() + amount;
        configDao.saveCurrentReserve(after);
    }
}
