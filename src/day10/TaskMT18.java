package day10;
public class    TaskMT18 extends Thread {
    public void run() {

        System.out.println("thread started.");

    }

    public static void main(String args[]) {
        TaskMT18 th1 = new TaskMT18();
        th1.start();
    }
}


