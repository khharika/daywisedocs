package Day21;
public class DependencyExample {
    public static void main(String[] args) {
        Vehicle myVehicle = new Vehicle();
        myVehicle.move();
    }
}

class Engine {
    public void start() {
        System.out.println("Engine starting");
    }
}

class Vehicle {
    public void move() {
        Engine engine = new Engine();
        engine.start();
        System.out.println("Vehicle is moving");
    }
}

