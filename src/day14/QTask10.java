package day14;
public class QTask10 {
    static class MyQueue {
        int[] q;
        int front = 0, rear = -1, size;
        MyQueue(int size) {
            this.size = size;
            q = new int[size];
        }
        boolean isEmpty() {
            return front > rear;
        }
        boolean isFull() {
            return rear == size - 1;
        }
        void enqueue(int data) {
            if (isFull()) System.out.println("Queue full");
            else q[++rear] = data;
        }
        void dequeue() {
            if (isEmpty()) System.out.println("Queue empty");
            else System.out.println("Dequeued: " + q[front++]);
        }
        void peek() {
            if (isEmpty()) System.out.println("Queue empty");
            else System.out.println("Front: " + q[front]);
        }
        void display() {
            if (isEmpty()) System.out.println("Queue empty");
            else {
                System.out.print("Queue: ");
                for (int i = front; i <= rear; i++)
                    System.out.print(q[i] + " ");
                System.out.println();
            }
        }
    }
    public static void main(String[] args) {
        MyQueue q = new MyQueue(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.display();
        q.peek();
        q.dequeue();
        q.display();
        System.out.println("Empty? " + q.isEmpty());
        System.out.println("Full? " + q.isFull());
        q.enqueue(40);
        q.enqueue(50);
        q.enqueue(60);
        q.enqueue(70);
        q.display();
    }
}
