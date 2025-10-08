/*
package org.example;

import org.example.model.Account;
import org.example.model.AuditLog;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.service.AccountService;
import org.example.service.AuditService;
import org.example.service.CustomerService;
import org.example.service.TransactionService;
import org.example.database.DatabaseConfig;
import org.example.database.DynamoDBConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerService customerService = new CustomerService();
    private static final AccountService accountService = new AccountService();
    private static final TransactionService transactionService = new TransactionService();
    private static final AuditService auditService = new AuditService();
    private static String currentCustomerId = null;

    public static void main(String[] args) {
        System.out.println("=== Banking Transaction System ===");
        System.out.println("Initializing system...");
        
        // Test database immediately
        testDatabase();
        
        // Initialize DynamoDB connection (MANDATORY)
        try {
            DynamoDBConfig.getDynamoDbClient();
            System.out.println("✓ Connected to DynamoDB on localhost:8000");
        } catch (Exception e) {
            System.err.println("✗ CRITICAL ERROR: DynamoDB Local is not running!");
            System.err.println("DynamoDB is MANDATORY for this banking system.");
            System.err.println("");
            System.err.println("SOLUTION:");
            System.err.println("1. Run 'start-dynamodb-local.bat' in another terminal");
            System.err.println("2. Wait for DynamoDB to start on port 8000");
            System.err.println("3. Then restart this banking application");
            System.err.println("");
            System.err.println("Application will now exit.");
            System.exit(1);
        }
        
        while (true) {
            if (currentCustomerId == null) {
                displayWelcomeMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> {
                        System.out.println("Thank you for using Banking Transaction System!");
                        DatabaseConfig.closeConnection();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } else {
                displayBankingMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> viewAccount();
                    case 3 -> checkBalance();
                    case 4 -> viewTransactionHistory();
                    case 5 -> transferMoney();
                    case 6 -> depositMoney();
                    case 7 -> withdrawMoney();
                    case 8 -> logout();
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void displayWelcomeMenu() {
        System.out.println("\n=== WELCOME ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
    }

    private static void displayBankingMenu() {
        System.out.println("\n=== BANKING MENU ===");
        System.out.println("1. Create Account");
        System.out.println("2. View Account");
        System.out.println("3. Check Balance");
        System.out.println("4. Transaction History");
        System.out.println("5. Transfer Money");
        System.out.println("6. Deposit Money");
        System.out.println("7. Withdraw Money");
        System.out.println("8. Exit");
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        String email = getStringInput("Enter email: ");
        String password = getStringInput("Enter password: ");
        Customer customer = customerService.getCustomerByEmail(email);
        
        if (customer != null && customer.getPassword().equals(password)) {
            currentCustomerId = customer.getCustomerId();
            System.out.println("Login successful! Welcome " + customer.getName());
        } else {
            System.out.println("Invalid email or password.");
        }
    }

    private static void register() {
        System.out.println("\n--- Register New Customer ---");
        String customerId = "CUST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        String name = getStringInput("Enter your name: ");
        String email = getStringInput("Enter your email: ");
        String phone = getStringInput("Enter your phone: ");
        String password = getStringInput("Enter password: ");
        
        Customer customer = new Customer(customerId, name, email, phone, password);
        
        if (customerService.createCustomer(customer)) {
            System.out.println("Registration successful!");
            System.out.println("Your Customer ID: " + customerId);
            System.out.println("Use your email (" + email + ") and password to login.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private static void logout() {
        currentCustomerId = null;
        System.out.println("Logged out successfully!");
    }

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        String accountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        System.out.println("Select Account Type:");
        System.out.println("1. SAVINGS");
        System.out.println("2. CHECKING");
        System.out.println("3. BUSINESS");
        
        int typeChoice = getIntInput("Enter choice (1-3): ");
        String accountType;
        
        switch (typeChoice) {
            case 1 -> accountType = "SAVINGS";
            case 2 -> accountType = "CHECKING";
            case 3 -> accountType = "BUSINESS";
            default -> {
                System.out.println("Invalid choice. Defaulting to SAVINGS.");
                accountType = "SAVINGS";
            }
        }
        
        BigDecimal initialBalance = getBigDecimalInput("Enter initial balance: ");
        
        Account account = new Account(accountId, currentCustomerId, accountType, initialBalance);
        
        if (accountService.createAccount(account)) {
            System.out.println("Account created successfully!");
            System.out.println("Account ID: " + accountId);
            System.out.println("Account Type: " + accountType);
        } else {
            System.out.println("Failed to create account.");
        }
    }

    private static void viewAccount() {
        List<Account> accounts = accountService.getAccountsByCustomerId(currentCustomerId);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Please create an account first.");
        } else {
            System.out.println("\n=== Your Accounts ===");
            accounts.forEach(System.out::println);
        }
    }

    private static void checkBalance() {
        String accountId = getStringInput("Enter account ID: ");
        Account account = accountService.getAccountById(accountId);
        
        if (account != null && account.getCustomerId().equals(currentCustomerId)) {
            System.out.println("\nAccount Balance: $" + account.getBalance());
        } else {
            System.out.println("Account not found or access denied.");
        }
    }

    private static void viewTransactionHistory() {
        String accountId = getStringInput("Enter account ID: ");
        Account account = accountService.getAccountById(accountId);
        
        if (account != null && account.getCustomerId().equals(currentCustomerId)) {
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for this account.");
            } else {
                System.out.println("\n=== Transaction History ===");
                transactions.forEach(System.out::println);
            }
        } else {
            System.out.println("Account not found or access denied.");
        }
    }

    private static void depositMoney() {
        System.out.println("\n--- Deposit Money ---");
        String accountId = getStringInput("Enter account ID: ");
        Account account = accountService.getAccountById(accountId);
        
        if (account != null && account.getCustomerId().equals(currentCustomerId)) {
            BigDecimal amount = getBigDecimalInput("Enter deposit amount: ");
            String description = getStringInput("Enter description (optional): ");
            
            if (transactionService.deposit(accountId, amount, description)) {
                System.out.println("Deposit successful!");
            } else {
                System.out.println("Deposit failed.");
            }
        } else {
            System.out.println("Account not found or access denied.");
        }
    }

    private static void withdrawMoney() {
        System.out.println("\n--- Withdraw Money ---");
        String accountId = getStringInput("Enter account ID: ");
        Account account = accountService.getAccountById(accountId);
        
        if (account != null && account.getCustomerId().equals(currentCustomerId)) {
            BigDecimal amount = getBigDecimalInput("Enter withdrawal amount: ");
            String description = getStringInput("Enter description (optional): ");
            
            if (transactionService.withdraw(accountId, amount, description)) {
                System.out.println("Withdrawal successful!");
            } else {
                System.out.println("Withdrawal failed. Check balance.");
            }
        } else {
            System.out.println("Account not found or access denied.");
        }
    }

    private static void transferMoney() {
        System.out.println("\n--- Transfer Money ---");
        String fromAccountId = getStringInput("Enter source account ID: ");
        Account fromAccount = accountService.getAccountById(fromAccountId);
        
        if (fromAccount != null && fromAccount.getCustomerId().equals(currentCustomerId)) {
            String toAccountId = getStringInput("Enter destination account ID: ");
            BigDecimal amount = getBigDecimalInput("Enter transfer amount: ");
            String description = getStringInput("Enter description (optional): ");
            
            if (transactionService.transfer(fromAccountId, toAccountId, amount, description)) {
                System.out.println("Transfer successful!");
            } else {
                System.out.println("Transfer failed. Check details and balance.");
            }
        } else {
            System.out.println("Source account not found or access denied.");
        }
    }

    private static void testDatabase() {
        try {
            // Force database initialization
            java.sql.Connection conn = org.example.database.DatabaseConfig.getConnection();
            System.out.println("✓ Database initialized successfully");
            
            // Test customer creation
            Customer testCustomer = new Customer("TEST001", "Test User", "test@example.com", "1234567890", "password");
            boolean created = customerService.createCustomer(testCustomer);
            System.out.println("✓ Test customer creation: " + (created ? "SUCCESS" : "FAILED"));
            
            // Test customer retrieval
            Customer retrieved = customerService.getCustomerByEmail("test@example.com");
            System.out.println("✓ Test customer retrieval: " + (retrieved != null ? "SUCCESS" : "FAILED"));
            
        } catch (Exception e) {
            System.err.println("✗ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Utility methods for input handling
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}*/
package org.example;

