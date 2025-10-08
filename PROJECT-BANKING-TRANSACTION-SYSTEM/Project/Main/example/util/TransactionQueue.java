package org.example.util;

import org.example.model.Transaction;
import java.util.LinkedList;
import java.util.Queue;

public class TransactionQueue {
    private final Queue<Transaction> pendingTransactions;
    private final Queue<Transaction> completedTransactions;

    public TransactionQueue() {
        this.pendingTransactions = new LinkedList<>();
        this.completedTransactions = new LinkedList<>();
    }

    public void addPendingTransaction(Transaction transaction) {
        pendingTransactions.offer(transaction);
    }

    public Transaction getNextPendingTransaction() {
        return pendingTransactions.poll();
    }

    public void addCompletedTransaction(Transaction transaction) {
        completedTransactions.offer(transaction);
    }

    public boolean hasPendingTransactions() {
        return !pendingTransactions.isEmpty();
    }

    public int getPendingTransactionCount() {
        return pendingTransactions.size();
    }

    public int getCompletedTransactionCount() {
        return completedTransactions.size();
    }

    public Queue<Transaction> getPendingTransactions() {
        return new LinkedList<>(pendingTransactions);
    }

    public Queue<Transaction> getCompletedTransactions() {
        return new LinkedList<>(completedTransactions);
    }
}