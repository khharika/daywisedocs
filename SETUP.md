# Banking Transaction System - Setup Guide

## Quick Start (Recommended for Beginners)

### Option 1: Using IntelliJ IDEA (Easiest)

1. **Open the project in IntelliJ IDEA**
   - File → Open → Select the `BANKINGTRANSACTIONPROJECT` folder
   - Wait for IntelliJ to import the Maven project

2. **Run the application**
   - Navigate to `src/main/java/org/example/Main.java`
   - Right-click on `Main.java` → Run 'Main.main()'
   - The interactive banking system will start

3. **Run tests**
   - Right-click on `src/test/java` folder → Run 'All Tests'
   - Or run individual test files

### Option 2: Using Command Line (If Maven is installed)

1. **Check if Maven is installed**
   ```cmd
   mvn --version
   ```

2. **If Maven is available, run:**
   ```cmd
   run.bat
   ```
   Or manually:
   ```cmd
   mvn clean compile
   mvn exec:java -Dexec.mainClass="org.example.Main"
   ```

3. **To run tests:**
   ```cmd
   test.bat
   ```
   Or manually:
   ```cmd
   mvn test
   ```

## Project Features

### ✅ Complete Banking System Implementation
- **Customer Management**: Create and manage customers
- **Account Operations**: Create accounts, check balances
- **Transactions**: Deposit, withdraw, transfer money
- **Audit Logging**: Complete audit trail
- **DynamoDB Integration**: NoSQL storage (optional)
- **Data Structures**: Stack & Queue for transactions
- **JUnit Testing**: Comprehensive test suite
- **Interactive UI**: User-friendly command-line interface

### ✅ All Requirements Met
- ✅ Day 1: Money transfer, audit logs
- ✅ Day 2: Customer, Account, Transaction models
- ✅ Day 3: Deposit/withdraw services
- ✅ Day 4: ACID transaction principles
- ✅ Day 5: Audit log insertion
- ✅ Day 6: DynamoDB eventual consistency
- ✅ Day 7: Stack & queue data structures
- ✅ Day 8: JUnit BDD tests
- ✅ Day 10: Interactive demo

## Sample Usage

When you run the application, you'll see:

```
=== Welcome to Banking Transaction System ===
System initialized with sample data

=== Banking System Menu ===
1. Create Customer
2. Create Account
3. Deposit Money
4. Withdraw Money
5. Transfer Money
6. Check Balance
7. View Transaction History
8. View All Customers
9. View All Accounts
10. View Audit Logs
11. View System Statistics
0. Exit
```

## Sample Data Included

The system starts with:
- 2 sample customers (John Doe, Jane Smith)
- 2 sample accounts with initial balances
- Ready for immediate testing

## Error Handling

The system handles all common banking errors:
- Insufficient funds
- Invalid accounts
- Negative amounts
- Account status issues
- Concurrent transaction safety

## AWS DynamoDB (Optional)

- If AWS credentials are configured, audit logs will be stored in DynamoDB
- If not available, the system uses in-memory storage
- No setup required - works out of the box

## Testing

The project includes comprehensive tests:
- Transaction service tests
- Account service tests
- Error condition testing
- Concurrent transaction testing

## Troubleshooting

### If you get compilation errors:
1. Make sure you're using Java 17 or higher
2. Refresh the Maven project in your IDE
3. Check that all dependencies are downloaded

### If Maven is not found:
1. Use IntelliJ IDEA (recommended for beginners)
2. Or install Maven from https://maven.apache.org/

### If DynamoDB errors occur:
- The system will automatically fall back to in-memory storage
- No action needed - this is expected behavior

## Project Structure

```
BANKINGTRANSACTIONPROJECT/
├── src/main/java/org/example/
│   ├── model/              # Customer, Account, Transaction, AuditLog
│   ├── service/            # Business logic services
│   ├── repository/         # Data access (In-memory + DynamoDB)
│   ├── util/              # Stack & Queue implementations
│   ├── exception/         # Custom banking exceptions
│   └── BankingSystem.java # Main interactive system
├── src/test/java/         # JUnit test suite
├── src/main/resources/    # Configuration files
├── README.md             # Detailed documentation
├── run.bat              # Windows run script
└── test.bat            # Windows test script
```

## Next Steps

1. Run the application using IntelliJ IDEA
2. Try all the banking operations
3. Run the test suite to see BDD testing
4. Explore the code to understand the architecture
5. Check the logs in the `logs/` directory

The system is production-ready with proper error handling, logging, testing, and documentation!