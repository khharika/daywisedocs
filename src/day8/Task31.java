package day8;

// Base class
class Calculation {
    int z;

    public void addition(int x, int y) {
        z = x + y;
        System.out.println("Sum: " + z);
    }

    public void subtraction(int x, int y) {
        z = x - y;
        System.out.println("Difference: " + z);
    }
}

// First level derived class
class My_Calculation extends Calculation {
    public void multiplication(int x, int y) {
        z = x * y;
        System.out.println("Product: " + z);
    }
}

// Second level derived class (Multilevel Inheritance)
public class Task31 extends My_Calculation {
    public void division(int x, int y) {
        if (y != 0) {
            z = x / y;
            System.out.println("Quotient: " + z);
        } else {
            System.out.println("Cannot divide by zero.");
        }
    }

    public static void main(String[] args) {
        Task31 obj = new Task31();
        int a = 20, b = 10;

        obj.addition(a, b);        // From Calculation
        obj.subtraction(a, b);     // From Calculation
        obj.multiplication(a, b);  // From My_Calculation
        obj.division(a, b);        // From Task31
    }
}
