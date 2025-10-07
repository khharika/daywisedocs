package org.example.service;

import org.example.exception.BankingException;
import org.example.model.Customer;
import org.example.repository.InMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final InMemoryRepository repository;
    private final AuditService auditService;

    public CustomerService(InMemoryRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public Customer createCustomer(String firstName, String lastName, String email, String phoneNumber, String password) throws BankingException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new BankingException("First name is required", "INVALID_INPUT");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new BankingException("Last name is required", "INVALID_INPUT");
        }
        if (email == null || !email.contains("@")) {
            throw new BankingException("Valid email is required", "INVALID_INPUT");
        }

        String customerId = UUID.randomUUID().toString();
        Customer customer = new Customer(customerId, firstName, lastName, email, phoneNumber, password);
        
        repository.saveCustomer(customer);
        auditService.logCustomerAction(customerId, "CREATE", null, customer.toString(), "SYSTEM");
        
        logger.info("Customer created: {} {}", firstName, lastName);
        return customer;
    }

    public Customer findCustomerById(String customerId) {
        return repository.findCustomerById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return repository.findAllCustomers();
    }

    public void updateCustomer(String customerId, String firstName, String lastName, 
                              String email, String phoneNumber, String password, String userId) throws BankingException {
        Customer customer = repository.findCustomerById(customerId);
        if (customer == null) {
            throw new BankingException("Customer not found: " + customerId, "CUSTOMER_NOT_FOUND");
        }

        String oldValue = customer.toString();
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            customer.setFirstName(firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            customer.setLastName(lastName);
        }
        if (email != null && email.contains("@")) {
            customer.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            customer.setPhoneNumber(phoneNumber);
        }
        if (password != null && !password.trim().isEmpty()) {
            customer.setPassword(password);
        }

        repository.saveCustomer(customer);
        auditService.logCustomerAction(customerId, "UPDATE", oldValue, customer.toString(), userId);
        
        logger.info("Customer updated: {}", customerId);
    }
}