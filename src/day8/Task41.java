package day8;
// Interface Declared
interface testInterface {
    // public, static and final
    final int tax = 10;

    // public and abstract
    void display();
}

// Class implementing interface
class TestClass implements testInterface {
    public void display() {
        System.out.println("Myclass");
    }
}
// Driver Code Starts
public class Task41 {
    public static void main(String[] args) {
        TestClass t = new TestClass();
        t.display();

        // Accessing interface constant
        System.out.println(testInterface.tax);
    }
}
