package Day24;
public class MobileStore {

    private MobileStore() {
        System.out.println("Hello, welcome to the world of Mobile");
    }

    public static Mobile getMobile(String brand, String model) {
        if ("Apple".equalsIgnoreCase(brand)) {
            System.out.println("Here are your Apple models:");
            return Apple.getMobile(model);
        }
        // Future expansion for other brands like Samsung can go here.
        // else if ("Samsung".equalsIgnoreCase(brand)) {
        //     return Samsung.getMobile(model);
        // }

        return new NoMobile(); // unknown brand
    }
}
