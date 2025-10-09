package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import org.example.util.SecurityUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.*;

public class AccountService {
    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean createAccount(Account account, String transactionPinPlain) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("account_id", AttributeValue.builder().s(account.getAccountId()).build());
            item.put("customer_id", AttributeValue.builder().s(account.getCustomerId()).build());
            item.put("account_type", AttributeValue.builder().s(account.getAccountType()).build());
            item.put("balance", AttributeValue.builder().n(account.getBalance().toPlainString()).build());
            item.put("is_active", AttributeValue.builder().bool(account.isActive()).build());
            item.put("created_at", AttributeValue.builder().s(account.getCreatedAt().toString()).build());
            if (transactionPinPlain != null && !transactionPinPlain.isBlank()) {
                item.put("transaction_pin", AttributeValue.builder().s(SecurityUtil.hashPin(transactionPinPlain)).build());
            }

            dynamoDbClient.putItem(PutItemRequest.builder().tableName("accounts").item(item).build());
            System.out.println("✅ Account saved to DynamoDB: " + account.getAccountId());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            return false;
        }
    }

    public Account getAccountById(String accountId) {
        try {
            GetItemResponse resp = dynamoDbClient.getItem(GetItemRequest.builder()
                    .tableName("accounts")
                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                    .build());
            if (!resp.hasItem()) return null;
            Map<String, AttributeValue> it = resp.item();
            Account a = new Account();
            a.setAccountId(it.get("account_id").s());
            a.setCustomerId(it.get("customer_id").s());
            a.setAccountType(it.get("account_type").s());
            a.setBalance(new BigDecimal(it.get("balance").n()));
            a.setActive(it.get("is_active").bool());
            a.setCreatedAt(java.time.LocalDateTime.parse(it.get("created_at").s()));
            if (it.containsKey("transaction_pin")) a.setTransactionPinHash(it.get("transaction_pin").s());
            return a;
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
            for (Map<String, AttributeValue> it : items) {
                Account a = new Account();
                a.setAccountId(it.get("account_id").s());
                a.setCustomerId(it.get("customer_id").s());
                a.setAccountType(it.get("account_type").s());
                a.setBalance(new BigDecimal(it.get("balance").n()));
                a.setActive(it.get("is_active").bool());
                a.setCreatedAt(java.time.LocalDateTime.parse(it.get("created_at").s()));
                if (it.containsKey("transaction_pin")) a.setTransactionPinHash(it.get("transaction_pin").s());
                accounts.add(a);
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving accounts: " + e.getMessage());
        }
        return accounts;
    }

    public boolean updateAccountPin(String accountId, String customerEmail, String newPinPlain) {
        try {
            // simple verification: ensure account exists
            Account a = getAccountById(accountId);
            if (a == null) {
                System.err.println("❌ Account not found: " + accountId);
                return false;
            }
            Map<String, AttributeValueUpdate> updates = Map.of("transaction_pin", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(SecurityUtil.hashPin(newPinPlain)).build())
                    .action(AttributeAction.PUT).build());
            dynamoDbClient.updateItem(UpdateItemRequest.builder()
                    .tableName("accounts")
                    .key(Map.of("account_id", AttributeValue.builder().s(accountId).build()))
                    .attributeUpdates(updates).build());
            System.out.println("✅ Account PIN updated for " + accountId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error updating account PIN: " + e.getMessage());
            return false;
        }
    }
}
