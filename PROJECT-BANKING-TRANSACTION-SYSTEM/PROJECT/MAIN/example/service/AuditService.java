package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.AuditLog;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditService {
    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean logAction(String transactionId, String action, String details, String userId) {
        try {
            String logId = "LOG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("log_id", AttributeValue.builder().s(logId).build());
            item.put("transaction_id", AttributeValue.builder().s(transactionId).build());
            item.put("action", AttributeValue.builder().s(action).build());
            item.put("details", AttributeValue.builder().s(details).build());
            item.put("user_id", AttributeValue.builder().s(userId).build());
            item.put("timestamp", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder().tableName("audit_logs").item(item).build());
            System.out.println("✅ Audit log saved: " + logId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving audit log: " + e.getMessage());
            return false;
        }
    }
}
