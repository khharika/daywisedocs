# Banking Transaction System (BTS)

A comprehensive banking system built with Java, featuring DynamoDB Local NoSQL database support.

## Features

- Customer management
- Account creation and management
- Transaction processing (deposits, withdrawals)
- DynamoDB Local for NoSQL database operations
- No cloud dependencies - runs completely offline
- Interactive console application
- Comprehensive JUnit testing

## Project Structure

```
BTS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── app/
│   │   │           │   └── BankingApp.java
│   │   │           ├── model/
│   │   │           │   ├── Account.java
│   │   │           │   ├── Customer.java
│   │   │           │   └── Transaction.java
│   │   │           ├── repository/
│   │   │           │   └── DatabaseService.java
│   │   │           └── service/
│   │   │               └── BankingService.java
│   │   └── resources/
│   └── test/
│       └── java/
│           └── BankingServiceTest.java
├── pom.xml
├── run.bat
├── test.bat
└── README.md
```

## Setup DynamoDB Local

1. **Download DynamoDB Local:**
   - Go to: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html
   - Download and extract to your project folder

2. **Start DynamoDB Local:**
   ```
   java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
   ```
   - This starts DynamoDB Local on http://localhost:8000

## How to Run

1. **Start DynamoDB Local first** (see setup above)

2. **Run the application:**
   ```
   run.bat
   ```

3. **Run tests:**
   ```
   test.bat
   ```

## Dependencies

- Java 17+
- Maven 3.6+
- H2 Database
- AWS SDK for DynamoDB
- JUnit 5
- SLF4J for logging

## Usage

The application provides an interactive menu with the following options:

1. Create Customer
2. Create Account
3. Deposit Money
4. Withdraw Money
5. View Customer Accounts
6. Exit

Follow the prompts to perform banking operations.