/*
package org.example.database;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DynamoDBConfig {
    private static DynamoDbClient dynamoDbClient;
    private static final String AUDIT_LOGS_TABLE = "audit_logs";

    public static DynamoDbClient getDynamoDbClient() {
        if (dynamoDbClient == null) {
            try {
                dynamoDbClient = DynamoDbClient.builder()
                        .endpointOverride(URI.create("http://localhost:8000"))
                        .region(Region.US_EAST_1)
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy")))
                        .build();
                
                // Test connection by listing tables
                dynamoDbClient.listTables();
                createAuditLogsTable();
            } catch (Exception e) {
                System.out.println("DynamoDB Local not available: " + e.getMessage());
                dynamoDbClient = null;
                throw e;
            }
        }
        return dynamoDbClient;
    }

    private static void createAuditLogsTable() {
        try {
            System.out.println("Attempting to create DynamoDB table: " + AUDIT_LOGS_TABLE);
            
            // First check if table exists
            try {
                DescribeTableRequest describeRequest = DescribeTableRequest.builder()
                        .tableName(AUDIT_LOGS_TABLE)
                        .build();
                dynamoDbClient.describeTable(describeRequest);
                System.out.println("DynamoDB table already exists: " + AUDIT_LOGS_TABLE);
                return;
            } catch (ResourceNotFoundException e) {
                System.out.println("Table does not exist, creating new table...");
            }
            
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(AUDIT_LOGS_TABLE)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("log_id")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("log_id")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            CreateTableResponse response = dynamoDbClient.createTable(request);
            System.out.println("Created DynamoDB table: " + AUDIT_LOGS_TABLE);
            System.out.println("Table status: " + response.tableDescription().tableStatus());
        } catch (Exception e) {
            System.err.println("Error creating DynamoDB table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void putAuditLog(String logId, String transactionId, String action, 
                                  String details, String userId, String timestamp) {
        try {
            DynamoDbClient client = getDynamoDbClient();
            if (client == null) {
                return; // Skip DynamoDB if not available
            }
            
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("log_id", AttributeValue.builder().s(logId).build());
            item.put("transaction_id", AttributeValue.builder().s(transactionId != null ? transactionId : "").build());
            item.put("action", AttributeValue.builder().s(action).build());
            item.put("details", AttributeValue.builder().s(details != null ? details : "").build());
            item.put("user_id", AttributeValue.builder().s(userId != null ? userId : "").build());
            item.put("timestamp", AttributeValue.builder().s(timestamp).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(AUDIT_LOGS_TABLE)
                    .item(item)
                    .build();

            client.putItem(request);
        } catch (Exception e) {
            // Silently fail DynamoDB operations - audit still saved to H2
        }
    }

    public static void putCustomer(String customerId, String name, String email, String phone, String password) {
        try {
            // Create customers table if not exists
            createCustomersTable();
            
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("name", AttributeValue.builder().s(name).build());
            item.put("email", AttributeValue.builder().s(email).build());
            item.put("phone", AttributeValue.builder().s(phone).build());
            item.put("password", AttributeValue.builder().s(password).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("customers")
                    .item(item)
                    .build();

            getDynamoDbClient().putItem(request);
        } catch (Exception e) {
            System.err.println("Error putting customer to DynamoDB: " + e.getMessage());
        }
    }
    
    public static void putAccount(String accountId, String customerId, String accountType, String balance) {
        try {
            // Create accounts table if not exists
            createAccountsTable();
            
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("account_id", AttributeValue.builder().s(accountId).build());
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("account_type", AttributeValue.builder().s(accountType).build());
            item.put("balance", AttributeValue.builder().s(balance).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("accounts")
                    .item(item)
                    .build();

            getDynamoDbClient().putItem(request);
        } catch (Exception e) {
            System.err.println("Error putting account to DynamoDB: " + e.getMessage());
        }
    }
    
    private static void createCustomersTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("customers")
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("customer_id")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("customer_id")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            getDynamoDbClient().createTable(request);
            System.out.println("Created DynamoDB customers table");
        } catch (ResourceInUseException e) {
            // Table already exists
        } catch (Exception e) {
            System.err.println("Error creating customers table: " + e.getMessage());
        }
    }
    
    private static void createAccountsTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("accounts")
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("account_id")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("account_id")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            getDynamoDbClient().createTable(request);
            System.out.println("Created DynamoDB accounts table");
        } catch (ResourceInUseException e) {
            // Table already exists
        } catch (Exception e) {
            System.err.println("Error creating accounts table: " + e.getMessage());
        }
    }

    public static String getAuditLogsTableName() {
        return AUDIT_LOGS_TABLE;
    }
}
*/

