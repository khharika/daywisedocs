package com.banking.app;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.service.BankingService;
import com.banking.service.AuthService;


import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class BankingApp {
    private final BankingService bankingService;
    private final AuthService authService;
    private final Scanner scanner;
    private Customer loggedInCustomer;
    
    public BankingApp() {
        this.bankingService = new BankingService();
        this.authService = new AuthService();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        BankingApp app = new BankingApp();
        app.run();
    }
    
    public void run() {
        System.out.println("Welcome to Banking System!");
        
        while (true) {
            if (loggedInCustomer == null) {
                showLoginMenu();
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> forgotPassword();
                    case 4 -> {
                        System.out.println("Thank you for using Banking System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } else {
                showMainMenu();
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> deposit();
                    case 3 -> withdraw();
                    case 4 -> checkBalance();
                    case 5 -> viewAccounts();
                    case 6 -> {
                        loggedInCustomer = null;
                        System.out.println("Logged out successfully!");
                    }
                    case 7 -> {
                        System.out.println("Thank you for using Banking System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n=== Banking System Login ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Forgot Password");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void showMainMenu() {
        System.out.println("\n=== Welcome " + loggedInCustomer.getName() + " ===");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. View Accounts");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void login() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        Customer customer = authService.login(email, password);
        if (customer != null) {
            loggedInCustomer = customer;
            System.out.println("Login successful! Welcome " + customer.getName());
        } else {
            System.out.println("Invalid email or password!");
        }
    }
    
    private void register() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Create password: ");
        String password = scanner.nextLine();
        System.out.print("Security question (What is your mother's maiden name?): ");
        String securityQuestion = scanner.nextLine();
        System.out.print("Security answer: ");
        String securityAnswer = scanner.nextLine();
        
        String hashedPassword = authService.hashPassword(password);
        Customer customer = bankingService.registerCustomer(name, email, phone, hashedPassword, securityQuestion, securityAnswer);
        System.out.println("Registration successful! Customer ID: " + customer.getCustomerId());
    }
    
    private void forgotPassword() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Answer security question - What is your mother's maiden name?: ");
        String securityAnswer = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        if (authService.resetPassword(email, securityAnswer, newPassword)) {
            System.out.println("Password reset successful!");
        } else {
            System.out.println("Invalid email or security answer!");
        }
    }
    

    
    private void createAccount() {
        System.out.print("Enter account type (SAVINGS/CHECKING): ");
        String accountType = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        BigDecimal initialBalance = scanner.nextBigDecimal();
        scanner.nextLine(); // consume newline
        
        Account account = bankingService.createAccount(loggedInCustomer.getCustomerId(), accountType, initialBalance);
        System.out.println("Account created successfully: " + account);
    }
    
    private void deposit() {
        try {
            System.out.print("Enter account ID: ");
            String accountId = scanner.nextLine();
            System.out.print("Enter deposit amount: ");
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine(); // consume newline
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            
            bankingService.deposit(accountId, amount, description);
            Account updatedAccount = bankingService.getAccount(accountId);
            System.out.println("Deposit successful! New balance: " + updatedAccount.getBalance());
        } catch (Exception e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }
    
    private void withdraw() {
        try {
            System.out.print("Enter account ID: ");
            String accountId = scanner.nextLine();
            System.out.print("Enter withdrawal amount: ");
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine(); // consume newline
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            
            bankingService.withdraw(accountId, amount, description);
            Account updatedAccount = bankingService.getAccount(accountId);
            System.out.println("Withdrawal successful! New balance: " + updatedAccount.getBalance());
        } catch (Exception e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }
    
    private void checkBalance() {
        System.out.print("Enter account ID: ");
        String accountId = scanner.nextLine();
        
        try {
            Account account = bankingService.getAccount(accountId);
            if (account != null && account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("Account ID: " + account.getAccountId());
                System.out.println("Account Type: " + account.getAccountType());
                System.out.println("Current Balance: $" + account.getBalance());
            } else {
                System.out.println("Account not found or you don't have access to this account!");
            }
        } catch (Exception e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }
    
    private void viewAccounts() {
        List<Account> accounts = bankingService.getCustomerAccounts(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Create an account first!");
        } else {
            System.out.println("Your accounts:");
            accounts.forEach(System.out::println);
        }
    }
}