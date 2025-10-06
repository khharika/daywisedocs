package Day21;
interface I1 {
    void area();
    void volume();
}
class C1 implements I1 {
    @Override
    public void area() {
        System.out.println("Area of circle");
    }
    @Override
    public void volume() {
        System.out.println("Volume not applicable"); // Dummy
    }
}
class S1 implements I1 {
    @Override
    public void area() {
        System.out.println("Area of sphere");
    }
    @Override
    public void volume() {
        System.out.println("Volume of sphere");
    }
}
public class ISPBad {
    public static void main(String[] args) {
        I1 shape1 = new C1();
        shape1.area();
        shape1.volume(); // ❌ Circle shouldn't be doing this
        I1 shape2 = new S1();
        shape2.area();
        shape2.volume();
    }
}

