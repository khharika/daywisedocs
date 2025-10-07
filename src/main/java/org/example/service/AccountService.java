package org.example.service;

import org.example.exception.BankingException;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.repository.InMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final InMemoryRepository repository;
    private final AuditService auditService;

    public AccountService(InMemoryRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public Account createAccount(String customerId, Account.AccountType accountType) throws BankingException {
        Customer customer = repository.findCustomerById(customerId);
        if (customer == null) {
            throw new BankingException("Customer not found: " + customerId, "CUSTOMER_NOT_FOUND");
        }

        String accountId = UUID.randomUUID().toString();
        String accountNumber = generateAccountNumber();
        
        Account account = new Account(accountId, customerId, accountNumber, accountType);
        repository.saveAccount(account);
        
        auditService.logAccountAction(accountId, "CREATE", null, account.toString(), "SYSTEM");
        logger.info("Account created: {}", accountNumber);
        
        return account;
    }

    public Account findAccountById(String accountId) {
        return repository.findAccountById(accountId);
    }

    public Account findAccountByNumber(String accountNumber) {
        return repository.findAccountByNumber(accountNumber);
    }

    public List<Account> findAccountsByCustomerId(String customerId) {
        return repository.findAccountsByCustomerId(customerId);
    }

    public void updateAccountStatus(String accountId, Account.AccountStatus status, String userId) throws BankingException {
        Account account = repository.findAccountById(accountId);
        if (account == null) {
            throw new BankingException("Account not found: " + accountId, "ACCOUNT_NOT_FOUND");
        }

        Account.AccountStatus oldStatus = account.getStatus();
        account.setStatus(status);
        repository.saveAccount(account);
        
        auditService.logAccountAction(accountId, "STATUS_UPDATE", oldStatus.toString(), status.toString(), userId);
        logger.info("Account status updated: {} -> {}", oldStatus, status);
    }

    public BigDecimal getBalance(String accountId) throws BankingException {
        Account account = repository.findAccountById(accountId);
        if (account == null) {
            throw new BankingException("Account not found: " + accountId, "ACCOUNT_NOT_FOUND");
        }
        return account.getBalance();
    }

    public void updateBalance(String accountId, BigDecimal newBalance, String userId) throws BankingException {
        Account account = repository.findAccountById(accountId);
        if (account == null) {
            throw new BankingException("Account not found: " + accountId, "ACCOUNT_NOT_FOUND");
        }

        BigDecimal oldBalance = account.getBalance();
        account.setBalance(newBalance);
        repository.saveAccount(account);
        
        auditService.logAccountAction(accountId, "BALANCE_UPDATE", oldBalance.toString(), newBalance.toString(), userId);
        logger.info("Account balance updated: {} -> {}", oldBalance, newBalance);
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    public List<Account> getAllAccounts() {
        return repository.findAllAccounts();
    }
}