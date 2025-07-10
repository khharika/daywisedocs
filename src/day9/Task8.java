package day9;
import java.util.Scanner;
class MyException extends Exception {
    public MyException(String message) {
        super(message); // calling the parent Exception constructor
    }
}
public class Task8 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your age: ");
        int age = sc.nextInt();
        try {
            if (age < 0) {
                // Throw custom exception if age is negative
                throw new MyException("Age cannot be negative!");
            } else {
                System.out.println("Valid age: " + age);
            }
        } catch (MyException ex) {
            System.out.println("Caught");
            System.out.println(ex.getMessage());
        }
    }
}
