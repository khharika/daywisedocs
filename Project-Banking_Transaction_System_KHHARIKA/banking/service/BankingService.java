package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.repository.DatabaseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class BankingService {
    private final DatabaseService databaseService;
    
    public BankingService() {
        this.databaseService = new DatabaseService();
    }
    
    public Customer createCustomer(String name, String email, String phone) {
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer(customerId, name, email, phone);
        databaseService.saveCustomer(customer);
        return customer;
    }
    
    public Customer registerCustomer(String name, String email, String phone, String password, String securityQuestion, String securityAnswer) {
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer(customerId, name, email, phone, password, securityQuestion, securityAnswer);
        databaseService.saveCustomer(customer);
        return customer;
    }
    
    public Account createAccount(String customerId, String accountType, BigDecimal initialBalance) {
        String accountId = "ACC-" + UUID.randomUUID().toString().substring(0, 8);
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        databaseService.saveAccount(account);
        
        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            Transaction initialDeposit = new Transaction(
                "TXN-" + UUID.randomUUID().toString().substring(0, 8),
                accountId,
                "DEPOSIT",
                initialBalance,
                "Initial deposit"
            );
            databaseService.saveTransaction(initialDeposit);
        }
        
        return account;
    }
    
    public boolean deposit(String accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Account account = databaseService.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        
        BigDecimal newBalance = account.getBalance().add(amount);
        databaseService.updateAccountBalance(accountId, newBalance);
        
        Transaction transaction = new Transaction(
            "TXN-" + UUID.randomUUID().toString().substring(0, 8),
            accountId,
            "DEPOSIT",
            amount,
            description
        );
        databaseService.saveTransaction(transaction);
        return true;
    }
    
    public boolean withdraw(String accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Account account = databaseService.getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: " + account.getBalance());
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        databaseService.updateAccountBalance(accountId, newBalance);
        
        Transaction transaction = new Transaction(
            "TXN-" + UUID.randomUUID().toString().substring(0, 8),
            accountId,
            "WITHDRAWAL",
            amount,
            description
        );
        databaseService.saveTransaction(transaction);
        return true;
    }
    
    public Account getAccount(String accountId) {
        return databaseService.getAccount(accountId);
    }
    
    public List<Account> getCustomerAccounts(String customerId) {
        return databaseService.getAccountsByCustomerId(customerId);
    }
}