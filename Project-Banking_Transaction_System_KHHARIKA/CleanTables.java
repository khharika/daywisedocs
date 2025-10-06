import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.Map;

public class CleanTables {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            // Delete all items from Customers table
            ScanRequest customerScan = ScanRequest.builder().tableName("Customers").build();
            ScanResponse customerResponse = client.scan(customerScan);
            for (Map<String, AttributeValue> item : customerResponse.items()) {
                DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                        .tableName("Customers")
                        .key(Map.of("customerId", item.get("customerId")))
                        .build();
                client.deleteItem(deleteRequest);
            }
            System.out.println("Cleaned Customers table: " + customerResponse.items().size() + " items deleted");
            
            // Delete all items from Accounts table
            ScanRequest accountScan = ScanRequest.builder().tableName("Accounts").build();
            ScanResponse accountResponse = client.scan(accountScan);
            for (Map<String, AttributeValue> item : accountResponse.items()) {
                DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                        .tableName("Accounts")
                        .key(Map.of("accountId", item.get("accountId")))
                        .build();
                client.deleteItem(deleteRequest);
            }
            System.out.println("Cleaned Accounts table: " + accountResponse.items().size() + " items deleted");
            
            // Delete all items from Transactions table
            ScanRequest transactionScan = ScanRequest.builder().tableName("Transactions").build();
            ScanResponse transactionResponse = client.scan(transactionScan);
            for (Map<String, AttributeValue> item : transactionResponse.items()) {
                DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                        .tableName("Transactions")
                        .key(Map.of("transactionId", item.get("transactionId")))
                        .build();
                client.deleteItem(deleteRequest);
            }
            System.out.println("Cleaned Transactions table: " + transactionResponse.items().size() + " items deleted");
            
            System.out.println("All tables cleaned! Ready for fresh data.");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        client.close();
    }
}