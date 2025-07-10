package day8;

public class Task35 {
    void add(char x, char y) {
        System.out.println("x: " + x + ", y: " + y);
    }

    void add(int x, int y) {
        System.out.println("x: " + x + ", y: " + y);
    }

    public static void main(String[] args) {
        Task35 obj = new Task35();
        obj.add('d', 'a');
        obj.add(100, 100);
    }
}
