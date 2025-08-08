package Day24;
public class ClientBuildMethodDP {
    public static void main(String[] args) {
        LaptopBuilder lbobj = new LaptopConcreteBuilder();
        LaptopDirector dir = new LaptopDirector(lbobj);
        Laptop lobj = dir.constructLaptop();

        System.out.println(lobj);
    }
}