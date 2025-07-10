package day10;
public class TaskMT20 {

    public static void main(String[] args) {
        method1();
    }

    public static void method1() {
        method2();
    }

    public static void method2() {
        method3();
    }

    public static void method3() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        System.out.println("Thread Stack Trace:");
        for (StackTraceElement element : stackTrace) {
            System.out.println("  Class: " + element.getClassName() +
                    ", Method: " + element.getMethodName() +
                    ", Line: " + element.getLineNumber());
        }
    }
}
