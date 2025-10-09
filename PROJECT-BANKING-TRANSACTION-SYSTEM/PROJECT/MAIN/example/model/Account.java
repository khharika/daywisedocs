package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private String accountId;
    private String customerId;
    private String accountType;
    private BigDecimal balance;
    private boolean isActive;
    private LocalDateTime createdAt;
    private String transactionPinHash; // hashed PIN

    public Account() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.balance = BigDecimal.ZERO;
    }

    public Account(String accountId, String customerId, String accountType, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // getters/setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getTransactionPinHash() { return transactionPinHash; }
    public void setTransactionPinHash(String transactionPinHash) { this.transactionPinHash = transactionPinHash; }

    @Override
    public String toString() {
        return String.format("Account{id=%s, type=%s, balance=%s, customer=%s}", accountId, accountType, balance, customerId);
    }
}
