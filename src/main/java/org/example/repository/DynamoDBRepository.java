package org.example.repository;

import org.example.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DynamoDBRepository {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBRepository.class);
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "BankingAuditLogs";

    public DynamoDBRepository() {
        try {
            this.dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            createTableIfNotExists();
        } catch (Exception e) {
            logger.warn("DynamoDB not available, using in-memory storage: {}", e.getMessage());
            throw new RuntimeException("DynamoDB initialization failed", e);
        }
    }

    private void createTableIfNotExists() {
        try {
            DescribeTableRequest describeRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            dynamoDbClient.describeTable(describeRequest);
            logger.info("Table {} already exists", tableName);
        } catch (ResourceNotFoundException e) {
            createTable();
        }
    }

    private void createTable() {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("logId")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("logId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        dynamoDbClient.createTable(createTableRequest);
        logger.info("Created table: {}", tableName);
    }

    public void saveAuditLog(AuditLog auditLog) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("logId", AttributeValue.builder().s(auditLog.getLogId()).build());
            item.put("entityType", AttributeValue.builder().s(auditLog.getEntityType()).build());
            item.put("entityId", AttributeValue.builder().s(auditLog.getEntityId()).build());
            item.put("action", AttributeValue.builder().s(auditLog.getAction()).build());
            item.put("oldValue", AttributeValue.builder().s(auditLog.getOldValue() != null ? auditLog.getOldValue() : "").build());
            item.put("newValue", AttributeValue.builder().s(auditLog.getNewValue() != null ? auditLog.getNewValue() : "").build());
            item.put("userId", AttributeValue.builder().s(auditLog.getUserId()).build());
            item.put("timestamp", AttributeValue.builder().s(auditLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build());

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(putItemRequest);
            logger.info("Audit log saved to DynamoDB: {}", auditLog.getLogId());
        } catch (Exception e) {
            logger.error("Failed to save audit log to DynamoDB: {}", e.getMessage());
            throw new RuntimeException("Failed to save audit log", e);
        }
    }

    public void close() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
    }
}