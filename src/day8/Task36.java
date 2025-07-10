package day8;

public class Task36 {
    void add(int x, float y) {
        System.out.println("x: " + x + ", y: " + y);
    }

    void add(float x, int y) {
        System.out.println("x: " + x + ", y: " + y);
    }

    public static void main(String[] args) {
        Task36 obj = new Task36();
        obj.add(10.50f, 60);    // float, int
        obj.add(100, 80.80f);   // int, float
    }
}
