package org.example.util;

import org.example.model.Transaction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionQueue {
    private final ConcurrentLinkedQueue<Transaction> queue;
    private final ReentrantLock lock;

    public TransactionQueue() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
    }

    public void enqueue(Transaction transaction) {
        lock.lock();
        try {
            queue.offer(transaction);
        } finally {
            lock.unlock();
        }
    }

    public Transaction dequeue() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public Transaction peek() {
        return queue.peek();
    }
}