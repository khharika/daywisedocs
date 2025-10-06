package Day21;
abstract class F {
    abstract void fly();
}
abstract class NF {
    abstract void info();
}
class Eg extends F {
    @Override
    public void fly() {
        System.out.println("Eg flies high");
    }
}
class Os extends NF {
    @Override
    public void info() {
        System.out.println("Os lays big eggs");
    }
}
public class LSPGood {
    public static void main(String[] args) {
        F f = new Eg();
        f.fly();
        NF nf = new Os();
        nf.info();
    }
}

