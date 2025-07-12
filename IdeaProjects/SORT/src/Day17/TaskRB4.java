package Day17;
class TaskRBT4 {
    static final boolean RED = true, BLACK = false;
    class Node {
        int data;
        boolean color = RED;
        Node left, right, parent;
        Node(int data) { this.data = data; }
    }
    Node root;
    public void insert(int data) {
        Node node = new Node(data);
        root = bstInsert(root, node);
        fixInsert(node);
    }
    private Node bstInsert(Node root, Node node) {
        if (root == null) return node;
        if (node.data < root.data) {
            root.left = bstInsert(root.left, node);
            root.left.parent = root;
        } else {
            root.right = bstInsert(root.right, node);
            root.right.parent = root;
        }
        return root;
    }
    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }
    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.right = x;
        x.parent = y;
    }
    private void fixInsert(Node node) {
        while (node != root && node.parent.color == RED) {
            Node parent = node.parent, grand = parent.parent;
            if (parent == grand.left) {
                Node uncle = grand.right;
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK; uncle.color = BLACK; grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.right) { node = parent; rotateLeft(node); }
                    parent.color = BLACK; grand.color = RED; rotateRight(grand);
                }
            } else {
                Node uncle = grand.left;
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK; uncle.color = BLACK; grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.left) { node = parent; rotateRight(node); }
                    parent.color = BLACK; grand.color = RED; rotateLeft(grand);
                }
            }
        }
        root.color = BLACK;
    }
    public void display(Node node) {
        if (node != null) {
            display(node.left);
            System.out.print(node.data + (node.color ? "R " : "B "));
            display(node.right);
        }
    }
    public static void main(String[] args) {
        TaskRBT4 t = new TaskRBT4();
        t.insert(10); t.insert(20); t.insert(30);
        t.insert(15); t.insert(5); t.insert(25);
        t.display(t.root);
    }
}

