package Day20;
interface Shape {
    int area();
}

class Square implements Shape {
    int h;
    Square(int h) { this.h = h; }
    public int area() { return h * h; }
}

class Circle implements Shape {
    int r;
    Circle(int r) { this.r = r; }
    public int area() { return (int)(Math.PI * r * r); }
}

class AreaComparator {
    int compare(Shape s1, Shape s2) {
        return s1.area() - s2.area();
    }
}

public class Task05 {
    public static void main(String[] args) {
        Shape s1 = new Square(4);
        Shape s2 = new Circle(3);

        AreaComparator cmp = new AreaComparator();
        System.out.println(cmp.compare(s1, s2));
    }
}

