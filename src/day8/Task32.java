package day8;
// Calculation.java (Your existing Calculation class)
class Calculation1 {
    public void addition(int a, int b) {
        System.out.println("Sum: " + (a + b));
    }
}
// Clock.java (Your existing Clock class)
class Clock {
    public void showTime() {
        System.out.println("Time: 12:00 PM");
    }
}
// Task32.java (The corrected file)
public class Task32 {

    // 1. Declare instances of the classes you want to use
    private Calculation calculator;
    private Clock clock;

    // 2. Initialize them in the constructor
    public Task32() {
        this.calculator = new Calculation();
        this.clock = new Clock();
    }
    // 3. Define the multiply method (if it's unique to Task32)
    public void multiply(int a, int b) {
        System.out.println("Product: " + (a * b));
    }

    // 4. Create "wrapper" methods to delegate calls to the contained objects
    public void addition(int a, int b) {
        calculator.addition(a, b); // Delegate to the calculator object
    }

    public void showTime() {
        clock.showTime(); // Delegate to the clock object
    }
    public static void main(String[] args) {
        Task32 obj = new Task32();
        obj.addition(10, 20);  // Now calls the delegated addition method
        obj.multiply(10, 5);   // Calls Task32's own multiply method
        obj.showTime();        // Now calls the delegated showTime method
    }
}