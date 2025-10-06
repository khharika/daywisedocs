import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.HashMap;
import java.util.Map;

public class TestData {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            // Add test customer
            Map<String, AttributeValue> customer = new HashMap<>();
            customer.put("customerId", AttributeValue.builder().s("CUST-12345").build());
            customer.put("name", AttributeValue.builder().s("John Doe").build());
            customer.put("email", AttributeValue.builder().s("john@example.com").build());
            customer.put("phone", AttributeValue.builder().s("123-456-7890").build());
            
            PutItemRequest customerRequest = PutItemRequest.builder()
                    .tableName("Customers")
                    .item(customer)
                    .build();
            client.putItem(customerRequest);
            System.out.println("Added customer: John Doe");
            
            // Add test account
            Map<String, AttributeValue> account = new HashMap<>();
            account.put("accountId", AttributeValue.builder().s("ACC-67890").build());
            account.put("customerId", AttributeValue.builder().s("CUST-12345").build());
            account.put("accountType", AttributeValue.builder().s("SAVINGS").build());
            account.put("balance", AttributeValue.builder().s("1000.00").build());
            account.put("createdAt", AttributeValue.builder().s("2024-01-01T10:00:00").build());
            
            PutItemRequest accountRequest = PutItemRequest.builder()
                    .tableName("Accounts")
                    .item(account)
                    .build();
            client.putItem(accountRequest);
            System.out.println("Added account: ACC-67890");
            
            // Scan accounts table
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName("Accounts")
                    .build();
            
            ScanResponse scanResponse = client.scan(scanRequest);
            System.out.println("Accounts in table:");
            for (Map<String, AttributeValue> item : scanResponse.items()) {
                System.out.println("- AccountId: " + item.get("accountId").s());
                System.out.println("  CustomerId: " + item.get("customerId").s());
                System.out.println("  Balance: " + item.get("balance").s());
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        client.close();
    }
}