/*
package org.example.service;

*/
/*import org.example.database.DatabaseConfig;*//*

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import org.example.model.AuditLog;
import org.example.model.Transaction;
import org.example.util.TransactionQueue;
import org.example.util.TransactionStack;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionService {
    private final AccountService accountService;
    private final AuditService auditService;
    private final TransactionQueue transactionQueue;
    private final TransactionStack transactionStack;

    public TransactionService() {
        this.accountService = new AccountService();
        this.auditService = new AuditService();
        this.transactionQueue = new TransactionQueue();
        this.transactionStack = new TransactionStack();
    }

    public boolean deposit(String accountId, BigDecimal amount, String description) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            Account account = accountService.getAccountById(accountId);
            if (account == null || !account.isActive()) {
                conn.rollback();
                return false;
            }

            String transactionId = UUID.randomUUID().toString();
            BigDecimal newBalance = account.getBalance().add(amount);

            // Create transaction record
            Transaction transaction = new Transaction(transactionId, null, accountId, 
                                                    amount, "DEPOSIT", description);
            
            if (saveTransaction(transaction, conn) && 
                accountService.updateBalance(accountId, newBalance)) {
                
                transaction.setStatus("COMPLETED");
                updateTransactionStatus(transactionId, "COMPLETED", conn);
                
                // Add to data structures
                transactionQueue.addCompletedTransaction(transaction);
                transactionStack.pushTransaction(transaction);
                
                // Create audit log
                auditService.createAuditLog(transactionId, "DEPOSIT", 
                    "Deposited " + amount + " to account " + accountId, "SYSTEM");
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error processing deposit: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean withdraw(String accountId, BigDecimal amount, String description) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            Account account = accountService.getAccountById(accountId);
            if (account == null || !account.isActive() || 
                account.getBalance().compareTo(amount) < 0) {
                conn.rollback();
                return false;
            }

            String transactionId = UUID.randomUUID().toString();
            BigDecimal newBalance = account.getBalance().subtract(amount);

            Transaction transaction = new Transaction(transactionId, accountId, null, 
                                                    amount, "WITHDRAWAL", description);
            
            if (saveTransaction(transaction, conn) && 
                accountService.updateBalance(accountId, newBalance)) {
                
                transaction.setStatus("COMPLETED");
                updateTransactionStatus(transactionId, "COMPLETED", conn);
                
                transactionQueue.addCompletedTransaction(transaction);
                transactionStack.pushTransaction(transaction);
                
                auditService.createAuditLog(transactionId, "WITHDRAWAL", 
                    "Withdrew " + amount + " from account " + accountId, "SYSTEM");
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            Account fromAccount = accountService.getAccountById(fromAccountId);
            Account toAccount = accountService.getAccountById(toAccountId);

            if (fromAccount == null || toAccount == null || 
                !fromAccount.isActive() || !toAccount.isActive() ||
                fromAccount.getBalance().compareTo(amount) < 0) {
                conn.rollback();
                return false;
            }

            String transactionId = UUID.randomUUID().toString();
            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal toNewBalance = toAccount.getBalance().add(amount);

            Transaction transaction = new Transaction(transactionId, fromAccountId, toAccountId, 
                                                    amount, "TRANSFER", description);
            
            if (saveTransaction(transaction, conn) && 
                accountService.updateBalance(fromAccountId, fromNewBalance) &&
                accountService.updateBalance(toAccountId, toNewBalance)) {
                
                transaction.setStatus("COMPLETED");
                updateTransactionStatus(transactionId, "COMPLETED", conn);
                
                transactionQueue.addCompletedTransaction(transaction);
                transactionStack.pushTransaction(transaction);
                
                auditService.createAuditLog(transactionId, "TRANSFER", 
                    "Transferred " + amount + " from " + fromAccountId + " to " + toAccountId, "SYSTEM");
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error processing transfer: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private boolean saveTransaction(Transaction transaction, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, from_account_id, to_account_id, amount, transaction_type, status, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getFromAccountId());
            pstmt.setString(3, transaction.getToAccountId());
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getTransactionType());
            pstmt.setString(6, transaction.getStatus());
            pstmt.setString(7, transaction.getDescription());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean updateTransactionStatus(String transactionId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, transactionId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account_id = ? OR to_account_id = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountId);
            pstmt.setString(2, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getString("transaction_id"));
                transaction.setFromAccountId(rs.getString("from_account_id"));
                transaction.setToAccountId(rs.getString("to_account_id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setStatus(rs.getString("status"));
                transaction.setDescription(rs.getString("description"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }
        return transactions;
    }

    public TransactionQueue getTransactionQueue() {
        return transactionQueue;
    }

    public TransactionStack getTransactionStack() {
        return transactionStack;
    }
}*/
package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import org.example.model.Transaction;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.math.BigDecimal;
import java.util.*;

public class TransactionService {
    private final AccountService accountService;
    private final AuditService auditService;
    private final DynamoDbClient dynamoDbClient;

    public TransactionService() {
        this.accountService = new AccountService();
        this.auditService = new AuditService();
        this.dynamoDbClient = DynamoDBConfig.getDynamoDbClient();
    }

