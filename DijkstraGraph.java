
import java.util.PriorityQueue;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
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
        super(new PlaceholderMap<>());
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
    	PlaceholderMap<NodeType, EdgeType> visitedNodes = new PlaceholderMap<NodeType, EdgeType>();
        LinkedList<NodeType> path = new LinkedList<NodeType>();
    	PriorityQueue<SearchNode> visitingNodes = new PriorityQueue<SearchNode>();
    	//add start to visited map
    	visitedNodes.put(start, null);
    	SearchNode searchStart = new SearchNode(nodes.get(start), 0, null); 
    	for(Edge i: nodes.get(start).edgesLeaving) { 
    		visitingNodes.add(new SearchNode(i.successor, (double) i.data + searchStart.cost, searchStart));
    	}
    	 SearchNode head = null;
    	while(!visitingNodes.isEmpty()) {
    		//remove the head of p.q.
    		head = visitingNodes.remove();
    		//if reached the end node, add head to visited and break loop.
    		if(head.node.data.equals(end)){
    			visitedNodes.put(head.node.data, this.getEdge(head.predecessor.node.data, head.node.data));
    			
    			path.add(head.node.data);
    			break; 
    		}
    		//if head has not been visited, visit it and add its edges to p.q.
    		if(!visitedNodes.containsKey(head.node.data)) {
    			visitedNodes.put(head.node.data, this.getEdge(head.predecessor.node.data, head.node.data));
    			path.add(head.node.data);
    			
    			for(Edge i: head.node.edgesLeaving) { 
    	    		visitingNodes.add(new SearchNode(i.successor, (Integer) i.data + head.cost, head));
    	    	}
    		}
    	}
    	if(!visitedNodes.containsKey(end)) {
    		throw new NoSuchElementException();
    	}
    	
    	return head;
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
    	SearchNode endNode = computeShortestPath(start, end);
        
    	LinkedList<NodeType> path = new LinkedList<NodeType>();
    	while(endNode != null) {
    		path.add(endNode.node.data);
    		endNode = endNode.predecessor;
    	}
    	LinkedList<NodeType> copyPath = new LinkedList<NodeType>();
    	for(int i = path.size()-1; i>=0; i--) {
    		copyPath.add(path.get(i));
    	}
        return copyPath; 
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
    	SearchNode endNode = computeShortestPath(start, end);
    	double cost = 0;
    	
    	while(endNode != null) {
    		cost+=endNode.cost;
    		endNode = endNode.predecessor;
    	}
        return cost;
    	
        
    }

    

    @Test
    /**
     * This test method tests a map against the shortestPathCost
     * and shortestPathData
     */
    public void test1(){
	DijkstraGraph<String, Integer> dGraph = new DijkstraGraph<String, Integer>();
	
	dGraph.insertNode("A");
	dGraph.insertNode("B");
	dGraph.insertNode("C");
	dGraph.insertNode("D");
	dGraph.insertNode("E");
	
	//insert A's edges.
	dGraph.insertEdge("A", "B", 15);
	dGraph.insertEdge("A", "C", 1);
	dGraph.insertEdge("A", "D", 4);
	//insert B's Edges.
	dGraph.insertEdge("B", "A", 15);
	dGraph.insertEdge("B", "D", 2);
	dGraph.insertEdge("B", "E", 1);
	//insert C's edges.
	dGraph.insertEdge("C", "A", 1);
	dGraph.insertEdge("C", "E", 10);
	//insert D's edges.
	dGraph.insertEdge("D", "A", 4);
	dGraph.insertEdge("D", "B", 2);
	dGraph.insertEdge("D", "E", 10);
	//insert E's edges.
	dGraph.insertEdge("E", "D", 10);
	dGraph.insertEdge("E", "C", 1);
	//check if cost is correct.
	assertEquals(dGraph.shortestPathCost("A", "E"), 7);
	List<String> expected = new LinkedList<String>();
	expected.add("A"); 
	expected.add("D");
	expected.add("B");
	expected.add("E");
	//check if path is correct.
	List<String> result = dGraph.shortestPathData("A", "E");
	for(int i = 0; i<result.size(); i++) {
		assertEquals(result.get(i), expected.get(i));
	}
    }
    
    @Test
    /**
     * This test method tests a map against the shortestPathCost
     * and shortestPathData with different start and end points.
     */
    
    public void test2() {
    	DijkstraGraph<String, Integer> dGraph = new DijkstraGraph<String, Integer>();
    	
    	dGraph.insertNode("A");
    	dGraph.insertNode("B");
    	dGraph.insertNode("C");
    	dGraph.insertNode("D");
    	dGraph.insertNode("E");
    	
    	//insert A's edges.
    	dGraph.insertEdge("A", "B", 15);
    	dGraph.insertEdge("A", "C", 1);
    	dGraph.insertEdge("A", "D", 4);
    	//insert B's Edges.
    	dGraph.insertEdge("B", "A", 15);
    	dGraph.insertEdge("B", "D", 2);
    	dGraph.insertEdge("B", "E", 1);
    	//insert C's edges.
    	dGraph.insertEdge("C", "A", 1);
    	dGraph.insertEdge("C", "E", 10);
    	//insert D's edges.
    	dGraph.insertEdge("D", "A", 4);
    	dGraph.insertEdge("D", "B", 2);
    	dGraph.insertEdge("D", "E", 10);
    	//insert E's edges.
    	dGraph.insertEdge("E", "D", 10);
    	dGraph.insertEdge("E", "C", 1);
    	//checks if cost is correct.
    	assertEquals(dGraph.shortestPathCost("C", "E"), 8);
    	List<String> expected = new LinkedList<String>();
    	expected.add("C"); 
    	expected.add("A"); 
    	expected.add("D");
    	expected.add("B");
    	expected.add("E");
    	//checks if path is correct.
    	List<String> result = dGraph.shortestPathData("C", "E");
    	for(int i = 0; i<result.size(); i++) {
    		assertEquals(result.get(i), expected.get(i));
    	}
    }
    
    @Test
    /**
     * This test method tests a map against the shortestPathCost
     * and shortestPathData when there is no connection between nodes.
     */
    public void test3(){
    	DijkstraGraph<String, Integer> dGraph = new DijkstraGraph<String, Integer>();
    	
    	dGraph.insertNode("A");
    	dGraph.insertNode("B");
    	dGraph.insertNode("C");
    	dGraph.insertNode("D");
    	dGraph.insertNode("E");
    	
    	//insert A's edges.
    	dGraph.insertEdge("A", "B", 15);
    	dGraph.insertEdge("A", "D", 4);
    	//insert B's Edges.
    	dGraph.insertEdge("B", "A", 15);
    	dGraph.insertEdge("B", "D", 2);
    	dGraph.insertEdge("B", "E", 1);
    	//insert C's edges.
    	dGraph.insertEdge("C", "A", 1);
    	dGraph.insertEdge("C", "E", 10);
    	//insert D's edges.
    	dGraph.insertEdge("D", "A", 4);
    	dGraph.insertEdge("D", "B", 2);
    	dGraph.insertEdge("D", "E", 10);
    	//insert E's edges.
    	dGraph.insertEdge("E", "D", 10);
    	
    	try {
    		dGraph.shortestPathData("D", "C");
    		assertEquals(0,1, "no exception was called.");
    	} catch(NoSuchElementException e) {
    		assertEquals(1,1, "correct exception called.");
    	}
    	
    }
    
    public static void main(String[] args) {
    	DijkstraGraph<String, Integer> dGraph = new DijkstraGraph<String, Integer>();
    	
    	dGraph.insertNode("A");
    	dGraph.insertNode("B");
    	dGraph.insertNode("C");
    	dGraph.insertNode("D");
    	dGraph.insertNode("E");
    	
    	//insert A's edges.
    	dGraph.insertEdge("A", "B", 15);
    	dGraph.insertEdge("A", "C", 1);
    	dGraph.insertEdge("A", "D", 4);
    	//insert B's Edges.
    	dGraph.insertEdge("B", "A", 15);
    	dGraph.insertEdge("B", "D", 2);
    	dGraph.insertEdge("B", "E", 1);
    	//insert C's edges.
    	dGraph.insertEdge("C", "A", 1);
    	dGraph.insertEdge("C", "E", 10);
    	//insert D's edges.
    	dGraph.insertEdge("D", "A", 4);
    	dGraph.insertEdge("D", "B", 2);
    	dGraph.insertEdge("D", "E", 10);
    	//insert E's edges.
    	dGraph.insertEdge("E", "D", 10);
    	dGraph.insertEdge("E", "C", 1);
    	
    	
    	List<String> expected = new LinkedList<String>();
    	expected.add("A"); 
    	expected.add("D");
    	expected.add("B");
    	expected.add("E");
    	//check if path is correct.
    	List<String> result = dGraph.shortestPathData("A", "E");
    	
    	for(int i = 0; i<result.size(); i++) {
    		if(result.get(i).equals(expected.get(i))) {
    			System.out.println(result.get(i));
    		}
    	}
    }
    
}
