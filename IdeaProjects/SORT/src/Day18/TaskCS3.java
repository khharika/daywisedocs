package Day18;
import java.util.Scanner;
public class TaskCS3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number: ");
        long number = scanner.nextLong();

        // Make sure the number is positive
        number = Math.abs(number);

        // Special case for 0
        if (number == 0) {
            System.out.println("It is a 1 digit number.");
        } else {
            int count = 0;
            while (number > 0) {
                number = number / 10;
                count++;
            }
            System.out.println("It is a " + count + " digit number.");
        }

        scanner.close();
    }
}
