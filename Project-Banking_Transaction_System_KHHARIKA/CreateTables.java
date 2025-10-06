import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public class CreateTables {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            // Create Customers table
            CreateTableRequest customersRequest = CreateTableRequest.builder()
                    .tableName("Customers")
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
            client.createTable(customersRequest);
            System.out.println("Created Customers table");
            
            // Create Accounts table
            CreateTableRequest accountsRequest = CreateTableRequest.builder()
                    .tableName("Accounts")
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
            client.createTable(accountsRequest);
            System.out.println("Created Accounts table");
            
            // Create Transactions table
            CreateTableRequest transactionsRequest = CreateTableRequest.builder()
                    .tableName("Transactions")
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
            client.createTable(transactionsRequest);
            System.out.println("Created Transactions table");
            
            // List tables
            ListTablesResponse response = client.listTables();
            System.out.println("Tables created:");
            response.tableNames().forEach(name -> System.out.println("- " + name));
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        client.close();
    }
}