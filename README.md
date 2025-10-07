# Banking Transaction System

A comprehensive Java-based banking transaction system with audit logging, DynamoDB integration, and complete testing suite.

## Features

### Core Banking Operations
- **Customer Management**: Create and manage customer profiles
- **Account Management**: Create accounts (Savings, Checking, Business)
- **Transaction Processing**: Deposit, Withdrawal, and Transfer operations
- **Balance Management**: Real-time balance tracking and updates

### Advanced Features
- **Audit Logging**: Complete audit trail for all operations
- **DynamoDB Integration**: NoSQL storage for audit logs with eventual consistency
- **ACID Transactions**: Ensures data consistency for financial operations
- **Data Structures**: Stack and Queue implementation for transaction processing
- **Concurrent Processing**: Thread-safe transaction handling
- **Interactive CLI**: User-friendly command-line interface

### Technical Features
- **Exception Handling**: Custom banking exceptions with error codes
- **Logging**: Comprehensive logging with Logback
- **Testing**: JUnit 5 test suite with BDD-style tests
- **Repository Pattern**: Clean separation of data access logic

## Project Structure

```
src/
├── main/java/org/example/
│   ├── model/              # Domain models
│   │   ├── Customer.java
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   └── AuditLog.java
│   ├── service/            # Business logic
│   │   ├── CustomerService.java
│   │   ├── AccountService.java
│   │   ├── TransactionService.java
│   │   └── AuditService.java
│   ├── repository/         # Data access
│   │   ├── InMemoryRepository.java
│   │   └── DynamoDBRepository.java
│   ├── util/              # Utility classes
│   │   ├── TransactionQueue.java
│   │   └── TransactionStack.java
│   ├── exception/         # Custom exceptions
│   │   └── BankingException.java
│   ├── BankingSystem.java # Main application
│   └── Main.java          # Entry point
└── test/java/org/example/
    └── service/           # Unit tests
        ├── TransactionServiceTest.java
        └── AccountServiceTest.java
```

## Requirements Met

### Day 1 - Requirements ✅
- Money transfer functionality
- Comprehensive audit logs

### Day 2 - Design ✅
- Customer, Account, Transaction domain models
- Clean architecture with service layer

### Day 3 - Java ✅
- Deposit and withdrawal services
- Object-oriented design with proper encapsulation

### Day 4 - SQL ✅
- ACID transaction principles implemented
- Consistent data operations

### Day 5 - Logs ✅
- Audit log insertion for all operations
- Structured logging with Logback

### Day 6 - DynamoDB ✅
- NoSQL integration for audit logs
- Eventual consistency model

### Day 7 - Data Structures ✅
- Stack implementation for transaction history
- Queue implementation for transaction processing

### Day 8 - BDD Testing ✅
- JUnit 5 test suite
- Behavior-driven test scenarios

### Day 10 - Demo ✅
- Interactive banking system demo
- Complete user interface

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- AWS credentials (optional, for DynamoDB)

### Installation

1. Clone the repository
2. Navigate to project directory
3. Build the project:
   ```bash
   mvn clean compile
   ```

### Running the Application

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

### Running Tests

```bash
mvn test
```

### AWS DynamoDB Setup (Optional)

If you want to use DynamoDB for audit logs:

1. Configure AWS credentials:
   ```bash
   aws configure
   ```

2. The application will automatically create the required table `BankingAuditLogs`

3. If DynamoDB is not available, the system falls back to in-memory storage

## Usage

The system provides an interactive menu with the following options:

1. **Create Customer** - Register new customers
2. **Create Account** - Open new accounts for customers
3. **Deposit Money** - Add funds to accounts
4. **Withdraw Money** - Remove funds from accounts
5. **Transfer Money** - Move funds between accounts
6. **Check Balance** - View account balances
7. **View Transaction History** - See all transactions for an account
8. **View All Customers** - List all registered customers
9. **View All Accounts** - List all accounts in the system
10. **View Audit Logs** - See system audit trail
11. **View System Statistics** - Display system metrics

## Sample Data

The system initializes with sample data:
- Two customers: John Doe and Jane Smith
- Two accounts with initial deposits
- Ready-to-use transaction capabilities

## Error Handling

The system includes comprehensive error handling:
- `InsufficientFundsException` - For overdraft attempts
- `AccountNotFoundException` - For invalid account operations
- `InvalidTransactionException` - For invalid transaction parameters
- `AccountFrozenException` - For operations on frozen accounts

## Logging

All operations are logged with different levels:
- **INFO**: Normal operations and transactions
- **WARN**: Non-critical issues (e.g., DynamoDB unavailable)
- **ERROR**: Critical errors and exceptions

Logs are written to both console and file (`logs/banking-system.log`)

## Testing

The test suite includes:
- Unit tests for all service classes
- Integration tests for transaction flows
- Concurrent transaction testing
- Error condition testing
- BDD-style test scenarios

## Architecture

The system follows clean architecture principles:
- **Domain Models**: Core business entities
- **Services**: Business logic and operations
- **Repositories**: Data access abstraction
- **Utilities**: Supporting data structures
- **Exception Handling**: Centralized error management

## Thread Safety

All critical operations are thread-safe:
- Transaction processing uses locks
- Repository operations are concurrent-safe
- Queue and Stack implementations are thread-safe

## Future Enhancements

- REST API endpoints
- Database persistence (PostgreSQL/MySQL)
- User authentication and authorization
- Transaction limits and validation rules
- Notification system
- Reporting and analytics