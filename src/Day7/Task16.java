package Enumerations;
import java.util.Enumeration;
import java.util.Vector;
enum Color {
    RED, GREEN, BLUE;
}
public class Task16 {
    public static void main(String[] args) {
        // Using enum
        Color c1 = Color.GREEN;
        System.out.println("Enum value: " + c1);
        Vector<String> colors = new Vector<>();
        colors.add("Red");
        colors.add("Green");
        colors.add("Blue");
        Enumeration<String> e = colors.elements();
        System.out.println("Vector elements using Enumeration:");
        while (e.hasMoreElements()) {
            System.out.println(e.nextElement());
        }
    }
}
