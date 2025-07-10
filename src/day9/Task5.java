package day9;
public class Task5 {
    public static void main(String args[]) {
        try {
            int a[] = new int[2];
            int b = 10; // changed from 0 to 10
            int c = 1 / b;
            System.out.println("Access element three :" + a[3]); // this will cause exception
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException thrown  :" + e);
        }
        catch (Exception e) {
            System.out.println("Exception thrown  :" + e.getMessage());
        }
        System.out.println("Out of the block");
    }
}
