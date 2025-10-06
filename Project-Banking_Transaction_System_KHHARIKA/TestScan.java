import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.Map;

public class TestScan {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            // Test scan with filter (like your code)
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName("Accounts")
                    .filterExpression("customerId = :customerId")
                    .expressionAttributeValues(Map.of(":customerId", AttributeValue.builder().s("CUST-12345").build()))
                    .build();
            
            ScanResponse response = client.scan(scanRequest);
            System.out.println("Filtered scan results:");
            if (response.items().isEmpty()) {
                System.out.println("No items found");
            } else {
                for (Map<String, AttributeValue> item : response.items()) {
                    System.out.println("- AccountId: " + item.get("accountId").s());
                    System.out.println("  CustomerId: " + item.get("customerId").s());
                    System.out.println("  Balance: " + item.get("balance").s());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        client.close();
    }
}