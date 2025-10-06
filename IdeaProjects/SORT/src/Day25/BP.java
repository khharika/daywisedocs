package Day25;
public class BP {
    public static void main(String[] args) {
        System.out.println("Bridge Method Design Pattern - Structural DP!");

        ExcalidrawAPI obj1 = new DrawingFrame();
        ExcalidrawAPI obj2 = new DrawingPicture();

        Shape square1 = new Square(5, obj1);
        square1.draw();

        Shape square2 = new Square(7, obj2);
        square2.draw();
    }
}

