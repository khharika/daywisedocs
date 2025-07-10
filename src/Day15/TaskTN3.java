package Day15;
public class TaskTN3 {
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
                insert(root, value);
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

        // Preorder traversal
        public void preorderTraversal() {
            System.out.print("Preorder traversal: ");
            preorder(root);
            System.out.println();
        }
        private void preorder(Node node) {
            if (node != null) {
                System.out.print(node.data + " ");
                preorder(node.left);
                preorder(node.right);
            }
        }
    }
    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);
        tree.preorderTraversal();
    }
}
