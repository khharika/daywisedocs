/*
package org.example.service;

*/
/*import org.example.database.DatabaseConfig;*//*

import org.example.database.DynamoDBConfig;
import org.example.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    
    public boolean createAccount(Account account) {
        // Save to H2 database
        String sql = "INSERT INTO accounts (account_id, customer_id, account_type, balance) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountId());
            pstmt.setString(2, account.getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setBigDecimal(4, account.getBalance());
            
            int result = pstmt.executeUpdate();
            System.out.println("✓ Account saved to H2 database: " + (result > 0 ? "SUCCESS" : "FAILED"));
            
            // ALSO save to DynamoDB
            saveToDynamoDB(account);
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void saveToDynamoDB(Account account) {
        try {
            DynamoDBConfig.putAccount(
                account.getAccountId(),
                account.getCustomerId(),
                account.getAccountType(),
                account.getBalance().toString()
            );
            System.out.println("✓ Account saved to DynamoDB: " + account.getAccountId());
        } catch (Exception e) {
            System.err.println("Failed to save account to DynamoDB: " + e.getMessage());
        }
    }
    
    public Account getAccountById(String accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getString("account_id"));
                account.setCustomerId(rs.getString("customer_id"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setActive(rs.getBoolean("is_active"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return account;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }
    
    public List<Account> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getString("account_id"));
                account.setCustomerId(rs.getString("customer_id"));
                account.setAccountType(rs.getString("account_type"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setActive(rs.getBoolean("is_active"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts: " + e.getMessage());
        }
        return accounts;
    }
    
    public boolean updateBalance(String accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setString(2, accountId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }
}*/
package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.math.BigDecimal;
import java.util.*;

public class AccountService {

    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean createAccount(Account account) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("account_id", AttributeValue.builder().s(account.getAccountId()).build());
            item.put("customer_id", AttributeValue.builder().s(account.getCustomerId()).build());
            item.put("account_type", AttributeValue.builder().s(account.getAccountType()).build());
            item.put("balance", AttributeValue.builder().n(account.getBalance().toString()).build());
            item.put("is_active", AttributeValue.builder().bool(account.isActive()).build());
            item.put("created_at", AttributeValue.builder().s(account.getCreatedAt().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("accounts")
                    .item(item)
                    .build());

            System.out.println("✅ Account saved to DynamoDB: " + account.getAccountId());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            return false;
        }
    }

    public Account getAccountById(String accountId) {
        try {
            GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                    .tableName("accounts")
                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                    .build());

            if (!response.hasItem()) return null;
            return mapToAccount(response.item());
        } catch (Exception e) {
            System.err.println("❌ Error retrieving account: " + e.getMessage());
            return null;
        }
    }

    public List<Account> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        try {
            ScanRequest scan = ScanRequest.builder()
                    .tableName("accounts")
                    .filterExpression("customer_id = :cid")
                    .expressionAttributeValues(Map.of(":cid", AttributeValue.builder().s(customerId).build()))
                    .build();

            List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scan).items();
            for (Map<String, AttributeValue> item : items) {
                accounts.add(mapToAccount(item));
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving accounts: " + e.getMessage());
        }
        return accounts;
    }

    private Account mapToAccount(Map<String, AttributeValue> item) {
        Account account = new Account();
        account.setAccountId(item.get("account_id").s());
        account.setCustomerId(item.get("customer_id").s());
        account.setAccountType(item.get("account_type").s());
        account.setBalance(new BigDecimal(item.get("balance").n()));
        account.setActive(item.get("is_active").bool());
        return account;
    }
}
