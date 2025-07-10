package day8;
class Employee1 {
    private int pwd;
    protected int salary;
    public int empid;

    Employee1(int pwd, int salary, int empid) {
        this.pwd = pwd;
        this.salary = salary;
        this.empid = empid;
    }

    public int getPwd() {
        return pwd;
    }

    public void setPwd(int pwd) {
        this.pwd = pwd;
    }
}

class Hr extends Employee1 {
    Hr() {
        super(1254, 50000, 10001);
    }

    public void displayDetails() {
        System.out.println("Inside Hr class:");
        System.out.println("Password: " + getPwd());
        System.out.println("Salary: " + salary);
        System.out.println("EmpID: " + empid);
    }
}

public class Task37 {
    public static void main(String[] args) {
        Hr obj = new Hr();

        System.out.println("Inside Task37 class:");
        System.out.println("EmpID: " + obj.empid);
        System.out.println("Salary: " + obj.salary);
        System.out.println("Password: " + obj.getPwd());

        obj.displayDetails();
    }
}
