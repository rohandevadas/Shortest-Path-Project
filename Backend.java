import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;

public class Backend implements BackendInterface {
    private GraphADT<String, Double> graph;
    private List<String> locations;
    
    public Backend(GraphADT<String, Double> graph) {
        this.graph = graph;
        locations = new ArrayList<String>();
    }
    
    public void loadGraphData(String filename) throws IOException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        Pattern pattern = Pattern.compile("\"([a-zA-Z0-9 -.]+)\" -> \"([a-zA-Z0-9 -.]+)\" \\[seconds=([0-9.]+)\\]");
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);
            
            if (matcher.find()) {
                String startLocation = matcher.group(1);
                String endLocation = matcher.group(2);
                Double weight = Double.parseDouble(matcher.group(3));
                
                graph.insertNode(startLocation);
                graph.insertNode(endLocation);
                graph.insertEdge(startLocation, endLocation, weight);

                if (!locations.contains(startLocation)) {
                    locations.add(startLocation);
                }

                if (!locations.contains(endLocation)) {
                    locations.add(endLocation);
                }
            }
        }
        
        scanner.close();
    }
    
    public List<String> getListOfAllLocations() {
        return locations;
    }
    
    public List<String> findShortestPath(String startLocation, String endLocation) {
        return graph.shortestPathData(startLocation, endLocation);
    }
    
    public List<Double> getTravelTimesOnPath(String startLocation, String endLocation) {
        List <Double> times = new ArrayList<Double>();

        List<String> path = graph.shortestPathData(startLocation, endLocation);

        for (int i = 0; i < path.size() - 1; i++) {
            times.add(graph.getEdge(path.get(i), path.get(i + 1)));
        }

        return times;
    }
    
    public List<String> findShortestPathVia(String startLocation, String via, String endLocation) {
        List<String> path1 = graph.shortestPathData(startLocation, via);
        List<String> path2 = graph.shortestPathData(via, endLocation);

        List<String> path = new ArrayList<String>();

        // remove duplicate
        for (int i = 0; i < path1.size() - 1; i++) {
            path.add(path1.get(i));
        }

        for (int i = 0; i < path2.size(); i++) {
            path.add(path2.get(i));
        }

        return path;
    }
    
    public List<Double> getTravelTimesOnPathVia(String startLocation, String via, String endLocation) {
        List<Double> times = new ArrayList<Double>();

        List<String> path1 = graph.shortestPathData(startLocation, via);
        List<String> path2 = graph.shortestPathData(via, endLocation);
        List<String> path2copy = new ArrayList<String>();

        for (int i = 0; i < path2.size(); i++) {
            path2copy.add(path2.get(i));
        }

        path2copy.remove(0);
        path1.addAll(path2copy);

        System.out.println(path1);

        for (int i = 0; i < path1.size() - 1; i++) {
            times.add(graph.getEdge(path1.get(i), path1.get(i + 1)));
        }

        return times;
    }
}
