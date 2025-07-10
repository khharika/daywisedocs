package day9;
public class Task4 {
    public static void main(String[] args) {
        try {
            int[] myNumbers = {1, 2, 3};
            System.out.println(myNumbers[1]); // Accessing index 1 (value = 2)
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        } finally {
            System.out.println("I'm from finally block.");
        }
    }
}
