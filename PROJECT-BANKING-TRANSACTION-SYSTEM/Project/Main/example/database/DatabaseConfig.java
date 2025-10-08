/*
package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:h2:mem:bankingdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";
    
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            initializeTables();
        }
        return connection;
    }

    private static void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                        "customer_id VARCHAR(50) PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) UNIQUE NOT NULL, " +
                        "phone VARCHAR(20), " +
                        "password VARCHAR(100) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Database: customers table initialized");

            // Create accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                        "account_id VARCHAR(50) PRIMARY KEY, " +
                        "customer_id VARCHAR(50) NOT NULL, " +
                        "account_type VARCHAR(20) NOT NULL, " +
                        "balance DECIMAL(15,2) DEFAULT 0.00, " +
                        "is_active BOOLEAN DEFAULT TRUE, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))");
            System.out.println("Database: accounts table initialized");

            // Create transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                        "transaction_id VARCHAR(50) PRIMARY KEY, " +
                        "from_account_id VARCHAR(50), " +
                        "to_account_id VARCHAR(50), " +
                        "amount DECIMAL(15,2) NOT NULL, " +
                        "transaction_type VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'PENDING', " +
                        "description VARCHAR(255), " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Database: transactions table initialized");

            // Create audit_logs table
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (" +
                        "log_id VARCHAR(50) PRIMARY KEY, " +
                        "transaction_id VARCHAR(50), " +
                        "action VARCHAR(50), " +
                        "details TEXT, " +
                        "user_id VARCHAR(50), " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Database: audit_logs table initialized");
            System.out.println("Database initialization complete!");
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}*/
