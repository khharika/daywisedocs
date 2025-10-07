package org.example.exception;

public class BankingException extends Exception {
    private final String errorCode;

    public BankingException(String message) {
        super(message);
        this.errorCode = "BANKING_ERROR";
    }

    public BankingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BANKING_ERROR";
    }

    public BankingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

class InsufficientFundsException extends BankingException {
    public InsufficientFundsException(String message) {
        super(message, "INSUFFICIENT_FUNDS");
    }
}

class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String message) {
        super(message, "ACCOUNT_NOT_FOUND");
    }
}

class InvalidTransactionException extends BankingException {
    public InvalidTransactionException(String message) {
        super(message, "INVALID_TRANSACTION");
    }
}

class AccountFrozenException extends BankingException {
    public AccountFrozenException(String message) {
        super(message, "ACCOUNT_FROZEN");
    }
}