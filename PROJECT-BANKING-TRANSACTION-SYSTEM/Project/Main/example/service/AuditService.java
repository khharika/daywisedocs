/*
package org.example.service;

import org.example.database.DatabaseConfig;
import org.example.database.DynamoDBConfig;
import org.example.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuditService {
    
    public boolean createAuditLog(String transactionId, String action, String details, String userId) {
        String logId = UUID.randomUUID().toString();
        AuditLog auditLog = new AuditLog(logId, transactionId, action, details, userId);
        
        // Save to SQL database
        boolean sqlSaved = saveToSqlDatabase(auditLog);
        
        // Save to DynamoDB (eventual consistency)
        saveToDynamoDB(auditLog);
        
        return sqlSaved;
    }
    
    private boolean saveToSqlDatabase(AuditLog auditLog) {
        String sql = "INSERT INTO audit_logs (log_id, transaction_id, action, details, user_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auditLog.getLogId());
            pstmt.setString(2, auditLog.getTransactionId());
            pstmt.setString(3, auditLog.getAction());
            pstmt.setString(4, auditLog.getDetails());
            pstmt.setString(5, auditLog.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving audit log to SQL: " + e.getMessage());
            return false;
        }
    }
    
    private void saveToDynamoDB(AuditLog auditLog) {
        try {
            String timestamp = auditLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            DynamoDBConfig.putAuditLog(
                auditLog.getLogId(),
                auditLog.getTransactionId(),
                auditLog.getAction(),
                auditLog.getDetails(),
                auditLog.getUserId(),
                timestamp
            );
        } catch (Exception e) {
            // DynamoDB not available - audit still saved to H2 database
        }
    }
    
    public List<AuditLog> getAuditLogsByTransactionId(String transactionId) {
        List<AuditLog> auditLogs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE transaction_id = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AuditLog auditLog = new AuditLog();
                auditLog.setLogId(rs.getString("log_id"));
                auditLog.setTransactionId(rs.getString("transaction_id"));
                auditLog.setAction(rs.getString("action"));
                auditLog.setDetails(rs.getString("details"));
                auditLog.setUserId(rs.getString("user_id"));
                auditLog.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                auditLogs.add(auditLog);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs: " + e.getMessage());
        }
        return auditLogs;
    }
    
    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> auditLogs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AuditLog auditLog = new AuditLog();
                auditLog.setLogId(rs.getString("log_id"));
                auditLog.setTransactionId(rs.getString("transaction_id"));
                auditLog.setAction(rs.getString("action"));
                auditLog.setDetails(rs.getString("details"));
                auditLog.setUserId(rs.getString("user_id"));
                auditLog.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                auditLogs.add(auditLog);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all audit logs: " + e.getMessage());
        }
        return auditLogs;
    }
}*/
package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.AuditLog;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

public class AuditService {

    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean createAuditLog(String transactionId, String action, String details, String userId) {
        try {
            String logId = UUID.randomUUID().toString();
            AuditLog log = new AuditLog(logId, transactionId, action, details, userId);

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("log_id", AttributeValue.builder().s(log.getLogId()).build());
            item.put("transaction_id", AttributeValue.builder().s(log.getTransactionId()).build());
            item.put("action", AttributeValue.builder().s(log.getAction()).build());
            item.put("details", AttributeValue.builder().s(log.getDetails()).build());
            item.put("user_id", AttributeValue.builder().s(log.getUserId()).build());
            item.put("timestamp", AttributeValue.builder().s(log.getTimestamp().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("audit_logs")
                    .item(item)
                    .build());

            System.out.println("✅ Audit log saved to DynamoDB: " + log.getLogId());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving audit log: " + e.getMessage());
            return false;
        }
    }
}