package org.example.database;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DynamoDBConfig {

    private static DynamoDbClient dynamoDbClient;

    public static DynamoDbClient getDynamoDbClient() {
        if (dynamoDbClient == null) {
            try {
                dynamoDbClient = DynamoDbClient.builder()
                        .endpointOverride(URI.create("http://127.0.0.1:8000")) // Use 127.0.0.1 for reliability
                        .region(Region.US_EAST_1)
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy")))
                        .build();

                // Test connection
                dynamoDbClient.listTables();
                System.out.println("✅ Connected to DynamoDB Local (http://127.0.0.1:8000)");

                // Create all tables if not exist
                createCustomersTable();
                createAccountsTable();
                createTransactionsTable();
                createAuditLogsTable();

            } catch (Exception e) {
                System.err.println("❌ DynamoDB initialization failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return dynamoDbClient;
    }

    // -------------------- TABLE CREATION METHODS --------------------

    private static void createCustomersTable() {
        createTableIfNotExists("customers", "customer_id", ScalarAttributeType.S);
    }

    private static void createAccountsTable() {
        createTableIfNotExists("accounts", "account_id", ScalarAttributeType.S);
    }

    private static void createTransactionsTable() {
        createTableIfNotExists("transactions", "transaction_id", ScalarAttributeType.S);
    }

    private static void createAuditLogsTable() {
        createTableIfNotExists("audit_logs", "log_id", ScalarAttributeType.S);
    }

    private static void createTableIfNotExists(String tableName, String keyName, ScalarAttributeType keyType) {
        try {
            DescribeTableResponse describe = dynamoDbClient.describeTable(
                    DescribeTableRequest.builder().tableName(tableName).build());
            System.out.println("ℹ️ Table already exists: " + tableName);
        } catch (ResourceNotFoundException e) {
            System.out.println("🛠️ Creating table: " + tableName);
            try {
                CreateTableRequest request = CreateTableRequest.builder()
                        .tableName(tableName)
                        .keySchema(KeySchemaElement.builder()
                                .attributeName(keyName)
                                .keyType(KeyType.HASH)
                                .build())
                        .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName(keyName)
                                .attributeType(keyType)
                                .build())
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build();

                dynamoDbClient.createTable(request);
                waitForTableToBecomeActive(tableName);
                System.out.println("✅ Created DynamoDB table: " + tableName);

            } catch (Exception ex) {
                System.err.println("❌ Error creating table " + tableName + ": " + ex.getMessage());
            }
        }
    }

    private static void waitForTableToBecomeActive(String tableName) {
        boolean active = false;
        System.out.println("⏳ Waiting for " + tableName + " to become ACTIVE...");
        while (!active) {
            try {
                DescribeTableResponse response = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(tableName).build());
                String status = response.table().tableStatusAsString();
                if ("ACTIVE".equals(status)) {
                    active = true;
                    System.out.println("✅ Table " + tableName + " is ACTIVE");
                } else {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    // -------------------- PUT OPERATIONS --------------------

    /*public static void putCustomer(String customerId, String name, String email, String phone, String password) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("name", AttributeValue.builder().s(name).build());
            item.put("email", AttributeValue.builder().s(email).build());
            item.put("phone", AttributeValue.builder().s(phone).build());
            item.put("password", AttributeValue.builder().s(password).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("customers")
                    .item(item)
                    .build());

            System.out.println("✅ Customer saved to DynamoDB: " + customerId);
        } catch (Exception e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
        }
    }

    public static void putAccount(String accountId, String customerId, String accountType, String balance) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("account_id", AttributeValue.builder().s(accountId).build());
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("account_type", AttributeValue.builder().s(accountType).build());
            item.put("balance", AttributeValue.builder().s(balance).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("accounts")
                    .item(item)
                    .build());

            System.out.println("✅ Account saved to DynamoDB: " + accountId);
        } catch (Exception e) {
            System.err.println("❌ Error saving account: " + e.getMessage());
        }
    }

    public static void putAuditLog(String logId, String transactionId, String action,
                                   String details, String userId, String timestamp) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("log_id", AttributeValue.builder().s(logId).build());
            item.put("transaction_id", AttributeValue.builder().s(transactionId).build());
            item.put("action", AttributeValue.builder().s(action).build());
            item.put("details", AttributeValue.builder().s(details).build());
            item.put("user_id", AttributeValue.builder().s(userId).build());
            item.put("timestamp", AttributeValue.builder().s(timestamp).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("audit_logs")
                    .item(item)
                    .build());

            System.out.println("✅ Audit log saved to DynamoDB: " + logId);
        } catch (Exception e) {
            System.err.println("❌ Error saving audit log: " + e.getMessage());
        }
    }
}

*/
    public static void putCustomer(String customerId, String name, String email, String phone, String password) {
        try {
            DynamoDbClient client = getDynamoDbClient(); // ✅ ensures it's initialized
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("name", AttributeValue.builder().s(name).build());
            item.put("email", AttributeValue.builder().s(email).build());
            item.put("phone", AttributeValue.builder().s(phone).build());
            item.put("password", AttributeValue.builder().s(password).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            client.putItem(PutItemRequest.builder()
                    .tableName("customers")
                    .item(item)
                    .build());

            System.out.println("✅ Customer saved to DynamoDB: " + customerId);
        } catch (Exception e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
        }
    }

    public static void putAccount(String accountId, String customerId, String accountType, String balance) {
        try {
            DynamoDbClient client = getDynamoDbClient(); // ✅ ensures it's initialized
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("account_id", AttributeValue.builder().s(accountId).build());
            item.put("customer_id", AttributeValue.builder().s(customerId).build());
            item.put("account_type", AttributeValue.builder().s(accountType).build());
            item.put("balance", AttributeValue.builder().s(balance).build());
            item.put("created_at", AttributeValue.builder().s(java.time.LocalDateTime.now().toString()).build());

            client.putItem(PutItemRequest.builder()
                    .tableName("accounts")
                    .item(item)
                    .build());

            System.out.println("✅ Account saved to DynamoDB: " + accountId);
        } catch (Exception e) {
            System.err.println("❌ Error saving account: " + e.getMessage());
        }
    }

    public static void putAuditLog(String logId, String transactionId, String action,
                                   String details, String userId, String timestamp) {
        try {
            DynamoDbClient client = getDynamoDbClient(); // ✅ ensures it's initialized
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("log_id", AttributeValue.builder().s(logId).build());
            item.put("transaction_id", AttributeValue.builder().s(transactionId).build());
            item.put("action", AttributeValue.builder().s(action).build());
            item.put("details", AttributeValue.builder().s(details).build());
            item.put("user_id", AttributeValue.builder().s(userId).build());
            item.put("timestamp", AttributeValue.builder().s(timestamp).build());

            client.putItem(PutItemRequest.builder()
                    .tableName("audit_logs")
                    .item(item)
                    .build());

            System.out.println("✅ Audit log saved to DynamoDB: " + logId);
        } catch (Exception e) {
            System.err.println("❌ Error saving audit log: " + e.getMessage());
        }
    }
}