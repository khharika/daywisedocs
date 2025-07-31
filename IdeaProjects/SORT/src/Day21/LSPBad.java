package Day21;
abstract class B {
    abstract void fly();
}
class E extends B {
    @Override
    public void fly() {
        System.out.println("E flies");
    }
}
class O extends B {
    @Override
    public void fly() {
        System.out.println("O can't fly but lays big eggs");
    }
}
public class LSPBad {
    public static void main(String[] args) {
        B b1 = new E();
        b1.fly();
        B b2 = new O();
        b2.fly();  // ❌ violates LSP: O is not truly a flying bird
    }
}

