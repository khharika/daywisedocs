package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private String accountId;
    private String customerId;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    
    public Account() {}
    
    public Account(String accountId, String customerId, String accountType, BigDecimal balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                '}';
    }
}