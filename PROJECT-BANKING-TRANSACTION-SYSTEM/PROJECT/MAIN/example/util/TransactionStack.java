package org.example.util;

import org.example.model.Transaction;
import java.util.Stack;

public class TransactionStack {
    private final Stack<Transaction> history = new Stack<>();

    public void pushTransaction(Transaction t) { history.push(t); }
    public Transaction popTransaction() { return history.isEmpty() ? null : history.pop(); }
    public Transaction peekLastTransaction() { return history.isEmpty() ? null : history.peek(); }
    public int size() { return history.size(); }
}
