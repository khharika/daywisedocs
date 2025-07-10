package day8;
public class Task22 {
    public static void main(String[] args) {
        // declares an Array of integers.
        int[] arr;

        // allocating memory for 5 integers.
        arr = new int[5];

        // initialize the elements of the array
        // first to last(fifth) element
        arr[0] = 10;
        arr[1] = 20;
        arr[2] = 30;
        arr[3] = 40;
        // arr[4] is implicitly initialized to 0 if not assigned explicitly.
        // The example output only shows up to index 3, so we'll match that.

        // Print the elements of the array to demonstrate
        System.out.println("Element at index 0 : " + arr[0]);
        System.out.println("Element at index 1 : " + arr[1]);
        System.out.println("Element at index 2 : " + arr[2]);
        System.out.println("Element at index 3 : " + arr[3]);
        // If you want to print arr[4], uncomment the line below:
        // System.out.println("Element at index 4 : " + arr[4]);
    }
}