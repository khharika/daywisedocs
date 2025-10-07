package org.example.service;

import org.example.model.AuditLog;
import org.example.repository.DynamoDBRepository;
import org.example.repository.InMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final InMemoryRepository inMemoryRepository;
    private DynamoDBRepository dynamoDBRepository;

    public AuditService(InMemoryRepository inMemoryRepository) {
        this.inMemoryRepository = inMemoryRepository;
        try {
            this.dynamoDBRepository = new DynamoDBRepository();
        } catch (Exception e) {
            logger.warn("DynamoDB not available, using in-memory storage only");
            this.dynamoDBRepository = null;
        }
    }

    public void logAction(String entityType, String entityId, String action, 
                         String oldValue, String newValue, String userId) {
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID().toString(),
            entityType,
            entityId,
            action,
            oldValue,
            newValue,
            userId
        );

        // Always save to in-memory repository
        inMemoryRepository.saveAuditLog(auditLog);
        
        // Try to save to DynamoDB if available
        if (dynamoDBRepository != null) {
            try {
                dynamoDBRepository.saveAuditLog(auditLog);
                logger.info("Audit log saved to both in-memory and DynamoDB");
            } catch (Exception e) {
                logger.error("Failed to save audit log to DynamoDB, saved to in-memory only: {}", e.getMessage());
            }
        } else {
            logger.info("Audit log saved to in-memory repository only");
        }
    }

    public void logCustomerAction(String customerId, String action, String oldValue, String newValue, String userId) {
        logAction("CUSTOMER", customerId, action, oldValue, newValue, userId);
    }

    public void logAccountAction(String accountId, String action, String oldValue, String newValue, String userId) {
        logAction("ACCOUNT", accountId, action, oldValue, newValue, userId);
    }

    public void logTransactionAction(String transactionId, String action, String oldValue, String newValue, String userId) {
        logAction("TRANSACTION", transactionId, action, oldValue, newValue, userId);
    }

    public void close() {
        if (dynamoDBRepository != null) {
            dynamoDBRepository.close();
        }
    }
}