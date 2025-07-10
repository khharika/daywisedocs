package day8;

class Student1 {
    public String name;

    Student1(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

public class Task24 {
    public static void main(String[] args) {
        Student1[] myStudents = new Student1[5];

        myStudents[0] = new Student1("sanvi");
        myStudents[1] = new Student1("Dharma");
        myStudents[2] = new Student1("sanvi");
        myStudents[3] = new Student1("Rupa");
        myStudents[4] = new Student1("Ajay");

        for (int i = 0; i < myStudents.length; i++) {
            System.out.println(myStudents[i]);
        }
    }
}