package com.aureobank.model;

public class Card {
    private String id;
    private String userId;
    private String accountId;
    private String type; // DEBIT, CREDIT
    private String numberMasked;
    private boolean active;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNumberMasked() { return numberMasked; }
    public void setNumberMasked(String numberMasked) { this.numberMasked = numberMasked; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
