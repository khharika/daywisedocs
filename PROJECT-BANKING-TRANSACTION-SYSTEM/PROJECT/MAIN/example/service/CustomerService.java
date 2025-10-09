package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Customer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean createCustomer(Customer customer) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("customer_id", AttributeValue.builder().s(customer.getCustomerId()).build());
            item.put("first_name", AttributeValue.builder().s(customer.getFirstName()).build());
            item.put("last_name", AttributeValue.builder().s(customer.getLastName()).build());
            item.put("email", AttributeValue.builder().s(customer.getEmail()).build());
            item.put("phone", AttributeValue.builder().s(customer.getPhone()).build());
            item.put("pin", AttributeValue.builder().s(customer.getPinHash()).build());
            item.put("created_at", AttributeValue.builder().s(customer.getCreatedAt().toString()).build());

            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("customers")
                    .item(item)
                    .build());

            System.out.println("✅ Customer saved to DynamoDB: " + customer.getCustomerId());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
            return false;
        }
    }

    public Customer getCustomerByEmail(String email) {
        try {
            ScanRequest scan = ScanRequest.builder()
                    .tableName("customers")
                    .filterExpression("email = :e")
                    .expressionAttributeValues(Map.of(":e", AttributeValue.builder().s(email).build()))
                    .build();

            List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scan).items();
            if (items.isEmpty()) return null;
            Map<String, AttributeValue> it = items.get(0);
            Customer c = new Customer();
            c.setCustomerId(it.get("customer_id").s());
            c.setFirstName(it.get("first_name").s());
            c.setLastName(it.get("last_name").s());
            c.setEmail(it.get("email").s());
            c.setPhone(it.get("phone").s());
            c.setPinHash(it.get("pin").s());
            return c;
        } catch (Exception e) {
            System.err.println("❌ Error fetching customer: " + e.getMessage());
            return null;
        }
    }

    public Customer getCustomerById(String customerId) {
        try {
            GetItemResponse resp = dynamoDbClient.getItem(GetItemRequest.builder()
                    .tableName("customers")
                    .key(Map.of("customer_id", AttributeValue.builder().s(customerId).build()))
                    .build());
            if (!resp.hasItem()) return null;
            Map<String, AttributeValue> it = resp.item();
            Customer c = new Customer();
            c.setCustomerId(it.get("customer_id").s());
            c.setFirstName(it.get("first_name").s());
            c.setLastName(it.get("last_name").s());
            c.setEmail(it.get("email").s());
            c.setPhone(it.get("phone").s());
            c.setPinHash(it.get("pin").s());
            return c;
        } catch (Exception e) {
            System.err.println("❌ Error fetching customer by id: " + e.getMessage());
            return null;
        }
    }

    public boolean updateCustomerPin(String email, String phone, String newPinHash) {
        try {
            Customer c = getCustomerByEmail(email);
            if (c == null || !c.getPhone().equals(phone)) {
                System.err.println("❌ No matching customer for given email/phone.");
                return false;
            }
            Map<String, AttributeValue> key = Map.of("customer_id", AttributeValue.builder().s(c.getCustomerId()).build());
            Map<String, AttributeValueUpdate> updates = Map.of("pin", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(newPinHash).build())
                    .action(AttributeAction.PUT).build());
            dynamoDbClient.updateItem(UpdateItemRequest.builder().tableName("customers").key(key).attributeUpdates(updates).build());
            System.out.println("✅ Customer PIN updated for " + c.getCustomerId());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error updating customer PIN: " + e.getMessage());
            return false;
        }
    }
}
