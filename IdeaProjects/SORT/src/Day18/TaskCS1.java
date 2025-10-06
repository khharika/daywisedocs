package Day18;

import java.util.*;

public class TaskCS1 {

    // Array of LinkedLists to handle collisions using chaining
    LinkedList<Entry>[] data = new LinkedList[10];

    public void put(String keyval, int value) {
        int index = Math.abs(keyval.hashCode() % data.length);

        if (data[index] == null) {
            data[index] = new LinkedList<>();
        }

        // Check if key already exists, update value if so
        for (Entry e : data[index]) {
            if (e.keyval.equals(keyval)) {
                e.value = value;
                return;
            }
        }

        // If key not found, add new entry to the list
        data[index].add(new Entry(keyval, value));
    }

    public Integer get(String keyval) {
        int index = Math.abs(keyval.hashCode() % data.length);

        if (data[index] != null) {
            for (Entry e : data[index]) {
                if (e.keyval.equals(keyval)) {
                    return e.value;
                }
            }
        }

        return null; // Key not found
    }

    static class Entry {
        String keyval;
        int value;

        Entry(String k, int v) {
            keyval = k;
            value = v;
        }
    }

    // Sample main method to test the hash table
    public static void main(String[] args) {
        TaskCS1 table = new TaskCS1();

        table.put("apple", 100);
        table.put("banana", 200);
        table.put("apple", 300); // Updating existing key

        System.out.println("apple: " + table.get("apple"));   // Output: 300
        System.out.println("banana: " + table.get("banana")); // Output: 200
        System.out.println("grape: " + table.get("grape"));   // Output: null
    }
}

