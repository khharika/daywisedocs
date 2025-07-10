package day14;
import java.util.Stack;
public class STask7 {
    public static void main(String[] args) {
        Stack<Integer> numbers = new Stack<>();
        numbers.push(10);
        numbers.push(20);
        numbers.push(30);
        System.out.println("Stack after pushing elements: " + numbers);
        int poppedElement = numbers.pop();
        System.out.println("Popped element: " + poppedElement);
        System.out.println("Stack after popping: " + numbers);
        if (numbers.isEmpty()) {
            System.out.println("Stack is empty.");
        } else {
            System.out.println("Stack is not empty.");
        }
    }
}

