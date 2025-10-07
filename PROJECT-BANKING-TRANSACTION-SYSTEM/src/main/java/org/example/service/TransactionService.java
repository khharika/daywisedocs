package org.example.service;

import org.example.exception.BankingException;
import org.example.model.Account;
import org.example.model.Transaction;
import org.example.repository.InMemoryRepository;
import org.example.util.TransactionQueue;
import org.example.util.TransactionStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final InMemoryRepository repository;
    private final AccountService accountService;
    private final AuditService auditService;
    private final TransactionQueue transactionQueue;
    private final TransactionStack transactionStack;
    private final ReentrantLock transactionLock = new ReentrantLock();

    public TransactionService(InMemoryRepository repository, AccountService accountService, AuditService auditService) {
        this.repository = repository;
        this.accountService = accountService;
        this.auditService = auditService;
        this.transactionQueue = new TransactionQueue();
        this.transactionStack = new TransactionStack();
    }

    public Transaction deposit(String accountId, BigDecimal amount, String description) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Deposit amount must be positive", "INVALID_AMOUNT");
        }

        Account account = accountService.findAccountById(accountId);
        if (account == null) {
            throw new BankingException("Account not found: " + accountId, "ACCOUNT_NOT_FOUND");
        }

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BankingException("Account is not active", "ACCOUNT_INACTIVE");
        }

        transactionLock.lock();
        try {
            String transactionId = UUID.randomUUID().toString();
            Transaction transaction = new Transaction(transactionId, null, accountId, amount, 
                                                    Transaction.TransactionType.DEPOSIT, description);
            
            // Add to queue for processing
            transactionQueue.enqueue(transaction);
            
            // Process the transaction
            BigDecimal newBalance = account.getBalance().add(amount);
            accountService.updateBalance(accountId, newBalance, "SYSTEM");
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            repository.saveTransaction(transaction);
            
            // Add to stack for history
            transactionStack.push(transaction);
            
            auditService.logTransactionAction(transactionId, "DEPOSIT", "0", amount.toString(), "SYSTEM");
            logger.info("Deposit completed: {} to account {}", amount, accountId);
            
            return transaction;
        } finally {
            transactionLock.unlock();
        }
    }

    public Transaction withdraw(String accountId, BigDecimal amount, String description) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Withdrawal amount must be positive", "INVALID_AMOUNT");
        }

        Account account = accountService.findAccountById(accountId);
        if (account == null) {
            throw new BankingException("Account not found: " + accountId, "ACCOUNT_NOT_FOUND");
        }

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BankingException("Account is not active", "ACCOUNT_INACTIVE");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BankingException("Insufficient funds", "INSUFFICIENT_FUNDS");
        }

        transactionLock.lock();
        try {
            String transactionId = UUID.randomUUID().toString();
            Transaction transaction = new Transaction(transactionId, accountId, null, amount, 
                                                    Transaction.TransactionType.WITHDRAWAL, description);
            
            transactionQueue.enqueue(transaction);
            
            BigDecimal newBalance = account.getBalance().subtract(amount);
            accountService.updateBalance(accountId, newBalance, "SYSTEM");
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            repository.saveTransaction(transaction);
            
            transactionStack.push(transaction);
            
            auditService.logTransactionAction(transactionId, "WITHDRAWAL", amount.toString(), "0", "SYSTEM");
            logger.info("Withdrawal completed: {} from account {}", amount, accountId);
            
            return transaction;
        } finally {
            transactionLock.unlock();
        }
    }

    public Transaction transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Transfer amount must be positive", "INVALID_AMOUNT");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new BankingException("Cannot transfer to the same account", "INVALID_TRANSFER");
        }

        Account fromAccount = accountService.findAccountById(fromAccountId);
        Account toAccount = accountService.findAccountById(toAccountId);

        if (fromAccount == null) {
            throw new BankingException("Source account not found: " + fromAccountId, "ACCOUNT_NOT_FOUND");
        }
        if (toAccount == null) {
            throw new BankingException("Destination account not found: " + toAccountId, "ACCOUNT_NOT_FOUND");
        }

        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BankingException("Source account is not active", "ACCOUNT_INACTIVE");
        }
        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BankingException("Destination account is not active", "ACCOUNT_INACTIVE");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BankingException("Insufficient funds in source account", "INSUFFICIENT_FUNDS");
        }

        transactionLock.lock();
        try {
            String transactionId = UUID.randomUUID().toString();
            Transaction transaction = new Transaction(transactionId, fromAccountId, toAccountId, amount, 
                                                    Transaction.TransactionType.TRANSFER, description);
            
            transactionQueue.enqueue(transaction);
            
            // ACID transaction - both operations must succeed
            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal toNewBalance = toAccount.getBalance().add(amount);
            
            accountService.updateBalance(fromAccountId, fromNewBalance, "SYSTEM");
            accountService.updateBalance(toAccountId, toNewBalance, "SYSTEM");
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            repository.saveTransaction(transaction);
            
            transactionStack.push(transaction);
            
            auditService.logTransactionAction(transactionId, "TRANSFER", 
                fromAccountId + ":" + amount, toAccountId + ":" + amount, "SYSTEM");
            logger.info("Transfer completed: {} from {} to {}", amount, fromAccountId, toAccountId);
            
            return transaction;
        } finally {
            transactionLock.unlock();
        }
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        return repository.findTransactionsByAccountId(accountId);
    }

    public Transaction getLastTransaction() {
        return transactionStack.peek();
    }

    public Transaction getNextPendingTransaction() {
        return transactionQueue.peek();
    }

    public int getPendingTransactionCount() {
        return transactionQueue.size();
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAllTransactions();
    }
}