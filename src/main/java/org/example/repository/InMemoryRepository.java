package org.example.repository;

import org.example.model.Account;
import org.example.model.AuditLog;
import org.example.model.Customer;
import org.example.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository {
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private final List<AuditLog> auditLogs = new ArrayList<>();

    // Customer operations
    public void saveCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Customer findCustomerById(String customerId) {
        return customers.get(customerId);
    }

    public List<Customer> findAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // Account operations
    public void saveAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Account findAccountById(String accountId) {
        return accounts.get(accountId);
    }

    public Account findAccountByNumber(String accountNumber) {
        return accounts.values().stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public List<Account> findAccountsByCustomerId(String customerId) {
        return accounts.values().stream()
                .filter(account -> account.getCustomerId().equals(customerId))
                .toList();
    }

    public List<Account> findAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // Transaction operations
    public void saveTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }

    public Transaction findTransactionById(String transactionId) {
        return transactions.get(transactionId);
    }

    public List<Transaction> findTransactionsByAccountId(String accountId) {
        return transactions.values().stream()
                .filter(transaction -> accountId.equals(transaction.getFromAccountId()) || 
                                     accountId.equals(transaction.getToAccountId()))
                .toList();
    }

    public List<Transaction> findAllTransactions() {
        return new ArrayList<>(transactions.values());
    }

    // Audit log operations
    public synchronized void saveAuditLog(AuditLog auditLog) {
        auditLogs.add(auditLog);
    }

    public synchronized List<AuditLog> findAllAuditLogs() {
        return new ArrayList<>(auditLogs);
    }

    public synchronized List<AuditLog> findAuditLogsByEntityId(String entityId) {
        return auditLogs.stream()
                .filter(log -> log.getEntityId().equals(entityId))
                .toList();
    }
}