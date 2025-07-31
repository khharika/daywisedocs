package Day21;
public class AggregationExample {
    public static void main(String[] args) {
        Driver driver = new Driver("John");
        Car car = new Car(driver); // Aggregation
        car.display();
    }
}
class Driver {
    private String name;
    public Driver(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
class Car {
    private Driver driver; // Aggregation: Car "has-a" Driver
    public Car(Driver driver) {
        this.driver = driver;
    }
    public void display() {
        System.out.println("Car is being driven by " + driver.getName());
    }
}

