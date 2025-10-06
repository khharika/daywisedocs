package com.banking.util;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

public class TableChecker {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            ListTablesResponse response = client.listTables(ListTablesRequest.builder().build());
            System.out.println("DynamoDB Local Tables:");
            if (response.tableNames().isEmpty()) {
                System.out.println("No tables found. Run your banking app first to create tables.");
            } else {
                response.tableNames().forEach(tableName -> 
                    System.out.println("- " + tableName));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Make sure DynamoDB Local is running on port 8000");
        }
        
        client.close();
    }
}