package day8;
abstract class Employee {
    private String name;
    private String address;
    private int number;

    public Employee(String name, String address, int number) {
        System.out.println("Constructing an Employee");
        this.name = name;
        this.address = address;
        this.number = number;
    }

    public double computePay() {
        System.out.println("Inside Employee computePay");
        return 0.0;
    }

    public void mailCheck() {
        System.out.println("Mailing a check to " + this.name + ", " + this.address);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String newAddress) {
        address = newAddress;
    }

    public int getNumber() {
        return number;
    }

    public String toString() {
        return name + " " + address + " " + number;
    }
}

class SalaryEmployee extends Employee {
    public SalaryEmployee(String name, String address, int number) {
        super(name, address, number);
    }

    @Override
    public double computePay() {
        System.out.println("Inside SalaryEmployee computePay");
        return 50000.0;
    }
}

public class Task38 {
    public static void main(String[] args) {
        Employee e = new SalaryEmployee("George W.", "Houston, TX", 43);

        System.out.println("\nCall mailCheck using Employee reference:");
        e.mailCheck();

        System.out.println("\nCall computePay:");
        System.out.println("Pay: $" + e.computePay());
    }
}
