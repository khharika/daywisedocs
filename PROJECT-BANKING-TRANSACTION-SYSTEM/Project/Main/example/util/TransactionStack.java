package org.example.util;

import org.example.model.Transaction;
import java.util.Stack;

public class TransactionStack {
    private final Stack<Transaction> transactionHistory;

    public TransactionStack() {
        this.transactionHistory = new Stack<>();
    }

    public void pushTransaction(Transaction transaction) {
        transactionHistory.push(transaction);
    }

    public Transaction popTransaction() {
        return transactionHistory.isEmpty() ? null : transactionHistory.pop();
    }

    public Transaction peekLastTransaction() {
        return transactionHistory.isEmpty() ? null : transactionHistory.peek();
    }

    public boolean isEmpty() {
        return transactionHistory.isEmpty();
    }

    public int size() {
        return transactionHistory.size();
    }

    public Stack<Transaction> getTransactionHistory() {
        return new Stack<Transaction>() {{
            addAll(transactionHistory);
        }};
    }
}