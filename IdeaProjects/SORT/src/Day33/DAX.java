package Day33;

package org.example.Accelerator_DAX;


import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dax.DaxClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;




public class AcceeleratorDAXDemo {
    public static void main(String[] args) {
        //get  this end point created using AWS cloud
        // create a  acluster..
        String daxEndpoint = "daxs://daxcluster01.ee3lf0.dax-clusters.ap-south-1.amazonaws.com";


        // we are creating DAx client
        DaxClient daxClient = DaxClient.builder()
                .endpointOverride(URI.create(daxEndpoint))
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();


        String tableName = "Employees01";
        String KeyName = "ID";
        String KeyValue = "10001";




        Map<String, AttributeValue> item = new HashMap<>();
        item.put(KeyName, AttributeValue.builder().s(KeyValue).build());
        item.put("msg", AttributeValue.builder().s("we are creating DAX ").build());


        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();


        daxClient.putItem(request);


        Map<String, AttributeValue> getItem = new HashMap<>();
        getItem.put(KeyName, AttributeValue.builder().build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(getItem)
                .build();


        GetItemResponse response = daxClient.getItem(request);
        System.out.println("Dax working ..");
        daxClient.close();
    }
}