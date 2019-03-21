package application;
	

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;


public class Main extends Application {
	
	static boolean expanded=false;
	static double baseh;
	static double basew;
	
	private void initElements(Scene scene) {
		ChoiceBox edgeSize=(ChoiceBox) scene.lookup("#choiceBoxEdgeSize");
		ChoiceBox nodeSize=(ChoiceBox) scene.lookup("#choiceBoxNodeSize");
		
		edgeSize.getItems().add("1");
		edgeSize.getItems().add("0.75");
		edgeSize.getItems().add("0.5");
		edgeSize.getItems().add("0.25");
		edgeSize.getItems().add("0.1");
		edgeSize.setValue("0.25");

		nodeSize.getItems().add("2.5");
		nodeSize.getItems().add("5");
		nodeSize.getItems().add("10");
		nodeSize.getItems().add("15");
		nodeSize.getItems().add("20");
		nodeSize.getItems().add("25");
		nodeSize.getItems().add("30");
		nodeSize.setValue("15");
		
		nodeSize=(ChoiceBox) scene.lookup("#size");
		nodeSize.getItems().add("2.5");
		nodeSize.getItems().add("5");
		nodeSize.getItems().add("10");
		nodeSize.getItems().add("15");
		nodeSize.getItems().add("20");
		nodeSize.getItems().add("25");
		nodeSize.getItems().add("30");
		nodeSize.setValue("15");
		
		ColorPicker cp=(ColorPicker) scene.lookup("#cpNode");
		cp.setValue(Color.BLUE);
		cp=(ColorPicker) scene.lookup("#cpEdge");
		cp.setValue(Color.ORANGE);
		cp=(ColorPicker) scene.lookup("#cpLabel");
		cp.setValue(Color.BLACK);
		
		cp=(ColorPicker) scene.lookup("#cpfl");
		cp.setValue(Color.RED);
		cp=(ColorPicker) scene.lookup("#cpfn");
		cp.setValue(Color.YELLOW);
		cp=(ColorPicker) scene.lookup("#cpfe");
		cp.setValue(Color.BLACK);
		
		cp=(ColorPicker) scene.lookup("#cpsn");
		cp.setValue(Color.MAGENTA);
		cp=(ColorPicker) scene.lookup("#cpsl");
		cp.setValue(Color.DARKSLATEBLUE);
		cp=(ColorPicker) scene.lookup("#cpse");
		cp.setValue(Color.CRIMSON);
		
		
		Slider s=(Slider) scene.lookup("#slider");
		s.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldVal, Number newVal) {
				Graph.zoomFactor=newVal.doubleValue();
				
				Canvas canvas=(Canvas) scene.lookup("#canvas");
				ScrollPane scrollPane=(ScrollPane) scene.lookup("#scrollPane");
				
			    Bounds bounds = scrollPane.getViewportBounds();
			    double width=bounds.getWidth();
			    double height=bounds.getHeight();
			    if(!expanded) {
			    	canvas.setWidth(Graph.zoomFactor*(width-5));
			    	canvas.setHeight(Graph.zoomFactor*(height-5));
			    }
			    else {
			    	canvas.setWidth(Graph.zoomFactor*basew);
			    	canvas.setHeight(Graph.zoomFactor*baseh);
			    }
			    Graph.edgeW=Graph.edgeWC*Graph.zoomFactor;
			    Graph.nodeR=(int) (Graph.nodeRC*Graph.zoomFactor);
			    
			    Graph.updatePositions(canvas.getWidth(), canvas.getHeight());
				
				Graph.paint(canvas);
			}
			
		});
	}
	
static Stage pStage;	
	@Override
	public void start(Stage primaryStage) {
		try {
			Format.init();
			pStage=primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Graphico");
			primaryStage.setMinHeight(520);
			primaryStage.setMinWidth(770);
			primaryStage.getIcons().add(new Image("https://cdn.geekwire.com/wp-content/uploads/2016/07/GraphDB-FA-big-hero.png"));
			initElements(scene);
			
		// ---test----
		//	Graph.loadGraph("C:\\Users\\Nikola\\Desktop\\poop qt\\test\\C_GML.gml");
		//	System.out.print(Graph.printGraph());
					
		// --end test	
			//promeniti ovo sve staviti da kad se resajzuje resajzuje i canvas u odnosu na panel
			
			primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {

			    @Override
			    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

			    }
			});
			scene.widthProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
    	
			   }
			});
			scene.heightProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {

			    }
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
		
	}
}
