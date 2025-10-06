package Day21;
public class CompositionExample {
    public static void main(String[] args) {
        ComposedCar myCar = new ComposedCar(); // All wheels created with the car
        myCar.describe();
    }
}
class Wheel {
    public Wheel() {
        System.out.println("Wheel created");
    }
}
class ComposedCar { //
    private Wheel[] wheels;

    public ComposedCar() {
        wheels = new Wheel[] {
                new Wheel(), new Wheel(), new Wheel(), new Wheel()
        };
    }
    public void describe() {
        System.out.println("ComposedCar with " + wheels.length + " wheels created");
    }
}
