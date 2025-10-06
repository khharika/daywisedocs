package Day23;

public class SingletonDemo {
    private static SingletonDemo instance;

    private SingletonDemo() {
        System.out.println("initiating the singleton");
    }

    public static SingletonDemo getInstance() {
        if (instance == null) {
            instance = new SingletonDemo();
            System.out.println("in get instance");
        }
        return instance;
    }

    public void doHere() {
        System.out.println("doing here some thing");
    }
}