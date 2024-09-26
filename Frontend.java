import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.*;

 
public class Frontend extends Application{
  private boolean travelTimes = false;
  private boolean via = false; 
  private TextField viaText;
  private Label info;
  private Label path;
  private List<String> shortestPath;
  private List<Double> timesArray;
  private static BackendInterface back;

  
  public static void setBackend(BackendInterface back) {
    Frontend.back = back;
    try {
		back.loadGraphData("campus.dot");
	} catch (Exception e) {
		System.out.println("error, couldnt find file:(((");
	}
  }
  
  public void start(Stage stage) {
	
    Pane root = new Pane();

    createAllControls(root);

    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle("P2: Prototype");
    stage.show();
    
    
  }  
  /**
   * Creates all controls in the GUI.
   * @param parent the parent pane that contains all controls
   */
    public void createAllControls(Pane parent){
	createShortestPathControls(parent);
	createPathListDisplay(parent);
	createAdditionalFeatureControls(parent);
	createAboutAndQuitControls(parent);
    }
    
  /**
   * Creates the controls for the shortest path search.
   * @param parent the parent pane that contains all controls
   */
    public void createShortestPathControls(Pane parent){
    
	Label src = new Label("Path Start Selector: ");
    src.setLayoutX(32);
    src.setLayoutY(16);
    src.setId("src");
    parent.getChildren().add(src);
    //1. TEXTBOX START
    TextField start = new TextField("");
    start.setLayoutX(140);
    start.setLayoutY(13);
    start.setId("start");
    parent.getChildren().add(start);
    Label dst = new Label("Path End Selector: ");
    dst.setLayoutX(32);
    dst.setLayoutY(48);
    parent.getChildren().add(dst);
    //3. TEXTBOX END
    TextField end = new TextField("");
    end.setLayoutX(140);
    end.setLayoutY(45);
    end.setId("end");
    parent.getChildren().add(end);  
    //4. SUBMIT BUTTON
    Button find = new Button("Submit/Find");
    find.setLayoutX(32);
    find.setLayoutY(80);
    find.setId("submit");
    
    parent.getChildren().add(find);
    
    find.setOnAction(e -> {
    	
    	if(via&&viaText!=null) {
    		shortestPath = back.findShortestPathVia(start.getText(),viaText.getText() ,end.getText());
    		timesArray = back.getTravelTimesOnPathVia(start.getText(),viaText.getText() ,end.getText());
    		createPathListDisplay(parent);
    	} else {
    		
    		shortestPath = back.findShortestPath(start.getText(), end.getText());
    		timesArray = back.getTravelTimesOnPath(start.getText(), end.getText());
    		createPathListDisplay(parent);
    	}
    });
    
    }

  /**
   * Creates the controls for displaying the shortest path returned by the search.
   * @param the parent pane that contains all controls
   */
    public void createPathListDisplay(Pane parent){
    	String result = "";
    	if(shortestPath!=null) {
    		if(!travelTimes) {
    			if(!via) {
    				result = "Shortest Path: " + "\n";
    			} else {
    			result = "Shortest Path with : " + viaText.getText() + " as a stop on the way"+ "\n";
    			}
    			for(int i= 0; i<shortestPath.size(); i++) {
    				result += "   " + shortestPath.get(i) + "\n";
    			} 
    	} else { 
    		//System.out.println("hi");
    		if(!via) {
    			result = "Shortest Path: " + "\n";
    		} else {
    			result = "Shortest Path with " + viaText.getText() + " as a stop on the way: "+ "\n";
    		}
    		result += "   "+shortestPath.get(0) + "\n"; 
    		for(int i= 1; i<shortestPath.size(); i++) {
    				result += "   ->( "+ timesArray.get(i-1)+" ) "+ shortestPath.get(i) + "\n";
    		}
    		
    	  }
    	} 
    	if(path !=null) {
    		parent.getChildren().remove(path);
    	} 
    	path = new Label(result);
		path.setLayoutX(32);
		path.setLayoutY(112);
		path.setId("path");
		parent.getChildren().add(path); 
    	
    }

  /**
   * Creates controls for the two features in addition to the shortest path search.
   * @param parent parent pane that contains all controls
   */
    public void createAdditionalFeatureControls(Pane parent){
    	this.createTravelTimesBox(parent);
        this.createOptionalLocationControls(parent);
    }

  /**
   * Creates the check box to add travel times in the result display.
   * @param parent parent pane that contains all controls
   */
    public void createTravelTimesBox(Pane parent){
    	 CheckBox travelTimesBox = new CheckBox("Show Walking Times");
    	    travelTimesBox.setLayoutX(200);
    	    travelTimesBox.setLayoutY(85);
    	    travelTimesBox.setId("travel");
    	    parent.getChildren().add(travelTimesBox);
    	    
    	    travelTimesBox.setOnAction(e -> {
    	    	travelTimes = !travelTimes;
    	    	
    	    });
    }

  /**
   * Creates controls to allow users to add a third location for the path to go through.
   * @param parent parent pane that contains all controls
   */
    public void createOptionalLocationControls(Pane parent){
    	Label locationSelector = new Label("Via Location (optional): ");
        locationSelector.setLayoutX(500);
        locationSelector.setLayoutY(16);
        parent.getChildren().add(locationSelector);
        
        viaText = new TextField("");
        viaText.setLayoutX(625);
        viaText.setLayoutY(13); 
        parent.getChildren().add(viaText); 
        
        CheckBox useVia = new CheckBox("Use Above Location in Path");
        useVia.setLayoutX(500);
        useVia.setLayoutY(48);
        useVia.setOnAction(e -> via = !via);
        useVia.setId("via");
        parent.getChildren().add(useVia);
        
    }

  /**
   * Creates an about and quit button.
   * @param parent parent pane that contains all controls
   */
    public void createAboutAndQuitControls(Pane parent){
    	Button about = new Button("About");
        about.setLayoutX(32);
        about.setLayoutY(560);
        about.setId("aboutButton"); 
        parent.getChildren().add(about);
        
        about.setOnAction(e -> {
        	if(info == null) {
        	info = new Label("Instructions: " + "\n" + "\n" + 
        "Enter a start and end location in the textboxes and press the submit/find "
        + "button to display the shortest path between the two locations. " + "\n" + "Click the "
        + "checkboxes to toggle stop points and/or times and press the submit button again to refresh.");
        	info.setLayoutX(32);
            info.setLayoutY(400);
            info.setId("info");
            parent.getChildren().add(info);
            
        	} else { 
        		parent.getChildren().remove(info);
        		info = null;
        	}
        });
        
        Button quit = new Button("Quit");
        quit.setLayoutX(96);
        quit.setLayoutY(560);
        quit.setOnAction(e -> Platform.exit());
        parent.getChildren().add(quit);   
    }



}
