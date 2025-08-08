package Day24;

public class ClientAbstractFactory {
    public static void main(String[] args) {
        Mobile mObj = MobileStore.getMobile("Apple", "iphone16");
        mObj.getDesc();

        System.out.println("Thank you for visiting Mobile Store!");
    }
}
