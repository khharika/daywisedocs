import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.service.BankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankingServiceTest {
    private BankingService bankingService;
    
    @BeforeEach
    void setUp() {
        bankingService = new BankingService();
    }
    
    @Test
    @DisplayName("Should create customer with valid details")
    void testCreateCustomer() {
        Customer customer = bankingService.createCustomer("John Doe", "john@example.com", "123-456-7890");
        
        assertNotNull(customer);
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("123-456-7890", customer.getPhone());
        assertTrue(customer.getCustomerId().startsWith("CUST-"));
    }
    
    @Test
    @DisplayName("Should create account with initial balance")
    void testCreateAccount() {
        Customer customer = bankingService.createCustomer("Jane Doe", "jane@example.com", "098-765-4321");
        Account account = bankingService.createAccount(customer.getCustomerId(), "SAVINGS", new BigDecimal("1000.00"));
        
        assertNotNull(account);
        assertEquals(customer.getCustomerId(), account.getCustomerId());
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
        assertTrue(account.getAccountId().startsWith("ACC-"));
    }
    
    @Test
    @DisplayName("Should retrieve all customer accounts")
    void testGetCustomerAccounts() {
        Customer customer = bankingService.createCustomer("Bob Smith", "bob@example.com", "555-123-4567");
        bankingService.createAccount(customer.getCustomerId(), "CHECKING", new BigDecimal("500.00"));
        bankingService.createAccount(customer.getCustomerId(), "SAVINGS", new BigDecimal("2000.00"));
        
        List<Account> accounts = bankingService.getCustomerAccounts(customer.getCustomerId());
        
        assertEquals(2, accounts.size());
    }
    
    @Test
    @DisplayName("Should successfully deposit money and update balance")
    void testDeposit() {
        Customer customer = bankingService.createCustomer("Alice Johnson", "alice@example.com", "777-888-9999");
        Account account = bankingService.createAccount(customer.getCustomerId(), "CHECKING", new BigDecimal("100.00"));
        
        boolean result = bankingService.deposit(account.getAccountId(), new BigDecimal("50.00"), "Test deposit");
        assertTrue(result);
        
        Account updatedAccount = bankingService.getAccount(account.getAccountId());
        assertEquals(new BigDecimal("150.00"), updatedAccount.getBalance());
    }
    
    @Test
    @DisplayName("Should successfully withdraw money and update balance")
    void testWithdraw() {
        Customer customer = bankingService.createCustomer("Charlie Brown", "charlie@example.com", "111-222-3333");
        Account account = bankingService.createAccount(customer.getCustomerId(), "CHECKING", new BigDecimal("200.00"));
        
        boolean result = bankingService.withdraw(account.getAccountId(), new BigDecimal("75.00"), "Test withdrawal");
        assertTrue(result);
        
        Account updatedAccount = bankingService.getAccount(account.getAccountId());
        assertEquals(new BigDecimal("125.00"), updatedAccount.getBalance());
    }
    
    @Test
    @DisplayName("Should throw exception for insufficient funds")
    void testWithdrawInsufficientFunds() {
        Customer customer = bankingService.createCustomer("David Wilson", "david@example.com", "444-555-6666");
        Account account = bankingService.createAccount(customer.getCustomerId(), "SAVINGS", new BigDecimal("50.00"));
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.withdraw(account.getAccountId(), new BigDecimal("100.00"), "Overdraft attempt");
        });
        
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }
    
    @Test
    @DisplayName("Should throw exception for negative deposit amount")
    void testNegativeDeposit() {
        Customer customer = bankingService.createCustomer("Eva Green", "eva@example.com", "999-888-7777");
        Account account = bankingService.createAccount(customer.getCustomerId(), "CHECKING", new BigDecimal("100.00"));
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.deposit(account.getAccountId(), new BigDecimal("-50.00"), "Invalid deposit");
        });
        
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for negative withdrawal amount")
    void testNegativeWithdraw() {
        Customer customer = bankingService.createCustomer("Frank Miller", "frank@example.com", "123-987-4567");
        Account account = bankingService.createAccount(customer.getCustomerId(), "SAVINGS", new BigDecimal("500.00"));
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.withdraw(account.getAccountId(), new BigDecimal("-25.00"), "Invalid withdrawal");
        });
        
        assertEquals("Withdrawal amount must be positive", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for non-existent account")
    void testNonExistentAccount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bankingService.deposit("ACC-INVALID", new BigDecimal("100.00"), "Test deposit");
        });
        
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    @Test
    @DisplayName("Should handle multiple transactions correctly")
    void testMultipleTransactions() {
        Customer customer = bankingService.createCustomer("Grace Lee", "grace@example.com", "555-111-2222");
        Account account = bankingService.createAccount(customer.getCustomerId(), "CHECKING", new BigDecimal("1000.00"));
        
        // Multiple deposits and withdrawals
        bankingService.deposit(account.getAccountId(), new BigDecimal("200.00"), "Deposit 1");
        bankingService.withdraw(account.getAccountId(), new BigDecimal("150.00"), "Withdrawal 1");
        bankingService.deposit(account.getAccountId(), new BigDecimal("300.00"), "Deposit 2");
        bankingService.withdraw(account.getAccountId(), new BigDecimal("100.00"), "Withdrawal 2");
        
        Account finalAccount = bankingService.getAccount(account.getAccountId());
        assertEquals(new BigDecimal("1250.00"), finalAccount.getBalance());
    }
}