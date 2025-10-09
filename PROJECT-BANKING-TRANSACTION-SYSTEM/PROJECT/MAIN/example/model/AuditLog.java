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

    // getters/setters omitted for brevity if not needed
}
