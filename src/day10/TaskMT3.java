package day10;
class ThreadDemo extends Thread {
    private Thread t;
    private String threadName;

    ThreadDemo(String name) {
        this.threadName = name;
        System.out.println("Creating " + this.threadName);
    }

    public void run() {
        System.out.println("Running " + this.threadName);

        try {
            for (int i = 4; i > 0; --i) {
                System.out.println("Thread: " + this.threadName + ", " + i);
                Thread.sleep(50); // sleep for 50 milliseconds
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + this.threadName + " interrupted.");
        }

        System.out.println("Thread " + this.threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + this.threadName);
        if (this.t == null) {
            this.t = new Thread(this, this.threadName);
            this.t.start();
        }
    }
}

public class TaskMT3 {
    public static void main(String[] args) {
        ThreadDemo t1 = new ThreadDemo("Thread-1");
        t1.start();

        ThreadDemo t2 = new ThreadDemo("Thread-2");
        t2.start();
    }
}
