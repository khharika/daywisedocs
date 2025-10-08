package org.example.model;

import java.time.LocalDateTime;

public class AuditLog {
    private String logId;
    private String transactionId;
    private String action;
    private String details;
    private String userId;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String logId, String transactionId, String action, String details, String userId) {
        this.logId = logId;
        this.transactionId = transactionId;
        this.action = action;
        this.details = details;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("AuditLog{id='%s', transactionId='%s', action='%s', timestamp=%s}", 
                           logId, transactionId, action, timestamp);
    }
}