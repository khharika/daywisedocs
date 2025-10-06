package com.banking.repository;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {
    private final DynamoDbClient dynamoDbClient;
    private static final String CUSTOMERS_TABLE = "Customers";
    private static final String ACCOUNTS_TABLE = "Accounts";
    private static final String TRANSACTIONS_TABLE = "Transactions";
    
    public DatabaseService() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        createTablesIfNotExist();
    }
    
    private void createTablesIfNotExist() {
        createCustomersTable();
        createAccountsTable();
        createTransactionsTable();
    }
    
    private void createCustomersTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(CUSTOMERS_TABLE)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("customerId")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("customerId")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
            dynamoDbClient.createTable(request);
        } catch (ResourceInUseException e) {
            // Table already exists
        }
    }
    
    private void createAccountsTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(ACCOUNTS_TABLE)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("accountId")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("accountId")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
            dynamoDbClient.createTable(request);
        } catch (ResourceInUseException e) {
            // Table already exists
        }
    }
    
    private void createTransactionsTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(TRANSACTIONS_TABLE)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("transactionId")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("transactionId")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
            dynamoDbClient.createTable(request);
        } catch (ResourceInUseException e) {
            // Table already exists
        }
    }
    
    public void saveCustomer(Customer customer) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("customerId", AttributeValue.builder().s(customer.getCustomerId()).build());
        item.put("name", AttributeValue.builder().s(customer.getName()).build());
        item.put("email", AttributeValue.builder().s(customer.getEmail()).build());
        item.put("phone", AttributeValue.builder().s(customer.getPhone()).build());
        if (customer.getPassword() != null) {
            item.put("password", AttributeValue.builder().s(customer.getPassword()).build());
        }
        if (customer.getSecurityQuestion() != null) {
            item.put("securityQuestion", AttributeValue.builder().s(customer.getSecurityQuestion()).build());
        }
        if (customer.getSecurityAnswer() != null) {
            item.put("securityAnswer", AttributeValue.builder().s(customer.getSecurityAnswer()).build());
        }
        
        PutItemRequest request = PutItemRequest.builder()
                .tableName(CUSTOMERS_TABLE)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }
    
    public void saveAccount(Account account) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("accountId", AttributeValue.builder().s(account.getAccountId()).build());
        item.put("customerId", AttributeValue.builder().s(account.getCustomerId()).build());
        item.put("accountType", AttributeValue.builder().s(account.getAccountType()).build());
        item.put("balance", AttributeValue.builder().s(account.getBalance().toString()).build());
        item.put("createdAt", AttributeValue.builder().s(account.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build());
        
        PutItemRequest request = PutItemRequest.builder()
                .tableName(ACCOUNTS_TABLE)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }
    
    public void updateAccountBalance(String accountId, BigDecimal newBalance) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("accountId", AttributeValue.builder().s(accountId).build());
        
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("balance", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(newBalance.toString()).build())
                .action(AttributeAction.PUT)
                .build());
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(ACCOUNTS_TABLE)
                .key(key)
                .attributeUpdates(updates)
                .build();
        dynamoDbClient.updateItem(request);
    }
    
    public Account getAccount(String accountId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("accountId", AttributeValue.builder().s(accountId).build());
        
        GetItemRequest request = GetItemRequest.builder()
                .tableName(ACCOUNTS_TABLE)
                .key(key)
                .build();
        
        GetItemResponse response = dynamoDbClient.getItem(request);
        if (response.hasItem()) {
            Map<String, AttributeValue> item = response.item();
            Account account = new Account();
            account.setAccountId(item.get("accountId").s());
            account.setCustomerId(item.get("customerId").s());
            account.setAccountType(item.get("accountType").s());
            account.setBalance(new BigDecimal(item.get("balance").s()));
            account.setCreatedAt(LocalDateTime.parse(item.get("createdAt").s(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return account;
        }
        return null;
    }
    
    public void saveTransaction(Transaction transaction) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("transactionId", AttributeValue.builder().s(transaction.getTransactionId()).build());
        item.put("accountId", AttributeValue.builder().s(transaction.getAccountId()).build());
        item.put("type", AttributeValue.builder().s(transaction.getType()).build());
        item.put("amount", AttributeValue.builder().s(transaction.getAmount().toString()).build());
        item.put("description", AttributeValue.builder().s(transaction.getDescription()).build());
        item.put("timestamp", AttributeValue.builder().s(transaction.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build());
        
        PutItemRequest request = PutItemRequest.builder()
                .tableName(TRANSACTIONS_TABLE)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }
    
    public List<Account> getAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(ACCOUNTS_TABLE)
                .filterExpression("customerId = :customerId")
                .expressionAttributeValues(Map.of(":customerId", AttributeValue.builder().s(customerId).build()))
                .build();
        
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : response.items()) {
            Account account = new Account();
            account.setAccountId(item.get("accountId").s());
            account.setCustomerId(item.get("customerId").s());
            account.setAccountType(item.get("accountType").s());
            account.setBalance(new BigDecimal(item.get("balance").s()));
            account.setCreatedAt(LocalDateTime.parse(item.get("createdAt").s(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            accounts.add(account);
        }
        return accounts;
    }
    
    public Customer getCustomerByEmail(String email) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(CUSTOMERS_TABLE)
                .filterExpression("email = :email")
                .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email).build()))
                .build();
        
        ScanResponse response = dynamoDbClient.scan(scanRequest);
        if (!response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().get(0);
            Customer customer = new Customer();
            customer.setCustomerId(item.get("customerId").s());
            customer.setName(item.get("name").s());
            customer.setEmail(item.get("email").s());
            customer.setPhone(item.get("phone").s());
            if (item.containsKey("password")) {
                customer.setPassword(item.get("password").s());
            }
            if (item.containsKey("securityQuestion")) {
                customer.setSecurityQuestion(item.get("securityQuestion").s());
            }
            if (item.containsKey("securityAnswer")) {
                customer.setSecurityAnswer(item.get("securityAnswer").s());
            }
            return customer;
        }
        return null;
    }
    
    public void updateCustomer(Customer customer) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder().s(customer.getCustomerId()).build());
        
        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("password", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(customer.getPassword()).build())
                .action(AttributeAction.PUT)
                .build());
        
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(CUSTOMERS_TABLE)
                .key(key)
                .attributeUpdates(updates)
                .build();
        dynamoDbClient.updateItem(request);
    }
}