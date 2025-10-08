/*
package org.example.service;

import org.example.database.DatabaseConfig;
import org.example.database.DynamoDBConfig;
import org.example.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    
    public boolean createCustomer(Customer customer) {
        // Save to H2 database
        String sql = "INSERT INTO customers (customer_id, name, email, phone, password) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getPassword());
            
            int result = pstmt.executeUpdate();
            System.out.println("✓ Customer saved to H2 database: " + (result > 0 ? "SUCCESS" : "FAILED"));
            
            // ALSO save to DynamoDB
            saveToDynamoDB(customer);
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void saveToDynamoDB(Customer customer) {
        try {
            DynamoDBConfig.putCustomer(
                customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getPassword()
            );
            System.out.println("✓ Customer saved to DynamoDB: " + customer.getCustomerId());
        } catch (Exception e) {
            System.err.println("Failed to save customer to DynamoDB: " + e.getMessage());
        }
    }
    
    public Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setPassword(rs.getString("password"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer: " + e.getMessage());
        }
        return null;
    }
    
    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setPassword(rs.getString("password"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customer by email: " + e.getMessage());
        }
        return null;
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setPassword(rs.getString("password"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
        }
        return customers;
    }
}*/
package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Customer;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

public class CustomerService {

    private final DynamoDbClient dynamoDbClient = DynamoDBConfig.getDynamoDbClient();

    public boolean createCustomer(Customer customer) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("customer_id", AttributeValue.builder().s(customer.getCustomerId()).build());
            item.put("name", AttributeValue.builder().s(customer.getName()).build());
            item.put("email", AttributeValue.builder().s(customer.getEmail()).build());
            item.put("phone", AttributeValue.builder().s(customer.getPhone()).build());
            item.put("password", AttributeValue.builder().s(customer.getPassword()).build());
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
                    .filterExpression("email = :email")
                    .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email).build()))
                    .build();

            List<Map<String, AttributeValue>> items = dynamoDbClient.scan(scan).items();
            if (items.isEmpty()) return null;

            Map<String, AttributeValue> data = items.get(0);
            return mapToCustomer(data);
        } catch (Exception e) {
            System.err.println("❌ Error fetching customer by email: " + e.getMessage());
            return null;
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try {
            ScanResponse response = dynamoDbClient.scan(ScanRequest.builder().tableName("customers").build());
            for (Map<String, AttributeValue> item : response.items()) {
                customers.add(mapToCustomer(item));
            }
        } catch (Exception e) {
            System.err.println("❌ Error retrieving customers: " + e.getMessage());
        }
        return customers;
    }

    private Customer mapToCustomer(Map<String, AttributeValue> item) {
        Customer c = new Customer();
        c.setCustomerId(item.get("customer_id").s());
        c.setName(item.get("name").s());
        c.setEmail(item.get("email").s());
        c.setPhone(item.get("phone").s());
        c.setPassword(item.get("password").s());
        return c;
    }
}
