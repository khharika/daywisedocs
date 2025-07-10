package Day15;
public class TaskTN2 {
    static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }
    static class BinarySearchTree {
        Node root;
        public void insert(int value) {
            if (root == null) {
                root = new Node(value);
                System.out.println(value + " inserted as root");
            } else {
                insert(root, value); // call overloaded insert
            }
        }
        private void insert(Node current, int value) {
            if (value < current.data) {
                if (current.left == null) {
                    current.left = new Node(value);
                    System.out.println(value + " inserted to left of " + current.data);
                } else {
                    insert(current.left, value);
                }
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    System.out.println(value + " inserted to right of " + current.data);
                } else {
                    insert(current.right, value);
                }
            }
        }
    }
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        tree.insert(50); // Empty tree insert
        tree.insert(30); // Non-empty insert
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);
    }
}
