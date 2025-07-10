package day8;
class Superclass {
    int var;
    Superclass(int var) {
        this.var = var;
    }
    public void getVar() {
        System.out.println("var value in super class is " + var);
    }
}

public class Task33_1 extends Superclass {
    Task33_1(int var) {
        super(var);
    }
    public static void main(String[] args) {
        Superclass sobj = new Superclass(100);
        sobj.getVar();
        Task33_1 tobj = new Task33_1(200);
        tobj.getVar();
    }
}