import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.service.TransactionService;
import org.example.database.DynamoDBConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerService customerService = new CustomerService();
    private static final AccountService accountService = new AccountService();
    private static final TransactionService transactionService = new TransactionService();
    private static String currentCustomerId = null;

    public static void main(String[] args) {
        System.out.println("=== Banking Transaction System  ===");

        // Initialize DynamoDB connection
        DynamoDBConfig.getDynamoDbClient();
        System.out.println("✅ DynamoDB connected and ready!\n");

        while (true) {
            if (currentCustomerId == null) {
                displayWelcomeMenu();
                int choice = getIntInput("Enter your choice: ");
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> {
                        System.out.println("Thank you for using the system!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } else {
                displayBankingMenu();
                int choice = getIntInput("Enter your choice: ");
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> viewAccounts();
                    case 3 -> checkBalance();
                    case 4 -> viewTransactions();
                    case 5 -> transfer();
                    case 6 -> deposit();
                    case 7 -> withdraw();
                    case 8 -> logout();
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private static void displayWelcomeMenu() {
        System.out.println("\n=== WELCOME ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
    }

    private static void displayBankingMenu() {
        System.out.println("\n=== BANKING MENU ===");
        System.out.println("1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Check Balance");
        System.out.println("4. View Transactions");
        System.out.println("5. Transfer");
        System.out.println("6. Deposit");
        System.out.println("7. Withdraw");
        System.out.println("8. Logout");
    }

    private static void register() {
        System.out.println("\n--- Register New Customer ---");
        String id = "CUST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String name = getStringInput("Enter name: ");
        String email = getStringInput("Enter email: ");
        String phone = getStringInput("Enter phone: ");
        String password = getStringInput("Enter password: ");

        Customer customer = new Customer(id, name, email, phone, password);
        if (customerService.createCustomer(customer)) {
            System.out.println("✅ Registration successful! Your ID: " + id);
        } else {
            System.out.println("❌ Registration failed.");
        }
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        String email = getStringInput("Enter email: ");
        String password = getStringInput("Enter password: ");

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer != null && customer.getPassword().equals(password)) {
            currentCustomerId = customer.getCustomerId();
            System.out.println("✅ Login successful! Welcome " + customer.getName());
        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }

    private static void logout() {
        currentCustomerId = null;
        System.out.println("Logged out successfully!");
    }

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        String accountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String type = getStringInput("Enter account type (SAVINGS/CHECKING): ");
        BigDecimal balance = getBigDecimalInput("Enter initial balance: ");

        Account account = new Account(accountId, currentCustomerId, type, balance);
        if (accountService.createAccount(account))
            System.out.println("✅ Account created: " + accountId);
        else
            System.out.println("❌ Failed to create account.");
    }

    private static void viewAccounts() {
        List<Account> accounts = accountService.getAccountsByCustomerId(currentCustomerId);
        if (accounts.isEmpty())
            System.out.println("No accounts found.");
        else
            accounts.forEach(System.out::println);
    }

    private static void checkBalance() {
        String id = getStringInput("Enter account ID: ");
        Account account = accountService.getAccountById(id);
        if (account != null)
            System.out.println("💰 Current Balance: " + account.getBalance());
        else
            System.out.println("❌ Account not found.");
    }

    private static void viewTransactions() {
        String id = getStringInput("Enter account ID: ");
        List<Transaction> txns = transactionService.getTransactionsByAccountId(id);
        if (txns.isEmpty())
            System.out.println("No transactions found.");
        else
            txns.forEach(System.out::println);
    }

    private static void transfer() {
        String from = getStringInput("Enter from account ID: ");
        String to = getStringInput("Enter to account ID: ");
        BigDecimal amount = getBigDecimalInput("Enter amount: ");
        String desc = getStringInput("Enter description: ");
        transactionService.transfer(from, to, amount, desc);
    }

    private static void deposit() {
        String id = getStringInput("Enter account ID: ");
        BigDecimal amt = getBigDecimalInput("Enter amount: ");
        transactionService.deposit(id, amt, "Deposit");
    }

    private static void withdraw() {
        String id = getStringInput("Enter account ID: ");
        BigDecimal amt = getBigDecimalInput("Enter amount: ");
        transactionService.withdraw(id, amt, "Withdrawal");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static BigDecimal getBigDecimalInput(String prompt) {
        System.out.print(prompt);
        return new BigDecimal(scanner.nextLine().trim());
    }
}
