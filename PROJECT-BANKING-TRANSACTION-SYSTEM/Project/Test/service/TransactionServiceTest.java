package org.example.service;

import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    
    private TransactionService transactionService;
    private CustomerService customerService;
    private AccountService accountService;
    private String customerId;
    private String fromAccountId;
    private String toAccountId;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        customerService = new CustomerService();
        accountService = new AccountService();
        
        // Create test customer
        customerId = "CUST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Customer customer = new Customer(customerId, "Test Customer", "test@example.com", "1234567890");
        customerService.createCustomer(customer);
        
        // Create test accounts
        fromAccountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        toAccountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Account fromAccount = new Account(fromAccountId, customerId, "CHECKING", new BigDecimal("1000.00"));
        Account toAccount = new Account(toAccountId, customerId, "SAVINGS", new BigDecimal("500.00"));
        
        accountService.createAccount(fromAccount);
        accountService.createAccount(toAccount);
    }

    @Test
    @DisplayName("Given a valid account and amount, when deposit is performed, then balance should increase")
    void testDepositSuccess() {
        // Given
        BigDecimal depositAmount = new BigDecimal("200.00");
        BigDecimal expectedBalance = new BigDecimal("1200.00");
        
        // When
        boolean result = transactionService.deposit(fromAccountId, depositAmount, "Test deposit");
        
        // Then
        assertTrue(result, "Deposit should be successful");
        Account updatedAccount = accountService.getAccountById(fromAccountId);
        assertEquals(expectedBalance, updatedAccount.getBalance(), "Balance should be updated correctly");
    }

    @Test
    @DisplayName("Given a valid account with sufficient balance, when withdrawal is performed, then balance should decrease")
    void testWithdrawalSuccess() {
        // Given
        BigDecimal withdrawalAmount = new BigDecimal("300.00");
        BigDecimal expectedBalance = new BigDecimal("700.00");
        
        // When
        boolean result = transactionService.withdraw(fromAccountId, withdrawalAmount, "Test withdrawal");
        
        // Then
        assertTrue(result, "Withdrawal should be successful");
        Account updatedAccount = accountService.getAccountById(fromAccountId);
        assertEquals(expectedBalance, updatedAccount.getBalance(), "Balance should be updated correctly");
    }

    @Test
    @DisplayName("Given an account with insufficient balance, when withdrawal is attempted, then transaction should fail")
    void testWithdrawalInsufficientFunds() {
        // Given
        BigDecimal withdrawalAmount = new BigDecimal("1500.00"); // More than available balance
        
        // When
        boolean result = transactionService.withdraw(fromAccountId, withdrawalAmount, "Test withdrawal");
        
        // Then
        assertFalse(result, "Withdrawal should fail due to insufficient funds");
        Account account = accountService.getAccountById(fromAccountId);
        assertEquals(new BigDecimal("1000.00"), account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    @DisplayName("Given two valid accounts, when transfer is performed, then balances should be updated correctly")
    void testTransferSuccess() {
        // Given
        BigDecimal transferAmount = new BigDecimal("250.00");
        BigDecimal expectedFromBalance = new BigDecimal("750.00");
        BigDecimal expectedToBalance = new BigDecimal("750.00");
        
        // When
        boolean result = transactionService.transfer(fromAccountId, toAccountId, transferAmount, "Test transfer");
        
        // Then
        assertTrue(result, "Transfer should be successful");
        
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);
        
        assertEquals(expectedFromBalance, fromAccount.getBalance(), "From account balance should be decreased");
        assertEquals(expectedToBalance, toAccount.getBalance(), "To account balance should be increased");
    }

    @Test
    @DisplayName("Given insufficient balance in source account, when transfer is attempted, then transaction should fail")
    void testTransferInsufficientFunds() {
        // Given
        BigDecimal transferAmount = new BigDecimal("1500.00"); // More than available balance
        BigDecimal originalFromBalance = new BigDecimal("1000.00");
        BigDecimal originalToBalance = new BigDecimal("500.00");
        
        // When
        boolean result = transactionService.transfer(fromAccountId, toAccountId, transferAmount, "Test transfer");
        
        // Then
        assertFalse(result, "Transfer should fail due to insufficient funds");
        
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);
        
        assertEquals(originalFromBalance, fromAccount.getBalance(), "From account balance should remain unchanged");
        assertEquals(originalToBalance, toAccount.getBalance(), "To account balance should remain unchanged");
    }

    @Test
    @DisplayName("Given invalid account ID, when any transaction is attempted, then transaction should fail")
    void testTransactionWithInvalidAccount() {
        // Given
        String invalidAccountId = "INVALID_ACCOUNT";
        BigDecimal amount = new BigDecimal("100.00");
        
        // When & Then
        assertFalse(transactionService.deposit(invalidAccountId, amount, "Test deposit"), 
                   "Deposit should fail with invalid account");
        assertFalse(transactionService.withdraw(invalidAccountId, amount, "Test withdrawal"), 
                   "Withdrawal should fail with invalid account");
        assertFalse(transactionService.transfer(invalidAccountId, toAccountId, amount, "Test transfer"), 
                   "Transfer should fail with invalid source account");
        assertFalse(transactionService.transfer(fromAccountId, invalidAccountId, amount, "Test transfer"), 
                   "Transfer should fail with invalid destination account");
    }

    @Test
    @DisplayName("Given completed transactions, when transaction history is retrieved, then all transactions should be present")
    void testTransactionHistory() {
        // Given
        BigDecimal depositAmount = new BigDecimal("100.00");
        BigDecimal withdrawalAmount = new BigDecimal("50.00");
        
        // When
        transactionService.deposit(fromAccountId, depositAmount, "Test deposit");
        transactionService.withdraw(fromAccountId, withdrawalAmount, "Test withdrawal");
        
        // Then
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(fromAccountId);
        assertEquals(2, transactions.size(), "Should have 2 transactions");
        
        // Verify transaction queue and stack
        assertEquals(2, transactionService.getTransactionQueue().getCompletedTransactionCount(), 
                    "Queue should have 2 completed transactions");
        assertEquals(2, transactionService.getTransactionStack().size(), 
                    "Stack should have 2 transactions");
    }

    @Test
    @DisplayName("Given multiple transactions, when data structures are accessed, then they should maintain correct state")
    void testDataStructures() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        
        // When
        transactionService.deposit(fromAccountId, amount, "Deposit 1");
        transactionService.withdraw(fromAccountId, amount, "Withdrawal 1");
        transactionService.transfer(fromAccountId, toAccountId, amount, "Transfer 1");
        
        // Then
        // Test Queue
        assertEquals(3, transactionService.getTransactionQueue().getCompletedTransactionCount());
        assertEquals(0, transactionService.getTransactionQueue().getPendingTransactionCount());
        
        // Test Stack
        assertEquals(3, transactionService.getTransactionStack().size());
        assertNotNull(transactionService.getTransactionStack().peekLastTransaction());
        assertEquals("TRANSFER", transactionService.getTransactionStack().peekLastTransaction().getTransactionType());
    }
}