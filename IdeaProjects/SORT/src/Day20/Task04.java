package Day20;
class Employee {
    String name, email;
    double salary;

    Employee(String name, String email, double salary) {
        this.name = name;
        this.email = email;
        this.salary = salary;
    }
}

class ReportGenerator {
    void generate(Employee e) {
        System.out.println("PDF for: " + e.name);
    }
}

class EmailSender {
    void send(String to, String msg) {
        System.out.println("Email to: " + to + " | Msg: " + msg);
    }
}

public class Task04 {
    public static void main(String[] args) {
        Employee e = new Employee("Harika", "harika@mail.com", 70000);
        new ReportGenerator().generate(e);
        new EmailSender().send(e.email, "Report generated");
    }
}
