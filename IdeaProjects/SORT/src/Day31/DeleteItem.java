package Day31;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
public class DeleteItem {
    public static void main(String[] args) {
        // AWS credentials
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("fakeaccess", "fakeaccess");

        // DynamoDB client
        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        String tableName = "Employees04";

        // Specify the key of the item to delete
        Map<String, AttributeValue> keyToDelete = new HashMap<>();
        keyToDelete.put("ID", AttributeValue.builder().n("1002").build());

        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(keyToDelete)
                .build();

        client.deleteItem(deleteRequest);
        System.out.println("Deleted item with ID=1002");

        client.close();
    }
}
