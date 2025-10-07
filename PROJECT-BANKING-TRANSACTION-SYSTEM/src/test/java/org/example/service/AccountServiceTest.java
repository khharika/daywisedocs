package org.example.service;

import org.example.exception.BankingException;
import org.example.model.Account;
import org.example.model.Customer;
import org.example.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    private InMemoryRepository repository;
    private AuditService auditService;
    private CustomerService customerService;
    private AccountService accountService;
    private Customer testCustomer;

    @BeforeEach
    void setUp() throws BankingException {
        repository = new InMemoryRepository();
        auditService = new AuditService(repository);
        customerService = new CustomerService(repository, auditService);
        accountService = new AccountService(repository, auditService);

        testCustomer = customerService.createCustomer("John", "Doe", "john@test.com", "123-456-7890");
    }

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccount() throws BankingException {
        // When
        Account account = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.CHECKING);

        // Then
        assertNotNull(account);
        assertEquals(testCustomer.getCustomerId(), account.getCustomerId());
        assertEquals(Account.AccountType.CHECKING, account.getAccountType());
        assertEquals(Account.AccountStatus.ACTIVE, account.getStatus());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertNotNull(account.getAccountNumber());
        assertTrue(account.getAccountNumber().startsWith("ACC"));
    }

    @Test
    @DisplayName("Should fail to create account for non-existent customer")
    void testCreateAccountInvalidCustomer() {
        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            accountService.createAccount("invalid-customer-id", Account.AccountType.SAVINGS);
        });

        assertEquals("CUSTOMER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    @DisplayName("Should find account by ID")
    void testFindAccountById() throws BankingException {
        // Given
        Account createdAccount = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.SAVINGS);

        // When
        Account foundAccount = accountService.findAccountById(createdAccount.getAccountId());

        // Then
        assertNotNull(foundAccount);
        assertEquals(createdAccount.getAccountId(), foundAccount.getAccountId());
        assertEquals(createdAccount.getAccountNumber(), foundAccount.getAccountNumber());
    }

    @Test
    @DisplayName("Should return null for non-existent account ID")
    void testFindAccountByIdNotFound() {
        // When
        Account account = accountService.findAccountById("non-existent-id");

        // Then
        assertNull(account);
    }

    @Test
    @DisplayName("Should find account by account number")
    void testFindAccountByNumber() throws BankingException {
        // Given
        Account createdAccount = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.BUSINESS);

        // When
        Account foundAccount = accountService.findAccountByNumber(createdAccount.getAccountNumber());

        // Then
        assertNotNull(foundAccount);
        assertEquals(createdAccount.getAccountId(), foundAccount.getAccountId());
        assertEquals(createdAccount.getAccountNumber(), foundAccount.getAccountNumber());
    }

    @Test
    @DisplayName("Should update account status")
    void testUpdateAccountStatus() throws BankingException {
        // Given
        Account account = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.CHECKING);
        assertEquals(Account.AccountStatus.ACTIVE, account.getStatus());

        // When
        accountService.updateAccountStatus(account.getAccountId(), Account.AccountStatus.FROZEN, "ADMIN");

        // Then
        Account updatedAccount = accountService.findAccountById(account.getAccountId());
        assertEquals(Account.AccountStatus.FROZEN, updatedAccount.getStatus());
    }

    @Test
    @DisplayName("Should fail to update status for non-existent account")
    void testUpdateAccountStatusNotFound() {
        // When & Then
        BankingException exception = assertThrows(BankingException.class, () -> {
            accountService.updateAccountStatus("invalid-account-id", Account.AccountStatus.CLOSED, "ADMIN");
        });

        assertEquals("ACCOUNT_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    @DisplayName("Should get account balance")
    void testGetBalance() throws BankingException {
        // Given
        Account account = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.SAVINGS);

        // When
        BigDecimal balance = accountService.getBalance(account.getAccountId());

        // Then
        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    @DisplayName("Should update account balance")
    void testUpdateBalance() throws BankingException {
        // Given
        Account account = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.CHECKING);
        BigDecimal newBalance = new BigDecimal("1500.75");

        // When
        accountService.updateBalance(account.getAccountId(), newBalance, "SYSTEM");

        // Then
        BigDecimal updatedBalance = accountService.getBalance(account.getAccountId());
        assertEquals(newBalance, updatedBalance);
    }

    @Test
    @DisplayName("Should find accounts by customer ID")
    void testFindAccountsByCustomerId() throws BankingException {
        // Given
        Account account1 = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.CHECKING);
        Account account2 = accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.SAVINGS);

        // When
        var accounts = accountService.findAccountsByCustomerId(testCustomer.getCustomerId());

        // Then
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(acc -> acc.getAccountId().equals(account1.getAccountId())));
        assertTrue(accounts.stream().anyMatch(acc -> acc.getAccountId().equals(account2.getAccountId())));
    }

    @Test
    @DisplayName("Should return empty list for customer with no accounts")
    void testFindAccountsByCustomerIdEmpty() {
        // When
        var accounts = accountService.findAccountsByCustomerId("non-existent-customer");

        // Then
        assertTrue(accounts.isEmpty());
    }

    @Test
    @DisplayName("Should get all accounts")
    void testGetAllAccounts() throws BankingException {
        // Given
        int initialCount = accountService.getAllAccounts().size();
        accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.CHECKING);
        accountService.createAccount(testCustomer.getCustomerId(), Account.AccountType.SAVINGS);

        // When
        var allAccounts = accountService.getAllAccounts();

        // Then
        assertEquals(initialCount + 2, allAccounts.size());
    }
}