package day10;
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Code executed in a new thread via Runnable.");
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Code executed in a new thread via Thread extension.");
    }
}

public class TaskMT19 {
    public static void main(String[] args) {
        MyRunnable runnableInstance = new MyRunnable();
        MyThread threadInstance = new MyThread();

        Thread t1 = new Thread(runnableInstance); // Thread from Runnable

        t1.start();             // starts the Runnable thread
        threadInstance.start(); // starts the extended Thread
    }
}
