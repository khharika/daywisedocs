package org.example.util;

import org.example.model.Transaction;
import java.util.LinkedList;
import java.util.Queue;

public class TransactionQueue {
    private final Queue<Transaction> pendingTransactions = new LinkedList<>();
    private final Queue<Transaction> completedTransactions = new LinkedList<>();

    public void addPendingTransaction(Transaction t) { pendingTransactions.offer(t); }
    public Transaction getNextPendingTransaction() { return pendingTransactions.poll(); }
    public void addCompletedTransaction(Transaction t) { completedTransactions.offer(t); }
    public boolean hasPendingTransactions() { return !pendingTransactions.isEmpty(); }
    public int getPendingTransactionCount() { return pendingTransactions.size(); }
    public int getCompletedTransactionCount() { return completedTransactions.size(); }
}