    public boolean deposit(String accountId, BigDecimal amount, String description) {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account == null || !account.isActive()) {
                System.err.println("❌ Deposit failed: Account not found or inactive");
                return false;
            }

            BigDecimal newBalance = account.getBalance().add(amount);
            updateAccountBalance(accountId, newBalance);

            String transactionId = UUID.randomUUID().toString();
            saveTransaction(new Transaction(transactionId, null, accountId, amount, "DEPOSIT", description));

            auditService.createAuditLog(transactionId, "DEPOSIT",
                    "Deposited " + amount + " into account " + accountId, "SYSTEM");

            System.out.println("✅ Deposit successful: " + amount + " to " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error processing deposit: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountId, BigDecimal amount, String description) {
        try {
            Account account = accountService.getAccountById(accountId);
            if (account == null || !account.isActive()) {
                System.err.println("❌ Withdrawal failed: Account not found or inactive");
                return false;
            }
            if (account.getBalance().compareTo(amount) < 0) {
                System.err.println("❌ Withdrawal failed: Insufficient balance");
                return false;
            }

            BigDecimal newBalance = account.getBalance().subtract(amount);
            updateAccountBalance(accountId, newBalance);

            String transactionId = UUID.randomUUID().toString();
            saveTransaction(new Transaction(transactionId, accountId, null, amount, "WITHDRAWAL", description));

            auditService.createAuditLog(transactionId, "WITHDRAWAL",
                    "Withdrew " + amount + " from account " + accountId, "SYSTEM");

            System.out.println("✅ Withdrawal successful: " + amount + " from " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        try {
            Account fromAccount = accountService.getAccountById(fromAccountId);
            Account toAccount = accountService.getAccountById(toAccountId);

            if (fromAccount == null || toAccount == null || !fromAccount.isActive() || !toAccount.isActive()) {
                System.err.println("❌ Transfer failed: One or both accounts not found/inactive");
                return false;
            }
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                System.err.println("❌ Transfer failed: Insufficient balance");
                return false;
            }

            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal newToBalance = toAccount.getBalance().add(amount);

            updateAccountBalance(fromAccountId, newFromBalance);
            updateAccountBalance(toAccountId, newToBalance);

            String transactionId = UUID.randomUUID().toString();
            saveTransaction(new Transaction(transactionId, fromAccountId, toAccountId, amount, "TRANSFER", description));

            auditService.createAuditLog(transactionId, "TRANSFER",
                    "Transferred " + amount + " from " + fromAccountId + " to " + toAccountId, "SYSTEM");

            System.out.println("✅ Transfer successful: " + amount + " from " + fromAccountId + " to " + toAccountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error processing transfer: " + e.getMessage());
            return false;
        }
    }

    private void saveTransaction(Transaction transaction) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("transaction_id", AttributeValue.builder().s(transaction.getTransactionId()).build());
            item.put("from_account_id", AttributeValue.builder().s(
                    transaction.getFromAccountId() == null ? "" : transaction.getFromAccountId()).build());
            item.put("to_account_id", AttributeValue.builder().s(
                    transaction.getToAccountId() == null ? "" : transaction.getToAccountId()).build());
            item.put("amount", AttributeValue.builder().n(transaction.getAmount().toString()).build());
            item.put("transaction_type", AttributeValue.builder().s(transaction.getTransactionType()).build());
            item.put("status", AttributeValue.builder().s("COMPLETED").build());
            item.put("description", AttributeValue.builder().s(transaction.getDescription()).build());
            item.put("timestamp", AttributeValue.builder().s(transaction.getTimestamp().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("transactions")
                    .item(item)
                    .build());
        } catch (Exception e) {
            System.err.println("❌ Error saving transaction: " + e.getMessage());
        }
    }

    private void updateAccountBalance(String accountId, BigDecimal newBalance) {
        try {
            Map<String, AttributeValueUpdate> update = Map.of(
                    "balance", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().n(newBalance.toString()).build())
                            .action(AttributeAction.PUT)
                            .build()
            );

            dynamoDbClient.updateItem(UpdateItemRequest.builder()
                    .tableName("accounts")
                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                    .attributeUpdates(update)
                    .build());
        } catch (Exception e) {
            System.err.println("❌ Error updating account balance: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            ScanRequest scan = ScanRequest.builder()
                    .tableName("transactions")
                    .filterExpression("from_account_id = :id OR to_account_id = :id")
                    .expressionAttributeValues(Map.of(":id", AttributeValue.builder().s(accountId).build()))
                    .build();

            List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scan).items();
            for (Map<String, AttributeValue> item : items) {
                Transaction t = new Transaction();
                t.setTransactionId(item.get("transaction_id").s());
                t.setFromAccountId(item.get("from_account_id").s());
                t.setToAccountId(item.get("to_account_id").s());
                t.setAmount(new BigDecimal(item.get("amount").n()));
                t.setTransactionType(item.get("transaction_type").s());
                t.setStatus(item.get("status").s());
                t.setDescription(item.get("description").s());
                transactions.add(t);
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }
}
