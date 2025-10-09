package org.example.model;

import java.time.LocalDateTime;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String pinHash; // stored hashed
    private LocalDateTime createdAt;
    private boolean active;

    public Customer() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public Customer(String customerId, String firstName, String lastName, String email, String phone, String pinHash) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.pinHash = pinHash;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // getters / setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("Customer{id=%s, name=%s %s, email=%s}", customerId, firstName, lastName, email);
    }
}
