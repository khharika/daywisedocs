abstract class Gadgets {
    abstract void turnOn();
    abstract void turnOff();
}
class TVRemote extends Gadgets {
    @Override
    void turnOn() {
        System.out.println("TV is turned ON.");
    }

    @Override
    void turnOff() {
        System.out.println("TV is turned OFF.");
    }
}
class ACRemote extends Gadgets {
    @Override
    void turnOn() {
        System.out.println("AC is turned ON.");
    }
    @Override
    void turnOff() {
        System.out.println("AC is turned OFF.");
    }
}
class FanRemote extends Gadgets {
    @Override
    void turnOn() {
        System.out.println("Fan is turned ON.");
    }
    @Override
    void turnOff() {
        System.out.println("Fan is turned OFF.");
    }
}
class CoolerRemote extends Gadgets {
    @Override
    void turnOn() {
        System.out.println("Cooler is turned ON.");
    }
    @Override
    void turnOff() {
        System.out.println("Cooler is turned OFF.");
    }
}
public class Task40 {
    public static void main(String[] args) {
        Gadgets tv = new TVRemote();
        Gadgets ac = new ACRemote();
        Gadgets fan = new FanRemote();
        Gadgets cooler = new CoolerRemote();
        tv.turnOn();
        tv.turnOff();
        ac.turnOn();
        ac.turnOff();
        fan.turnOn();
        fan.turnOff();
        cooler.turnOn();
        cooler.turnOff();
    }
}
