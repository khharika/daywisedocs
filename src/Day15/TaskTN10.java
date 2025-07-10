package Day15;
public class TaskTN10 {

    // Outer Graph class
    static class Graph {
        int vertex;
        int edge;

        // Inner Edge class
        static class Edge {
            int start;
            int end;

            Edge(int start, int end) {
                this.start = start;
                this.end = end;
            }
        }

        // Constructor
        Graph(int vertex, int edge) {
            this.vertex = vertex;
            this.edge = edge;
        }

        // Method to display edges
        public void displayEdges(Edge[] edges) {
            System.out.println("Number of vertices: " + vertex);
            System.out.println("Number of edges: " + edge);
            System.out.println("Graph edges:");
            for (int i = 0; i < edge; i++) {
                System.out.println(edges[i].start + " - " + edges[i].end);
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        int v = 5;
        int e = 8;

        Graph.Edge[] edges = new Graph.Edge[e];

        // Add edges
        edges[0] = new Graph.Edge(1, 2);
        edges[1] = new Graph.Edge(1, 3);
        edges[2] = new Graph.Edge(1, 4);
        edges[3] = new Graph.Edge(2, 4);
        edges[4] = new Graph.Edge(2, 5);
        edges[5] = new Graph.Edge(3, 4);
        edges[6] = new Graph.Edge(3, 5);
        edges[7] = new Graph.Edge(4, 5); // newly added 8th edge

        // Create graph and display edges
        Graph g = new Graph(v, e);
        g.displayEdges(edges);
    }
}
