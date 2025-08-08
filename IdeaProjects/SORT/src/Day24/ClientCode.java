package Day24;
public class ClientCode {
    public static void main(String[] args) {
        Colors BlackPrototypeObj = new BlackConcreteprototype("Black Color");
        Colors WhitePrototypeObj = new WhiteConcretePrototype("white color");
        Colors clonedBlackObj = BlackPrototypeObj.clone();
        Colors clonedWhiteObj = WhitePrototypeObj.clone();
        clonedBlackObj.setName("dark color");
        clonedWhiteObj.setName("light color");
        System.out.println("black color is " + clonedBlackObj.getName());
        System.out.println("while color is "+ clonedWhiteObj.getName());
    }
}
