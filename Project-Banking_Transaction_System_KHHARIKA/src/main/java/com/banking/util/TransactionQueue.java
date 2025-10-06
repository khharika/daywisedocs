package com.banking.util;

import com.banking.model.Transaction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TransactionQueue {
    private final BlockingQueue<Transaction> queue;
    
    public TransactionQueue() {
        this.queue = new LinkedBlockingQueue<>();
    }
    
    public void addTransaction(Transaction transaction) {
        try {
            queue.put(transaction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to add transaction to queue", e);
        }
    }
    
    public Transaction getNextTransaction() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get transaction from queue", e);
        }
    }
    
    public int getQueueSize() {
        return queue.size();
    }
    
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}