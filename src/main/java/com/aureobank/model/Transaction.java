package com.aureobank.model;

import java.time.Instant;

public class Transaction {
    private String id;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER, BILLPAY
    private String fromAccountId; // nullable
    private String toAccountId;   // nullable
    private double amount;
    private Instant timestamp;
    private String actorUserId;
    private String note;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getActorUserId() { return actorUserId; }
    public void setActorUserId(String actorUserId) { this.actorUserId = actorUserId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
