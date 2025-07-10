package day14;

import java.util.Stack;

public class Stask5 {
    public static void main(String[] args) {
        // Create a Stack of integers
        Stack<Integer> stack = new Stack<>();

        // Push elements into the stack
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);

        // Display the stack
        System.out.println("Stack after pushing elements: " + stack);

        // Pop and print elements from the stack
        System.out.println("Popping elements from the stack:");

        int poppedElement1 = stack.pop();
        System.out.println("Popped element: " + poppedElement1);

        int poppedElement2 = stack.pop();
        System.out.println("Popped element: " + poppedElement2);

        // Display the remaining stack
        System.out.println("Stack after popping elements: " + stack);
    }
}

