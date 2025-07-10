package Day15;
public class TaskTN6 {
    static class Node {
        int data;
        Node left, right;
        Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }
    static class BinarySearchTree {
        Node root;
        public void insert(int value) {
            root = insertRecursive(root, value);
        }
        private Node insertRecursive(Node node, int value) {
            if (node == null) {
                return new Node(value);
            }
            if (value < node.data) {
                node.left = insertRecursive(node.left, value);
            } else if (value > node.data) {
                node.right = insertRecursive(node.right, value);
            }
            return node;
        }
        public boolean search(int value) {
            return searchRecursive(root, value);
        }
        private boolean searchRecursive(Node node, int value) {
            if (node == null) return false;
            if (node.data == value) return true;
            if (value < node.data) return searchRecursive(node.left, value);
            else return searchRecursive(node.right, value);
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
        int value1 = 40;
        int value2 = 100;
        System.out.println("Search " + value1 + ": " + (tree.search(value1) ? "Found" : "Not Found"));
        System.out.println("Search " + value2 + ": " + (tree.search(value2) ? "Found" : "Not Found"));
    }
}

