package Day31;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.net.URI;
import java.util.Map;

public class ScanTable {
    public static void main(String[] args) {

        // AWS credentials (fake for local DynamoDB)
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("fakeaccess", "fakeaccess");

        // Create DynamoDB client
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        String tableName = "Employees04";

        // Scan the table
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse response = client.scan(scanRequest);

        System.out.println("✅ Connected to DynamoDB Local");
        System.out.println("\n📋 Employees in Table:");

        // Loop through all items safely
        for (Map<String, AttributeValue> item : response.items()) {
            String id = item.get("ID") != null ? item.get("ID").n() : "N/A";
            String name = item.get("Name") != null ? item.get("Name").s() : "N/A";
            String address = item.get("Address") != null ? item.get("Address").s() : "N/A";

            System.out.println("ID=" + id + ", Name=" + name + ", Address=" + address);
        }

        client.close();
    }
}
