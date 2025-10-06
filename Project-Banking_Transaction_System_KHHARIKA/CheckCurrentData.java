import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.Map;

public class CheckCurrentData {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("dummy", "dummy"))
                .build();
        
        try {
            // Check Customers
            ScanRequest customerScan = ScanRequest.builder().tableName("Customers").build();
            ScanResponse customerResponse = client.scan(customerScan);
            System.out.println("=== CUSTOMERS ===");
            System.out.println("Total customers: " + customerResponse.items().size());
            for (Map<String, AttributeValue> item : customerResponse.items()) {
                System.out.println("- " + item.get("name").s() + " (" + item.get("email").s() + ")");
            }
            
            // Check Accounts
            ScanRequest accountScan = ScanRequest.builder().tableName("Accounts").build();
            ScanResponse accountResponse = client.scan(accountScan);
            System.out.println("\n=== ACCOUNTS ===");
            System.out.println("Total accounts: " + accountResponse.items().size());
            for (Map<String, AttributeValue> item : accountResponse.items()) {
                System.out.println("- " + item.get("accountId").s() + " (Customer: " + item.get("customerId").s() + ", Balance: " + item.get("balance").s() + ")");
            }
            
            // Check Transactions
            ScanRequest transactionScan = ScanRequest.builder().tableName("Transactions").build();
            ScanResponse transactionResponse = client.scan(transactionScan);
            System.out.println("\n=== TRANSACTIONS ===");
            System.out.println("Total transactions: " + transactionResponse.items().size());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        client.close();
    }
}