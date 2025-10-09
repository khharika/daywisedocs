package org.example;

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.service.*;
import org.example.util.SecurityUtil;

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
        DynamoDBConfig.getDynamoDbClient();
        System.out.println("✅ DynamoDB connected and ready!");

        while (true) {
            if (currentCustomerId == null) {
                showWelcomeMenu();
                int ch = getInt("Enter choice: ");
                switch (ch) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> forgetCustomerPin();
                    case 4 -> { System.out.println("Bye"); System.exit(0); }
                    default -> System.out.println("Invalid.");
                }
            } else {
                showBankMenu();
                int ch = getInt("Enter choice: ");
                switch (ch) {
                    case 1 -> createAccount();
                    case 2 -> viewAccounts();
                    case 3 -> checkBalance();
                    case 4 -> viewTransactions();
                    case 5 -> transfer();
                    case 6 -> deposit();
                    case 7 -> withdraw();
                    case 8 -> forgetAccountPin();
                    case 9 -> logout();
                    default -> System.out.println("Invalid.");
                }
            }
        }
    }

    private static void showWelcomeMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Forget PIN (Customer)");
        System.out.println("4. Exit");
    }

    private static void showBankMenu() {
        System.out.println("\n1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Check Balance");
        System.out.println("4. View Transactions");
        System.out.println("5. Transfer");
        System.out.println("6. Deposit");
        System.out.println("7. Withdraw");
        System.out.println("8. Forget Account PIN");
        System.out.println("9. Logout");
    }

    private static void register() {
        System.out.println("--- Register ---");
        String id = "CUST" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String first = getString("First name: ");
        String last = getString("Last name: ");
        String email = getString("Email: ");
        if (!SecurityUtil.isValidEmail(email)) { System.out.println("Invalid email"); return; }
        String phone = getString("Phone: ");
        if (!SecurityUtil.isValidPhone(phone)) { System.out.println("Invalid phone"); return; }
        String pin = getString("Create 4-digit PIN: ");
        if (!SecurityUtil.isValidPinFormat(pin)) { System.out.println("Invalid PIN format (4 digits)"); return; }

        Customer c = new Customer(id, first, last, email, phone, SecurityUtil.hashPin(pin));
        if (customerService.createCustomer(c)) {
            System.out.println("✅ Registered: " + id);
        } else {
            System.out.println("❌ Registration failed.");
        }
    }

    private static void login() {
        System.out.println("--- Login ---");
        String email = getString("Email: ");
        String pin = getString("PIN: ");
        var c = customerService.getCustomerByEmail(email);
        if (c != null && SecurityUtil.verifyPin(pin, c.getPinHash())) {
            currentCustomerId = c.getCustomerId();
            System.out.println("✅ Welcome " + c.getFirstName());
        } else System.out.println("❌ Invalid credentials");
    }

    private static void logout() {
        currentCustomerId = null;
        System.out.println("✅ Logged out");
    }

    private static void createAccount() {
        if (currentCustomerId == null) { System.out.println("Login first"); return; }
        String accId = "ACC" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String type = getString("Account type (SAVINGS/CHECKING): ");
        BigDecimal bal = getBigDecimal("Initial balance: ");
        String pin = getString("Set 4-digit transaction PIN: ");
        if (!SecurityUtil.isValidPinFormat(pin)) { System.out.println("Invalid PIN format"); return; }

        Account a = new Account(accId, currentCustomerId, type, bal);
        boolean ok = accountService.createAccount(a, pin);
        if (ok) System.out.println("✅ Account created: " + accId);
        else System.out.println("❌ Failed to create account");
    }

    private static void viewAccounts() {
        if (currentCustomerId == null) { System.out.println("Login first"); return; }
        var list = accountService.getAccountsByCustomerId(currentCustomerId);
        if (list.isEmpty()) System.out.println("No accounts.");
        else list.forEach(System.out::println);
    }

    private static void checkBalance() {
        String acc = getString("Account ID: ");
        var a = accountService.getAccountById(acc);
        if (a == null) System.out.println("Not found");
        else System.out.println("Balance: " + a.getBalance());
    }

    private static void viewTransactions() {
        String acc = getString("Account ID: ");
        List<Transaction> txns = transactionService.getTransactionsByAccountId(acc);
        if (txns.isEmpty()) System.out.println("No transactions.");
        else txns.forEach(System.out::println);
    }

    private static void transfer() {
        String from = getString("From account ID: ");
        String to = getString("To account ID: ");
        BigDecimal amount = getBigDecimal("Amount: ");
        String desc = getString("Description: ");
        transactionService.transfer(from, to, amount, desc);
    }

    private static void deposit() {
        String acc = getString("Account ID: ");
        BigDecimal amt = getBigDecimal("Amount: ");
        transactionService.deposit(acc, amt, "Deposit");
    }

    private static void withdraw() {
        String acc = getString("Account ID: ");
        BigDecimal amt = getBigDecimal("Amount: ");
        transactionService.withdraw(acc, amt, "Withdraw");
    }

    private static void forgetCustomerPin() {
        System.out.println("--- Forget customer PIN ---");
        String email = getString("Email: ");
        String phone = getString("Phone: ");
        String newPin = getString("New 4-digit PIN: ");
        if (!SecurityUtil.isValidPinFormat(newPin)) { System.out.println("Invalid PIN"); return; }
        customerService.updateCustomerPin(email, phone, SecurityUtil.hashPin(newPin));
    }

    private static void forgetAccountPin() {
        System.out.println("--- Forget account PIN ---");
        String accId = getString("Account ID: ");
        String email = getString("Customer Email: ");
        // minimal verification: ensure account belongs to logged-in user (or check email/owner)
        // For ease: we require customer to be logged in and match account owner
        var acc = accountService.getAccountById(accId);
        if (acc == null) { System.out.println("Account not found"); return; }
        var customer = customerService.getCustomerById(acc.getCustomerId());
        if (customer == null || !customer.getEmail().equals(email)) {
            System.out.println("Verification failed.");
            return;
        }
        String newPin = getString("New 4-digit account PIN: ");
        if (!SecurityUtil.isValidPinFormat(newPin)) { System.out.println("Invalid PIN"); return; }
        accountService.updateAccountPin(accId, email, newPin);
    }

    // helpers
    private static String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getInt(String prompt) {
        try {
            System.out.print(prompt);
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) { return -1; }
    }

    private static BigDecimal getBigDecimal(String prompt) {
        try {
            System.out.print(prompt);
            return new BigDecimal(scanner.nextLine().trim());
        } catch (Exception e) { return BigDecimal.ZERO; }
    }
}
