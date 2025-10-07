package org.example.service;

import org.example.exception.BankingException;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.model.Transaction;
import org.example.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    private InMemoryRepository repository;
    private AuditService auditService;
    private CustomerService customerService;
    private AccountService accountService;
    private TransactionService transactionService;
    private Customer testCustomer1;
    private Customer testCustomer2;
    private Account testAccount1;
    private Account testAccount2;

    @BeforeEach
    void setUp() throws BankingException {
        repository = new InMemoryRepository();
        auditService = new AuditService(repository);
        customerService = new CustomerService(repository, auditService);
        accountService = new AccountService(repository, auditService);
        transactionService = new TransactionService(repository, accountService, auditService);

        // Create test customers
        testCustomer1 = customerService.createCustomer("John", "Doe", "john@test.com", "123-456-7890");
        testCustomer2 = customerService.createCustomer("Jane", "Smith", "jane@test.com", "098-765-4321");

        // Create test accounts
        testAccount1 = accountService.createAccount(testCustomer1.getCustomerId(), Account.AccountType.CHECKING);
        testAccount2 = accountService.createAccount(testCustomer2.getCustomerId(), Account.AccountType.SAVINGS);
    }

    @Test
    @DisplayName("Should successfully deposit money to account")
    void testDeposit() throws BankingException {
        // Given
        BigDecimal depositAmount = new BigDecimal("500.00");
        String description = "Test deposit";

        // When
        Transaction transaction = transactionService.deposit(testAccount1.getAccountId(), depositAmount, description);

        // Then
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.DEPOSIT, transaction.getType());
        assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(depositAmount, transaction.getAmount());
        assertEquals(testAccount1.getAccountId(), transaction.getToAccountId());
        assertEquals(description, transaction.getDescription());

        // Verify balance updated
        BigDecimal newBalance = accountService.getBalance(testAccount1.getAccountId());
        assertEquals(depositAmount, newBalance);
    }

    @Test
    @DisplayName("Should fail deposit with negative amount")
    void testDepositNegativeAmount() {
        // Given
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            transactionService.deposit(testAccount1.getAccountId(), negativeAmount, "Invalid deposit");
        });

        assertEquals("INVALID_AMOUNT", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("positive"));
    }

    @Test
    @DisplayName("Should successfully withdraw money from account with sufficient funds")
    void testWithdraw() throws BankingException {
        // Given - First deposit some money
        BigDecimal initialDeposit = new BigDecimal("1000.00");
        transactionService.deposit(testAccount1.getAccountId(), initialDeposit, "Initial deposit");

        BigDecimal withdrawAmount = new BigDecimal("300.00");
        String description = "Test withdrawal";

        // When
        Transaction transaction = transactionService.withdraw(testAccount1.getAccountId(), withdrawAmount, description);

        // Then
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(withdrawAmount, transaction.getAmount());
        assertEquals(testAccount1.getAccountId(), transaction.getFromAccountId());
        assertEquals(description, transaction.getDescription());

        // Verify balance updated
        BigDecimal expectedBalance = initialDeposit.subtract(withdrawAmount);
        BigDecimal actualBalance = accountService.getBalance(testAccount1.getAccountId());
        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    @DisplayName("Should fail withdrawal with insufficient funds")
    void testWithdrawInsufficientFunds() throws BankingException {
        // Given - Account with zero balance
        BigDecimal withdrawAmount = new BigDecimal("100.00");

        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            transactionService.withdraw(testAccount1.getAccountId(), withdrawAmount, "Invalid withdrawal");
        });

        assertEquals("INSUFFICIENT_FUNDS", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    @DisplayName("Should successfully transfer money between accounts")
    void testTransfer() throws BankingException {
        // Given - Add money to source account
        BigDecimal initialAmount = new BigDecimal("1000.00");
        transactionService.deposit(testAccount1.getAccountId(), initialAmount, "Initial deposit");

        BigDecimal transferAmount = new BigDecimal("400.00");
        String description = "Test transfer";

        // When
        Transaction transaction = transactionService.transfer(
            testAccount1.getAccountId(), 
            testAccount2.getAccountId(), 
            transferAmount, 
            description
        );

        // Then
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.TRANSFER, transaction.getType());
        assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(transferAmount, transaction.getAmount());
        assertEquals(testAccount1.getAccountId(), transaction.getFromAccountId());
        assertEquals(testAccount2.getAccountId(), transaction.getToAccountId());
        assertEquals(description, transaction.getDescription());

        // Verify balances updated
        BigDecimal sourceBalance = accountService.getBalance(testAccount1.getAccountId());
        BigDecimal destinationBalance = accountService.getBalance(testAccount2.getAccountId());
        
        assertEquals(initialAmount.subtract(transferAmount), sourceBalance);
        assertEquals(transferAmount, destinationBalance);
    }

    @Test
    @DisplayName("Should fail transfer to same account")
    void testTransferToSameAccount() throws BankingException {
        // Given
        BigDecimal transferAmount = new BigDecimal("100.00");

        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            transactionService.transfer(
                testAccount1.getAccountId(), 
                testAccount1.getAccountId(), 
                transferAmount, 
                "Invalid transfer"
            );
        });

        assertEquals("INVALID_TRANSFER", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("same account"));
    }

    @Test
    @DisplayName("Should fail transfer with insufficient funds")
    void testTransferInsufficientFunds() {
        // Given - Account with zero balance
        BigDecimal transferAmount = new BigDecimal("500.00");

        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            transactionService.transfer(
                testAccount1.getAccountId(), 
                testAccount2.getAccountId(), 
                transferAmount, 
                "Invalid transfer"
            );
        });

        assertEquals("INSUFFICIENT_FUNDS", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    @DisplayName("Should maintain transaction history")
    void testTransactionHistory() throws BankingException {
        // Given - Perform multiple transactions
        transactionService.deposit(testAccount1.getAccountId(), new BigDecimal("1000.00"), "Deposit 1");
        transactionService.withdraw(testAccount1.getAccountId(), new BigDecimal("200.00"), "Withdrawal 1");
        transactionService.deposit(testAccount1.getAccountId(), new BigDecimal("300.00"), "Deposit 2");

        // When
        var history = transactionService.getTransactionHistory(testAccount1.getAccountId());

        // Then
        assertEquals(3, history.size());
        
        // Verify all transactions are for the correct account
        for (Transaction transaction : history) {
            assertTrue(testAccount1.getAccountId().equals(transaction.getFromAccountId()) || 
                      testAccount1.getAccountId().equals(transaction.getToAccountId()));
        }
    }

    @Test
    @DisplayName("Should track pending transactions in queue")
    void testTransactionQueue() throws BankingException {
        // Given
        int initialPendingCount = transactionService.getPendingTransactionCount();
        
        // When - Perform a transaction (it gets processed immediately in our implementation)
        transactionService.deposit(testAccount1.getAccountId(), new BigDecimal("100.00"), "Test deposit");
        
        // Then - In our implementation, transactions are processed immediately
        // so the pending count should remain the same
        int finalPendingCount = transactionService.getPendingTransactionCount();
        assertEquals(initialPendingCount, finalPendingCount);
    }

    @Test
    @DisplayName("Should track last transaction in stack")
    void testTransactionStack() throws BankingException {
        // Given - No previous transactions
        Transaction initialLastTransaction = transactionService.getLastTransaction();
        
        // When
        Transaction newTransaction = transactionService.deposit(testAccount1.getAccountId(), new BigDecimal("250.00"), "Stack test");
        
        // Then
        Transaction lastTransaction = transactionService.getLastTransaction();
        assertNotNull(lastTransaction);
        assertEquals(newTransaction.getTransactionId(), lastTransaction.getTransactionId());
        assertEquals(Transaction.TransactionType.DEPOSIT, lastTransaction.getType());
    }

    @Test
    @DisplayName("Should handle concurrent transactions safely")
    void testConcurrentTransactions() throws BankingException, InterruptedException {
        // Given - Initial deposit
        transactionService.deposit(testAccount1.getAccountId(), new BigDecimal("1000.00"), "Initial");
        
        // When - Simulate concurrent withdrawals
        Thread thread1 = new Thread(() -> {
            try {
                transactionService.withdraw(testAccount1.getAccountId(), new BigDecimal("100.00"), "Concurrent 1");
            } catch (BankingException e) {
                // Expected in some cases due to insufficient funds
            }
        });
        
        Thread thread2 = new Thread(() -> {
            try {
                transactionService.withdraw(testAccount1.getAccountId(), new BigDecimal("150.00"), "Concurrent 2");
            } catch (BankingException e) {
                // Expected in some cases due to insufficient funds
            }
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        
        // Then - Balance should be consistent (not negative)
        BigDecimal finalBalance = accountService.getBalance(testAccount1.getAccountId());
        assertTrue(finalBalance.compareTo(BigDecimal.ZERO) >= 0);
    }
}