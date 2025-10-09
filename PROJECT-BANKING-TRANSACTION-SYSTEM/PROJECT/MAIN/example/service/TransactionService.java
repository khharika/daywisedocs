package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Transaction;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionService {
    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();
    private final AuditService auditService = new AuditService();

    // ACID transfer using TransactWriteItems
    public boolean transfer(String fromAccountId, String toAccountId, BigDecimal amount, String description) {
        try {
            // Validate existence and balance
            Map<String, AttributeValue> from = getAccount(fromAccountId);
            Map<String, AttributeValue> to = getAccount(toAccountId);
            if (from == null || to == null) {
                System.err.println("❌ One or both accounts not found.");
                return false;
            }

            BigDecimal fromBal = new BigDecimal(from.get("balance").n());
            BigDecimal toBal = new BigDecimal(to.get("balance").n());
            if (fromBal.compareTo(amount) < 0) {
                System.err.println("❌ Insufficient funds.");
                return false;
            }

            BigDecimal newFrom = fromBal.subtract(amount);
            BigDecimal newTo = toBal.add(amount);

            // Build transact items (two updates)
            TransactWriteItemsRequest tx = TransactWriteItemsRequest.builder()
                    .transactItems(
                            TransactWriteItem.builder()
                                    .update(Update.builder()
                                            .tableName("accounts")
                                            .key(Map.of("account_id", AttributeValue.builder().s(fromAccountId).build()))
                                            .updateExpression("SET balance = :b")
                                            .expressionAttributeValues(Map.of(":b", AttributeValue.builder().n(newFrom.toPlainString()).build()))
                                            .build())
                                    .build(),
                            TransactWriteItem.builder()
                                    .update(Update.builder()
                                            .tableName("accounts")
                                            .key(Map.of("account_id", AttributeValue.builder().s(toAccountId).build()))
                                            .updateExpression("SET balance = :b")
                                            .expressionAttributeValues(Map.of(":b", AttributeValue.builder().n(newTo.toPlainString()).build()))
                                            .build())
                                    .build()
                    ).build();

            dynamoDbClient.transactWriteItems(tx);

            // Save transaction and audit
            String txnId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction t = new Transaction(txnId, fromAccountId, toAccountId, amount, "TRANSFER", description);
            saveTransaction(t);
            auditService.logAction(txnId, "TRANSFER", description, fromAccountId);
            System.out.println("✅ Transfer successful: " + amount + " from " + fromAccountId + " to " + toAccountId);
            return true;

        } catch (TransactionCanceledException e) {
            System.err.println("❌ Transaction canceled: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error during transfer: " + e.getMessage());
            return false;
        }
    }

    public boolean deposit(String accountId, BigDecimal amount, String description) {
        try {
            Map<String, AttributeValue> acc = getAccount(accountId);
            if (acc == null) {
                System.err.println("❌ Account not found.");
                return false;
            }
            BigDecimal balance = new BigDecimal(acc.get("balance").n()).add(amount);
            TransactWriteItemsRequest tx = TransactWriteItemsRequest.builder()
                    .transactItems(TransactWriteItem.builder()
                            .update(Update.builder()
                                    .tableName("accounts")
                                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                                    .updateExpression("SET balance = :b")
                                    .expressionAttributeValues(Map.of(":b", AttributeValue.builder().n(balance.toPlainString()).build()))
                                    .build())
                            .build())
                    .build();
            dynamoDbClient.transactWriteItems(tx);

            String txnId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction t = new Transaction(txnId, null, accountId, amount, "DEPOSIT", description);
            saveTransaction(t);
            auditService.logAction(txnId, "DEPOSIT", description, accountId);
            System.out.println("✅ Deposit successful: " + amount + " to " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Deposit failed: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountId, BigDecimal amount, String description) {
        try {
            Map<String, AttributeValue> acc = getAccount(accountId);
            if (acc == null) {
                System.err.println("❌ Account not found.");
                return false;
            }
            BigDecimal balance = new BigDecimal(acc.get("balance").n());
            if (balance.compareTo(amount) < 0) {
                System.err.println("❌ Insufficient balance.");
                return false;
            }
            BigDecimal newBal = balance.subtract(amount);
            TransactWriteItemsRequest tx = TransactWriteItemsRequest.builder()
                    .transactItems(TransactWriteItem.builder()
                            .update(Update.builder()
                                    .tableName("accounts")
                                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                                    .updateExpression("SET balance = :b")
                                    .expressionAttributeValues(Map.of(":b", AttributeValue.builder().n(newBal.toPlainString()).build()))
                                    .build())
                            .build())
                    .build();
            dynamoDbClient.transactWriteItems(tx);

            String txnId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction t = new Transaction(txnId, accountId, null, amount, "WITHDRAW", description);
            saveTransaction(t);
            auditService.logAction(txnId, "WITHDRAW", description, accountId);
            System.out.println("✅ Withdrawal successful: " + amount + " from " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Withdrawal failed: " + e.getMessage());
            return false;
        }
    }

    private Map<String, AttributeValue> getAccount(String accountId) {
        GetItemResponse r = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName("accounts")
                .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                .build());
        return r.hasItem() ? r.item() : null;
    }

    private void saveTransaction(Transaction t) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("transaction_id", AttributeValue.builder().s(t.getTransactionId()).build());
        item.put("from_account_id", AttributeValue.builder().s(t.getFromAccountId() == null ? "" : t.getFromAccountId()).build());
        item.put("to_account_id", AttributeValue.builder().s(t.getToAccountId() == null ? "" : t.getToAccountId()).build());
        item.put("amount", AttributeValue.builder().n(t.getAmount().toPlainString()).build());
        item.put("transaction_type", AttributeValue.builder().s(t.getTransactionType()).build());
        item.put("status", AttributeValue.builder().s(t.getStatus()).build());
        item.put("description", AttributeValue.builder().s(t.getDescription()).build());
        item.put("timestamp", AttributeValue.builder().s(LocalDateTime.now().toString()).build());

        dynamoDbClient.putItem(PutItemRequest.builder().tableName("transactions").item(item).build());
    }

    // Fetch transactions for account (both outgoing & incoming)
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            // outgoing
            ScanRequest fromScan = ScanRequest.builder()
                    .tableName("transactions")
                    .filterExpression("from_account_id = :id")
                    .expressionAttributeValues(Map.of(":id", AttributeValue.builder().s(accountId).build()))
                    .build();
            transactions.addAll(mapToTransactions(dynamoDbClient.scan(fromScan).items()));

            // incoming
            ScanRequest toScan = ScanRequest.builder()
                    .tableName("transactions")
                    .filterExpression("to_account_id = :id")
                    .expressionAttributeValues(Map.of(":id", AttributeValue.builder().s(accountId).build()))
                    .build();
            transactions.addAll(mapToTransactions(dynamoDbClient.scan(toScan).items()));

            transactions.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        } catch (Exception e) {
            System.err.println("❌ Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    private List<Transaction> mapToTransactions(List<Map<String, AttributeValue>> items) {
        List<Transaction> list = new ArrayList<>();
        for (Map<String, AttributeValue> it : items) {
            Transaction t = new Transaction();
            t.setTransactionId(it.get("transaction_id").s());
            t.setFromAccountId(it.getOrDefault("from_account_id", AttributeValue.builder().s("").build()).s());
            t.setToAccountId(it.getOrDefault("to_account_id", AttributeValue.builder().s("").build()).s());
            t.setAmount(new BigDecimal(it.get("amount").n()));
            t.setTransactionType(it.get("transaction_type").s());
            t.setStatus(it.get("status").s());
            t.setDescription(it.get("description").s());
            t.setTimestamp(LocalDateTime.parse(it.get("timestamp").s()));
            list.add(t);
        }
        return list;
    }
}
