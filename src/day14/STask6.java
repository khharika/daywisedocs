package day14;

import java.util.Stack;

public class STask6 {
    public static void main(String[] args) {

        Stack<String> names = new Stack<>();


        names.push("a");
        names.push("B");
        names.push("C");
        names.push("D");

        System.out.println("Stack: " + names);


        String searchName = "E";
        int position = names.search(searchName);

        // Display the position (1-based from top of stack)
        if (position == -1) {
            System.out.println(searchName + " not found in the stack.");
        } else {
            System.out.println(searchName + " found at position (from top): " + position);
        }
    }
}

