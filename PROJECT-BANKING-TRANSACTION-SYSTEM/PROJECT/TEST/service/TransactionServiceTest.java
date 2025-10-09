package org.example.service;

import org.example.database.DynamoDBConfig;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.util.SecurityUtil;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransactionService using DynamoDB Local.
 * Requires DynamoDB Local running on localhost:8000 (sharedDb mode).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceTest {

    private static CustomerService customerService;
    private static AccountService accountService;
    private static TransactionService transactionService;

    private static String customerId;
    private static String fromAccountId;
    private static String toAccountId;

    @BeforeAll
    static void setupAll() {
        DynamoDBConfig.getDynamoDbClient(); // ensure tables exist
        customerService = new CustomerService();
        accountService = new AccountService();
        transactionService = new TransactionService();

        // Create test customer
        customerId = "CUST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Customer c = new Customer(customerId, "Test", "User",
                "test" + UUID.randomUUID().toString().substring(0,4) + "@example.com",
                "9999999999",
                SecurityUtil.hashPin("1234"));
        assertTrue(customerService.createCustomer(c));

        // Create two accounts
        fromAccountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        toAccountId   = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Account from = new Account(fromAccountId, customerId, "CHECKING", new BigDecimal("1000.00"));
        Account to   = new Account(toAccountId,   customerId, "SAVINGS",  new BigDecimal("500.00"));

        assertTrue(accountService.createAccount(from, "2222"));
        assertTrue(accountService.createAccount(to,   "3333"));
    }

    @Test
    @Order(1)
    @DisplayName("Deposit should increase balance")
    void testDeposit() {
        BigDecimal depositAmount = new BigDecimal("200.00");
        boolean result = transactionService.deposit(fromAccountId, depositAmount, "JUnit deposit");
        assertTrue(result, "Deposit should succeed");

        Account updated = accountService.getAccountById(fromAccountId);
        assertEquals(new BigDecimal("1200.00"), updated.getBalance());
    }

    @Test
    @Order(2)
    @DisplayName("Withdraw should decrease balance")
    void testWithdraw() {
        BigDecimal withdrawAmount = new BigDecimal("300.00");
        boolean result = transactionService.withdraw(fromAccountId, withdrawAmount, "JUnit withdraw");
        assertTrue(result, "Withdraw should succeed");

        Account updated = accountService.getAccountById(fromAccountId);
        assertEquals(new BigDecimal("900.00"), updated.getBalance());
    }

    @Test
    @Order(3)
    @DisplayName("Withdrawal should fail with insufficient funds")
    void testWithdrawInsufficientFunds() {
        BigDecimal large = new BigDecimal("99999.00");
        boolean result = transactionService.withdraw(fromAccountId, large, "Too large");
        assertFalse(result, "Withdrawal should fail");

        Account updated = accountService.getAccountById(fromAccountId);
        assertEquals(new BigDecimal("900.00"), updated.getBalance(),
                "Balance must remain unchanged after failed transaction");
    }

    @Test
    @Order(4)
    @DisplayName("Transfer between two valid accounts should be atomic (ACID)")
    void testTransferACIDSuccess() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal expectedFrom = new BigDecimal("800.00");
        BigDecimal expectedTo   = new BigDecimal("600.00");

        boolean result = transactionService.transfer(fromAccountId, toAccountId, amount, "JUnit transfer");
        assertTrue(result, "Transfer should succeed");

        Account from = accountService.getAccountById(fromAccountId);
        Account to   = accountService.getAccountById(toAccountId);

        assertEquals(expectedFrom, from.getBalance(), "From account must decrease");
        assertEquals(expectedTo,   to.getBalance(),   "To account must increase");
    }

    @Test
    @Order(5)
    @DisplayName("Transfer should fail atomically with insufficient funds (no partial changes)")
    void testTransferACIDFailure() {
        BigDecimal large = new BigDecimal("99999.00");

        Account beforeFrom = accountService.getAccountById(fromAccountId);
        Account beforeTo   = accountService.getAccountById(toAccountId);

        boolean result = transactionService.transfer(fromAccountId, toAccountId, large, "JUnit fail");
        assertFalse(result, "Transfer should fail");

        Account afterFrom = accountService.getAccountById(fromAccountId);
        Account afterTo   = accountService.getAccountById(toAccountId);

        // verify balances did NOT change
        assertEquals(beforeFrom.getBalance(), afterFrom.getBalance(),
                "From account balance must remain unchanged");
        assertEquals(beforeTo.getBalance(), afterTo.getBalance(),
                "To account balance must remain unchanged");
    }

    @Test
    @Order(6)
    @DisplayName("Transactions list should return all records for account")
    void testTransactionHistory() {
        List<Transaction> txns = transactionService.getTransactionsByAccountId(fromAccountId);
        assertFalse(txns.isEmpty(), "There should be at least one transaction recorded");
        txns.forEach(System.out::println);
    }

    @Test
    @Order(7)
    @DisplayName("Invalid account ID should fail for all operations")
    void testInvalidAccount() {
        String invalid = "ACCINVALID";
        BigDecimal amt = new BigDecimal("50.00");

        assertFalse(transactionService.deposit(invalid, amt, "invalid deposit"));
        assertFalse(transactionService.withdraw(invalid, amt, "invalid withdraw"));
        assertFalse(transactionService.transfer(invalid, toAccountId, amt, "invalid from"));
        assertFalse(transactionService.transfer(fromAccountId, invalid, amt, "invalid to"));
    }
}
