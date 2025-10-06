package Day21;
interface I2A {
    void area();
    void perimeter();
}
interface I2V {
    void volume();
}
class C2 implements I2A {
    @Override
    public void area() {
        System.out.println("Area of circle");
    }
    @Override
    public void perimeter() {
        System.out.println("Perimeter of circle");
    }
}
class S2 implements I2A, I2V {
    @Override
    public void area() {
        System.out.println("Area of sphere");
    }
    @Override
    public void perimeter() {
        System.out.println("Perimeter of sphere (great circle)");
    }
    @Override
    public void volume() {
        System.out.println("Volume of sphere");
    }
}
public class ISPGood {
    public static void main(String[] args) {
        I2A shape1 = new C2();
        shape1.area();
        shape1.perimeter();

        I2A shape2 = new S2();
        shape2.area();
        shape2.perimeter();

        I2V shape3 = new S2();
        shape3.volume();
    }
}

