package org.example.database;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.List;

public class DynamoDBConfig {

    private static DynamoDbClient dynamoDbClient;

    public static DynamoDbClient getDynamoDbClient() {
        if (dynamoDbClient == null) {
            dynamoDbClient = DynamoDbClient.builder()
                    .endpointOverride(URI.create("http://127.0.0.1:8000"))
                    .region(Region.US_EAST_1)
                    .build();

            System.out.println("✅ Connected to DynamoDB Local (http://127.0.0.1:8000)");
            createTablesIfNotExist();
        }
        return dynamoDbClient;
    }

    private static void createTablesIfNotExist() {
        List<String> existing = dynamoDbClient.listTables().tableNames();
        createTableIfMissing(existing, "customers", "customer_id");
        createTableIfMissing(existing, "accounts", "account_id");
        createTableIfMissing(existing, "transactions", "transaction_id");
        createTableIfMissing(existing, "audit_logs", "log_id");
    }

    private static void createTableIfMissing(List<String> existingTables, String tableName, String keyName) {
        if (!existingTables.contains(tableName)) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(AttributeDefinition.builder().attributeName(keyName).attributeType(ScalarAttributeType.S).build())
                    .keySchema(KeySchemaElement.builder().attributeName(keyName).keyType(KeyType.HASH).build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());
            System.out.println("✅ Created table: " + tableName);
        } else {
            System.out.println("ℹ️ Table already exists: " + tableName);
        }
    }
}
