package org.example.util;

import org.example.model.Transaction;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionStack {
    private final Stack<Transaction> stack;
    private final ReentrantLock lock;

    public TransactionStack() {
        this.stack = new Stack<>();
        this.lock = new ReentrantLock();
    }

    public void push(Transaction transaction) {
        lock.lock();
        try {
            stack.push(transaction);
        } finally {
            lock.unlock();
        }
    }

    public Transaction pop() {
        lock.lock();
        try {
            return stack.isEmpty() ? null : stack.pop();
        } finally {
            lock.unlock();
        }
    }

    public Transaction peek() {
        lock.lock();
        try {
            return stack.isEmpty() ? null : stack.peek();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}