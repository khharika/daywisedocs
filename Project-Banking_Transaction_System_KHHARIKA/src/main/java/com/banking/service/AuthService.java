package com.banking.service;

import com.banking.model.Customer;
import com.banking.repository.DatabaseService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthService {
    private final DatabaseService databaseService;
    
    public AuthService() {
        this.databaseService = new DatabaseService();
    }
    
    public Customer login(String email, String password) {
        Customer customer = databaseService.getCustomerByEmail(email);
        if (customer != null && verifyPassword(password, customer.getPassword())) {
            return customer;
        }
        return null;
    }
    
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        Customer customer = databaseService.getCustomerByEmail(email);
        if (customer != null && customer.getSecurityAnswer().equals(securityAnswer)) {
            customer.setPassword(hashPassword(newPassword));
            databaseService.updateCustomer(customer);
            return true;
        }
        return false;
    }
    
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}