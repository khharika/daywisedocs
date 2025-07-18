package Day18;

import java.util.Scanner;

public class TaskCS2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a 6-digit number: ");
        int num = scanner.nextInt();

        if (num < 100000 || num > 999999) {
            System.out.println("Please enter a valid 6-digit number.");
            return;
        }

        int units = num % 10;
        int tens = (num / 10) % 10;
        int hundreds = (num / 100) % 10;
        int thousands = (num / 1000) % 10;
        int tenThousands = (num / 10000) % 10;
        int lakhs = (num / 100000) % 10;

        System.out.println("Units digit is " + units);
        System.out.println("Ones digit is " + tens);
        System.out.println("Hundreds digit is " + hundreds);
        System.out.println("Thousands digit is " + thousands);
        System.out.println("Ten Thousands digit is " + tenThousands);
        System.out.println("Lakhs digit is " + lakhs);

        scanner.close();
    }
}

