package org.example;

import org.example.exception.BankingException;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.repository.InMemoryRepository;
import org.example.service.AccountService;
import org.example.service.AuditService;
import org.example.service.CustomerService;
import org.example.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class BankingSystem {
    private static final Logger logger = LoggerFactory.getLogger(BankingSystem.class);
    private final Scanner scanner;
    private final InMemoryRepository repository;
    private final AuditService auditService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private Customer loggedInCustomer = null;

    public BankingSystem() {
        this.scanner = new Scanner(System.in);
        this.repository = new InMemoryRepository();
        this.auditService = new AuditService(repository);
        this.customerService = new CustomerService(repository, auditService);
        this.accountService = new AccountService(repository, auditService);
        this.transactionService = new TransactionService(repository, accountService, auditService);
    }

    public void start() {
        System.out.println("=== Welcome to Banking Transaction System ===");
        
        while (true) {
            if (loggedInCustomer == null) {
                showLoginMenu();
            } else {
                showBankingMenu();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n=== Login / Register ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        
        int choice = getIntInput("Enter your choice: ");
        
        try {
            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 0 -> {
                    System.out.println("Thank you for using Banking Transaction System!");
                    auditService.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            logger.error("Operation failed", e);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void showBankingMenu() {
        System.out.println("\n=== Welcome " + loggedInCustomer.getFirstName() + " ===");
        displayMenu();
        int choice = getIntInput("Enter your choice: ");
        
        try {
            switch (choice) {
                case 1 -> viewAccounts();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> checkBalance();
                case 6 -> openAccount();
                case 7 -> transactionHistory();
                case 8 -> logout();
                case 0 -> {
                    System.out.println("Thank you for using Banking Transaction System!");
                    auditService.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            logger.error("Operation failed", e);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayMenu() {
        System.out.println("\n=== Banking Menu ===");
        System.out.println("1. View My Accounts");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Check Balance");
        System.out.println("6. Open New Account");
        System.out.println("7. Transaction History");
        System.out.println("8. Logout");
        System.out.println("0. Exit");
    }
    
    private void login() {
        System.out.println("\n=== Login ===");
        String email = getStringInput("Enter your email: ");
        String password = getStringInput("Enter your password: ");
        
        Customer customer = findCustomerByEmail(email);
        if (customer == null) {
            System.out.println("Email not found. Please register first.");
            return;
        }
        
        if (!password.equals(customer.getPassword())) {
            System.out.println("Incorrect password. Please try again.");
            return;
        }
        
        loggedInCustomer = customer;
        System.out.println("\nLogin successful! Welcome " + customer.getFirstName() + "!");
        
        // Show accounts immediately after login
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (!accounts.isEmpty()) {
            System.out.println("\nYour accounts:");
            for (Account account : accounts) {
                System.out.println("• " + account.getAccountNumber() + " (" + account.getAccountType() + ") - $" + account.getBalance());
            }
        } else {
            System.out.println("No accounts found. You can open one from the menu.");
        }
    }
    
    private void register() throws BankingException {
        System.out.println("\n=== Register New Customer ===");
        String firstName = getStringInput("Enter first name: ");
        String lastName = getStringInput("Enter last name: ");
        String email = getStringInput("Enter email: ");
        String phoneNumber = getStringInput("Enter phone number: ");
        String password = getStringInput("Create a password: ");
        String confirmPassword = getStringInput("Confirm password: ");
        
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters long.");
            return;
        }
        
        Customer customer = customerService.createCustomer(firstName, lastName, email, phoneNumber, password);
        System.out.println("\nRegistration successful!");
        System.out.println("Welcome " + firstName + " " + lastName + "!");
        System.out.println("\nYour login details:");
        System.out.println("Email: " + email);
        System.out.println("Password: [Hidden for security]");
    }

    private void viewAccounts() {
        System.out.println("\n=== My Accounts ===");
        
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            System.out.println("Please open an account first (Option 6).");
        } else {
            System.out.println("Your accounts:");
            for (Account account : accounts) {
                System.out.println("Account Number: " + account.getAccountNumber());
                System.out.println("Type: " + account.getAccountType());
                System.out.println("Balance: $" + account.getBalance());
                System.out.println("Status: " + account.getStatus());
                System.out.println("---");
            }
        }
    }

    private void openAccount() throws BankingException {
        System.out.println("\n=== Open New Account ===");
        System.out.println("Opening account for: " + loggedInCustomer.getFirstName() + " " + loggedInCustomer.getLastName());
        
        System.out.println("\nAccount Types:");
        System.out.println("1. SAVINGS");
        System.out.println("2. CHECKING");
        System.out.println("3. BUSINESS");
        
        int typeChoice = getIntInput("Select account type (1-3): ");
        Account.AccountType accountType = switch (typeChoice) {
            case 1 -> Account.AccountType.SAVINGS;
            case 2 -> Account.AccountType.CHECKING;
            case 3 -> Account.AccountType.BUSINESS;
            default -> throw new BankingException("Invalid account type", "INVALID_INPUT");
        };
        
        Account account = accountService.createAccount(loggedInCustomer.getCustomerId(), accountType);
        System.out.println("\nAccount opened successfully!");
        System.out.println("Your Account Number: " + account.getAccountNumber());
        System.out.println("Account Type: " + accountType);
        System.out.println("Initial Balance: $0.00");
    }

    private void deposit() throws BankingException {
        System.out.println("\n=== Deposit Money ===");
        
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Please open an account first.");
            return;
        }
        
        Account account = selectAccount(accounts);
        if (account == null) return;
        
        BigDecimal amount = getBigDecimalInput("Enter deposit amount: $");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        
        Transaction transaction = transactionService.deposit(account.getAccountId(), amount, "Deposit");
        System.out.println("\nDeposit successful!");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("New Balance: $" + accountService.getBalance(account.getAccountId()));
    }

    private void withdraw() throws BankingException {
        System.out.println("\n=== Withdraw Money ===");
        
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Please open an account first.");
            return;
        }
        
        Account account = selectAccount(accounts);
        if (account == null) return;
        
        BigDecimal amount = getBigDecimalInput("Enter withdrawal amount: $");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        
        Transaction transaction = transactionService.withdraw(account.getAccountId(), amount, "Withdrawal");
        System.out.println("\nWithdrawal successful!");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("New Balance: $" + accountService.getBalance(account.getAccountId()));
    }

    private void transfer() throws BankingException {
        System.out.println("\n=== Transfer Money ===");
        
        List<Account> myAccounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (myAccounts.isEmpty()) {
            System.out.println("No accounts found. Please open an account first.");
            return;
        }
        
        System.out.println("Select source account:");
        Account fromAccount = selectAccount(myAccounts);
        if (fromAccount == null) return;
        
        String toAccountNumber = getStringInput("Enter destination account number: ");
        Account toAccount = accountService.findAccountByNumber(toAccountNumber);
        if (toAccount == null) {
            System.out.println("Destination account not found.");
            return;
        }
        
        BigDecimal amount = getBigDecimalInput("Enter transfer amount: $");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        
        Transaction transaction = transactionService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), amount, "Transfer");
        System.out.println("\nTransfer successful!");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Your Account Balance: $" + accountService.getBalance(fromAccount.getAccountId()));
    }

    private void checkBalance() throws BankingException {
        System.out.println("\n=== Account Balances ===");
        
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Please open an account first.");
            return;
        }
        
        for (Account account : accounts) {
            System.out.println(account.getAccountNumber() + " (" + account.getAccountType() + "): $" + account.getBalance());
        }
    }

    private void transactionHistory() {
        System.out.println("\n=== Transaction History ===");
        
        List<Account> accounts = accountService.findAccountsByCustomerId(loggedInCustomer.getCustomerId());
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Please open an account first.");
            return;
        }
        
        Account account = selectAccount(accounts);
        if (account == null) return;
        
        List<Transaction> transactions = transactionService.getTransactionHistory(account.getAccountId());
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this account.");
        } else {
            System.out.println("Transaction History for " + account.getAccountNumber() + ":");
            for (Transaction transaction : transactions) {
                System.out.println("Date: " + transaction.getCreatedAt());
                System.out.println("Type: " + transaction.getType());
                System.out.println("Amount: $" + transaction.getAmount());
                System.out.println("Status: " + transaction.getStatus());
                System.out.println("Description: " + transaction.getDescription());
                System.out.println("---");
            }
        }
    }

    private Account selectAccount(List<Account> accounts) {
        if (accounts.size() == 1) {
            return accounts.get(0);
        }
        
        System.out.println("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i + 1) + ". " + acc.getAccountNumber() + " (" + acc.getAccountType() + ") - $" + acc.getBalance());
        }
        
        int choice = getIntInput("Choose account (1-" + accounts.size() + "): ");
        if (choice >= 1 && choice <= accounts.size()) {
            return accounts.get(choice - 1);
        }
        
        System.out.println("Invalid selection.");
        return null;
    }
    
    private Customer findCustomerByEmail(String email) {
        List<Customer> customers = customerService.getAllCustomers();
        return customers.stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    private void logout() {
        System.out.println("Logged out successfully. Goodbye " + loggedInCustomer.getFirstName() + "!");
        loggedInCustomer = null;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}