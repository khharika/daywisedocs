package Day15;

public class TaskTN1 {
    int data;
    TaskTN1 left;
    TaskTN1 right;

    // Constructor
    TaskTN1(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
    public static void main(String[] args) {
        TaskTN1 root = new TaskTN1(10);
        root.left = new TaskTN1(5);
        root.right = new TaskTN1(15);
        System.out.println("Root: " + root.data);
        System.out.println("Left Child: " + root.left.data);
        System.out.println("Right Child: " + root.right.data);
    }
}
