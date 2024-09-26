package application;
import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new HashtableMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        if (start == null || end == null) {
            throw new NoSuchElementException("Start and end nodes must not be null");
        }

        if (!nodes.containsKey(start) || !nodes.containsKey(end)) {
            throw new NoSuchElementException("Start and end nodes must be in the graph");
        }

        PriorityQueue<SearchNode> toVisit = new PriorityQueue<>();
        HashtableMap<NodeType, Boolean> visited = new HashtableMap<>();

        Node startNode = nodes.get(start);
        toVisit.add(new SearchNode(startNode, 0, null)); //cost is 0 since already there

        // While there are nodes we have not visited yet (and can visit from cur node)
        while (!toVisit.isEmpty()) {
            SearchNode current = toVisit.poll(); //get node off top of queue

            if (visited.containsKey(current.node.data)) {
                continue;
            }

            visited.put(current.node.data, true);

            if (current.node.data.equals(end)) {
                return current;
            }

            for (Edge edge : current.node.edgesLeaving) {
                Node neighbor = edge.successor;
                double cost = current.cost + edge.data.doubleValue();

                //Add a potential new path to the priority queue
                toVisit.add(new SearchNode(neighbor, cost, current));
            }
        }

        // If here then there is no path from start to end
        throw new NoSuchElementException("No path from start to end");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        List <NodeType> path = new LinkedList<>();

        try {
            SearchNode current = computeShortestPath(start, end);

            while (current != null) {
                path.add(0, current.node.data);
                current = current.predecessor;
            }
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No path from start to end.");
        }

        return path;
	}

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        double cost = 0.0;

        try {
            cost = computeShortestPath(start, end).cost;
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No path from start to end.");
        }

        return cost;
    }

    
}
