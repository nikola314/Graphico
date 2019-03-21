package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SampleController {
	private String filePath="C:\\Users\\Nikola\\Desktop\\C_CSV.csv";
	static int offx=15, offy=15,cnt=1;
	    
	@FXML
	private ScrollPane scrollPane;	
	@FXML
	private Canvas canvas;
	@FXML
	private Slider slider;	
	@FXML
	private ChoiceBox choiceBoxNodeSize;
	@FXML
	private ChoiceBox choiceBoxEdgeSize;
	@FXML
	private ColorPicker cpLabel;
	@FXML
	private ColorPicker cpNode;
	@FXML
	private ColorPicker cpEdge;
	@FXML
	private ColorPicker cpfe;
	@FXML
	private ColorPicker cpfn;
	@FXML
	private ColorPicker cpfl;
	@FXML
	private ColorPicker cpse;
	@FXML
	private ColorPicker cpsn;
	@FXML
	private ColorPicker cpsl;
	
	@FXML
	private RadioButton lss;
	@FXML
	private RadioButton grt;
	
	@FXML
	private TextArea ta1;
	@FXML
	private MenuItem redoitem;
	@FXML
	private MenuItem undoitem;
	@FXML
	private TextField inTf;
	@FXML
	private TextField ieTf1;
	@FXML
	private TextField ieTf2;
	@FXML
	private TextField dnTf;
	@FXML
	private TextField deTf1;
	@FXML
	private TextField deTf2;
	@FXML
	private TextField spTf1;
	@FXML
	private TextField spTf2;
	@FXML
	private TextField fgTf;
	@FXML
	private TextField name;
	@FXML 
	private ColorPicker color;
	@FXML
	private ChoiceBox size;
	@FXML
	private Label lname;
	@FXML
	private Label lsize;
	@FXML
	private Label lcolor;
	@FXML
	private Button apply;
	OperationList state=null;
	
	public void insertNode() {	
		String node=inTf.getText();
		if(node.equals("")) return;
		if(Graph.addNode(node)) {
			Node n=Graph.getNode(node);
			if(offx>canvas.getWidth()) {
				offx=15;
				cnt++;
			}
			if(offy>canvas.getHeight()) {
				offy=cnt*15;;
			}
			n.x=offx;
			n.y=offy;
			offx+=30;
			paintGraph();
		
			state.add(new AddNode(n));
		}
	}
	public void insertEdge() {
		String n1=ieTf1.getText();
		String n2=ieTf2.getText();
		Node nd1, nd2;
		if((nd1=Graph.getNode(n1))!=null && (nd2=Graph.getNode(n2))!=null) {
			Graph.addEdge(nd1, nd2);
			if(!nd1.neighbors.contains(nd2))
				nd1.neighbors.add(nd2);
			paintGraph();	
			state.add(new AddEdge(Graph.getEdge(nd1, nd2)));
		}
	}
	public void deleteNode() {
		String node=dnTf.getText();
		if(!node.equals("")) {
			if(Graph.getNode(node)!=null)
				state.add(new DeleteNode(Graph.getNode(node)));
			Graph.removeNode(node);
			paintGraph();
		}
	}
	public void deleteEdge() {
		Graph.removeEdge(deTf1.getText(), deTf2.getText());
		paintGraph();
	}
	
	@FXML
	public void shortestPath() {
		String s1=spTf1.getText();
		String s2=spTf2.getText();
		Graph.formatRemove();
		ShortestPath sp=new ShortestPath(Graph.nodes);
		LinkedList<Node> path=sp.findPath(Graph.getNode(s1), Graph.getNode(s2));
		if(path==null) return;
		if(path.size()<2) return;
		for(Node n:path) {
			n.formatted=true;
		}
		Graph.getNode(s1).formatted=true;
		paintGraph();
	}
	
	public void filterGraph() {
		int formatCnt=Integer.parseInt(fgTf.getText());
		boolean direction=lss.isSelected();
		state.add(new SetFormat(direction,formatCnt));
		Graph.formatLess=direction;
		Graph.format(formatCnt);
		paintGraph();
	}
	
	public void forceAtlas() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Graph.forceAtlas(canvas.getWidth()/2,canvas.getHeight()/2);				
				paintGraph();
			}			
		});
	}
	@FXML
	private TextField expansionScale;
	
	double factor;
	
	public void expansion() {
		factor=Double.parseDouble(expansionScale.getText());
		if(factor==0) return;
		if(factor<0) factor*=-1;
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Main.expanded=true;
				double oldw=canvas.getWidth();
				double oldh=canvas.getHeight();
				double newh=canvas.getHeight()*factor;
				double neww=canvas.getWidth()*factor;
				
				double movx=0;
				double movy=0;
				for(Node n:Graph.nodes) {
					n.x+=(1-factor)*(oldw/2-n.x);
					if(n.x<0 || n.x>oldw) {
						if(Math.abs(n.x)>movx) {
							movx=Math.abs(n.x);
						}
					}
					n.y+=(1-factor)*(oldh/2-n.y);
					if(n.y<0 || n.y>oldh) {
						if(Math.abs(n.y)>movy) {
							movy=Math.abs(n.y);
						}
					}
				}
				canvas.setHeight(oldh+1.5*movy);
				canvas.setWidth(movx*1.5+oldw);
				for(Node n:Graph.nodes) {
					n.x+=movx;
					n.y+=movy;
				}

				double right=0;
				double left=Double.MAX_VALUE;
				double up=Double.MAX_VALUE;
				double down=0;
				for(Node n:Graph.nodes) {
					if(n.x<left) left=n.x;
					if(n.x>right) right=n.x;
					if(n.y<up) up=n.y;
					if(n.y>down) down=n.y;			
				}
				for(Node n:Graph.nodes) {
					n.x-=(left-30);
					n.y-=(up-30);
				}
				canvas.setHeight(down-up+60);
				canvas.setWidth(right-left+60);
				Main.baseh=down-up+60;
				Main.basew=right-left+60;
				Graph.currh=Main.baseh;
				Graph.currw=Main.basew;				
				paintGraph();	
			}
			
		});
	}
	
	public void unlockSlider() {
		slider.setDisable(false);
	}
	
	public void closeGraph() {
		Graph.clear();
		paintGraph();
	}
	
	boolean graphLoaded=false;
	
	public void loadGraph() {
		firstEntry=true;
		Graph.nodeRC=15;
		Graph.edgeWC=0.25;
		readSettings();
		state=new OperationList();
		Graph.loadGraph(filePath);
		canvas.setWidth(Graph.zoomFactor*(scrollPane.getWidth()-5));
		canvas.setHeight(Graph.zoomFactor*(scrollPane.getHeight()-5));
		Graph.currh=(int) canvas.getHeight();
		Graph.currw=(int) canvas.getWidth();

		
		if(!graphLoaded) {
			graphLoaded=true;
			Main.pStage.setOnCloseRequest((WindowEvent e)->{
				Alert alert=new Alert(AlertType.WARNING, "Exit without saving?",ButtonType.YES,ButtonType.NO);
				alert.setTitle("Save your work!");
				Stage s= (Stage)alert.getDialogPane().getScene().getWindow();
				s.getIcons().add(new Image("https://cdn.geekwire.com/wp-content/uploads/2016/07/GraphDB-FA-big-hero.png"));
				alert.showAndWait();
				if(alert.getResult()==ButtonType.NO) {
					exportAsGraphico();
				}
			});
			
		}
		
		
		
		final int nodsInLine=100;
		boolean[][] mapper=new boolean[nodsInLine][nodsInLine];
		for(Node n:Graph.nodes) {
			boolean done=false;
			while(!done) {
				int x=(int)(Math.random()*nodsInLine);
				int y=(int)(Math.random()*nodsInLine);
				if(mapper[x][y]==false) {
					mapper[x][y]=true;
					double scaleX=20+((canvas.getWidth()-50)/nodsInLine)*x;				
					double scaleY=20+((canvas.getHeight()-50)/nodsInLine)*y;				
					n.x=scaleX;
					n.y=scaleY;
					done=true;
				}
			}
		}
		paintGraph();
		firstEntry=false;
	}
	
	public void about() {
		Alert alert=new Alert(AlertType.INFORMATION,"",ButtonType.OK);
		alert.setTitle("About");
		alert.setHeaderText("Made by: Nikola");
		Stage s= (Stage)alert.getDialogPane().getScene().getWindow();
		s.getIcons().add(new Image("https://cdn.geekwire.com/wp-content/uploads/2016/07/GraphDB-FA-big-hero.png"));
		alert.showAndWait();
	}
	
	private String olds="";
	boolean firstEntry=true;
	private String oldse="";
	@FXML
	private Label numnodes;
	@FXML
	private Label numedges;
	
	
	private void readSettings() {
		if(state==null) state=new OperationList();
		String s=(String)choiceBoxNodeSize.getValue();
		if(s!=null) 
			if(!s.equals("")) 
				{
				if(!s.equals(olds) && !undoCalled && !firstEntry) {
					
					state.add(new NodeSize(choiceBoxNodeSize,olds,s));
					olds=new String(s);
				}
				Graph.nodeRC=(int) Double.parseDouble(s);
				Graph.nodeR=(int) (Graph.nodeRC*Graph.zoomFactor);
				}
		s=(String)choiceBoxEdgeSize.getValue();
		if(s!=null) 
			if(!s.equals("")){
				if(!s.equals(oldse) && !undoCalled && !firstEntry) {
					state.add(new EdgeSize(choiceBoxEdgeSize,Graph.edgeWC,Double.parseDouble(s)));
					oldse=new String(s);
				}
				Graph.edgeWC=Double.parseDouble(s);
				Graph.edgeW= Graph.edgeWC*Graph.zoomFactor;
			}
		Graph.formatColorNode=cpfn.getValue();
		Graph.formatColorEdge=cpfe.getValue();
		Graph.selectedColorEdge=cpse.getValue();
		Graph.selectedColorNode=cpsn.getValue();
		Graph.selectedColorLabel=cpsl.getValue();
		Graph.formatColorLabel=cpfl.getValue();
		Graph.nodeColor=cpNode.getValue();
		Graph.edgeColor=cpEdge.getValue();
		Graph.labelColor=cpLabel.getValue();
		if(Graph.nodes!=null) {
			numnodes.setText("Number of nodes: "+Graph.nodes.size());
			numedges.setText("Number of edges: "+Graph.edges.size());
		}
		}
	
	private boolean undoCalled=false;
	
	public void paintGraph() {
		readSettings();
		GraphicsContext g=canvas.getGraphicsContext2D();
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		double x1=selectionStartX,x2=selectionEndX,y1=selectionStartY,y2=selectionEndY;
    	if(selectionStartX>selectionEndX) {
    		x1=selectionEndX;
    		x2=selectionStartX;
    	}
    	if(selectionStartY>selectionEndY) {
    		y1=selectionEndY;
    		y2=selectionStartY;
    	}
		if(selectionStartX!=0 || selectionEndX!=0) {
			
			g.setFill(Color.AQUA);
			g.fillRect(x1, y1, x2-x1, y2-y1);
			g.fill();
		}
		
		
		Graph.paint(canvas);
	}
	
	
	
	public void adjustSize() {
		canvas.setWidth(Graph.zoomFactor*(scrollPane.getWidth()-5));
	    canvas.setHeight(Graph.zoomFactor*(scrollPane.getHeight()-5));
		Graph.updatePositions(canvas.getWidth(), canvas.getHeight());
		readSettings();
		paintGraph();
	}

	
	
	@FXML
	public void draggingOver(DragEvent event) {
      Dragboard board = event.getDragboard();
      if (board.hasFiles()) {
        event.acceptTransferModes(TransferMode.ANY);
      }    
    }

    @FXML
    public void dropping(DragEvent event) {
      Dragboard board = event.getDragboard();
	System.out.println(board.getString());
	
	List<File> phil = board.getFiles();
	
	Graph.clear();
	filePath=phil.get(0).getPath();
	loadGraph();
	
     }
    
    double selectionStartX=0;
    double selectionEndX=0;
    double selectionStartY=0;
    double selectionEndY=0;
    boolean pressed=false;
    @FXML
    public void canvasMousePressed(MouseEvent m) {
    	double x=m.getX();
    	double y=m.getY();
    	pressed=true;
    	selectionStartX=selectionEndX=x;
    	selectionStartY=selectionEndY=y;
    	
    	for(Node n:Graph.nodes) {
    		
    		double measure=n.modified?(n.modifiedS*Graph.zoomFactor)/2:Graph.nodeR/2;
    		double distance=Math.sqrt(Math.pow(n.x+measure-x, 2)+Math.pow(n.y+measure-y,2));
    		if(distance<=measure) {
    			Graph.holded=n;
    			Graph.selectedC=n;
    			break;
    		}
    	} 	
    }
    
    @FXML
    public void canvasMouseReleased(MouseEvent m) {
    	Graph.holded=null;
    	    
       
    }
    

    
    @FXML
    public void canvasMouseMoved(MouseEvent m) {
    		double x=m.getX();
        	double y=m.getY();

    		Graph.selected=null;
    		if(Graph.nodes!=null)
	    		for(Node n:Graph.nodes) {
	        		double distance=Math.sqrt(Math.pow(n.x+Graph.nodeR/2-x, 2)+Math.pow(n.y+Graph.nodeR/2-y,2));
	        		if(distance<=Graph.nodeR/2) {
	        			Graph.selected=n;
	        			Graph.select(n);
	        			paintGraph();
	        			if(Graph.selectedC!=null) {
	        				if(!(Graph.selectedC.name().equals(n.name()))) {     					
	        					ta1.setText("Over: "+n.name());
	        					if(Graph.clicked!=null) ta1.appendText("\nSelected: "+Graph.clicked.name());
	        					return;
	        				}
	        			}
	        			else {
	        				Graph.selectedC=Graph.selected;
	        				ta1.setText("Over: "+n.name());
        					if(Graph.clicked!=null) ta1.appendText("\nSelected: "+Graph.clicked.name());
        					return;
	        			}
	        		}
	        	}
    		
    		Graph.selectedC=null;
    }
    
    @FXML
    public void canvasMouseDragged(MouseEvent m) {
    	selectionEndX=m.getX();
    	selectionEndY=m.getY();
    	
    	if(Graph.holded!=null) {
    		Graph.holded.x=m.getX()-Graph.nodeR/2;
    		Graph.holded.y=m.getY()-Graph.nodeR/2;   		
    	}
    	paintGraph();
    }
    @FXML
    public void canvasMouseClick(MouseEvent m) {
    	double x=m.getX();
    	double y=m.getY();
    	apply.setDisable(false);
    	name.setDisable(false);
    	color.setDisable(false);
    	size.setDisable(false);
    	lname.setDisable(false);
    	lsize.setDisable(false);
    	lcolor.setDisable(false);
    	
    	if(selectionStartX>selectionEndX) {
    		double tmp=selectionStartX;
    		selectionStartX=selectionEndX;
    		selectionEndX=tmp;
    	}
    	if(selectionStartY>selectionEndY) {
    		double tmp=selectionStartY;
    		selectionStartY=selectionEndY;
    		selectionEndY=tmp;
    	}

    	Graph.inSelection=new LinkedList<Node>();
	for(Node n:Graph.nodes) {  		
    		if(n.x>=selectionStartX && n.x<=selectionEndX)
    			if(n.y<=selectionEndY && n.y>=selectionStartY)
    				Graph.inSelection.add(n);
    		
    	}
	
    	pressed=false;
    	selectionStartX=0;
        selectionEndX=0;
        selectionStartY=0;
        selectionEndY=0;
  //  	selectionStartX=selectionEndX=selectionStartY=selectionEndY=0;
    	pressed=false;
    	boolean found=false;
    	for(Node n:Graph.nodes) {
    		double distance=Math.sqrt(Math.pow(n.x-x, 2)+Math.pow(n.y-y,2));
    		if(distance<=Graph.nodeR) {
    			Graph.clicked=n;
    			found=true;
    			break;
    		}
    	} 
    	if(found) {
	    	
	    	
	    	if(Graph.selectedC!=null) {
	    		ta1.setText("Over: "+Graph.selectedC.name());
	    		ta1.appendText("\nSelected: "+Graph.clicked.name());
	    	}
	    	else {
	    		ta1.setText("\nSelected: "+Graph.clicked.name());
	    	}
    	}
    }
    

   
    
    @FXML
    public void resetFormat() {
    	Graph.formatRemove();
    	paintGraph();
    }
    
    @FXML
    public void changeNode() {
    	for(Node n:Graph.inSelection) {
    		n.modified=true;
    		n.modifiedS=Double.parseDouble((String)size.getValue());
        	n.modifiedC=color.getValue();
    	}
    	

    	paintGraph();
    	
    	if(Graph.clicked==null) return;
    	if(Graph.getNode(name.getText())!=null)return;
    	Graph.clicked.modified=true;
    	Graph.clicked.modifiedS=Double.parseDouble((String)size.getValue());
    	Graph.clicked.modifiedC=color.getValue();
    	Graph.clicked.name=name.getText();
    	
    	if(Graph.clicked.modified) {
    		state.add(new NodeChange(Graph.clicked,Graph.clicked.modifiedC,color.getValue(),Graph.clicked.name(),name.getText(),Graph.clicked.modifiedS, Double.parseDouble((String)size.getValue()),true));
    	}
    	else {
    		state.add(new NodeChange(Graph.clicked,Graph.clicked.modifiedC,color.getValue(),Graph.clicked.name(),name.getText(),Graph.clicked.modifiedS, Double.parseDouble((String)size.getValue()),false));
    	}
    	

    	paintGraph();
    }
    @FXML
    public void loadFiles() {
    	FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Graph Files", "*.csv", "*.gml", "*.graphico"));
    	 File selectedFile = fileChooser.showOpenDialog(Main.pStage);
    	 if (selectedFile != null) { 	     
    	    Graph.clear();
    	 	filePath=selectedFile.getPath();
    	 	if(filePath.endsWith(".graphico")) {
    	 		state=new OperationList();
    	 		 try {
    	 			FileInputStream fis = new FileInputStream(filePath);
    	 			ObjectInputStream in = new ObjectInputStream(fis);
    	 			GraphicoFormat g=(GraphicoFormat) in.readObject();
    	 			in.close();
    	 			g.setGraph();
    	 			paintGraph();
    	 			
    	 		 
    	 		 } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	 		
    	 	}
    	 	else {
    	 		loadGraph();
    	 	}
    	 }
    }
    
    @FXML
    public void exportAsPng() {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
    	fc.setTitle("Save as Image");
    	File file=fc.showSaveDialog(Main.pStage);
    	if(file!=null) {
    		WritableImage w=new WritableImage((int)(canvas.getWidth()),(int)(canvas.getHeight()));
    		try {
    			ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(null, w), null), "png", file);
    		}
    		catch(Exception e) {			
    			e.printStackTrace();
    		}
    	}
    }
    
    @FXML
    public void exportAsGraphico(){
    	GraphicoFormat g=new GraphicoFormat();
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("GRAPHICO", "*.graphico"));
    	fc.setTitle("Save as Graphico");
    	File file=fc.showSaveDialog(Main.pStage);
    	if(file!=null) {
    		try {
				FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				ObjectOutputStream oos=new ObjectOutputStream(fos);			
				oos.writeObject(g);
				if(oos!=null) {
					oos.flush();
					oos.close();
				}
				
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
    	}
    }
    
    int stateCnt=0;
    @FXML
    public void saveState() {
    	if(stateCnt==4) return;
    	GraphicoFormat g=new GraphicoFormat();
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("GRAPHICO", "*.graphico"));
    	fc.setTitle("Save as Graphico");
    	File file=fc.showSaveDialog(Main.pStage);
    	if(file!=null) {
    		try {
				FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				ObjectOutputStream oos=new ObjectOutputStream(fos);			
				oos.writeObject(g);
				if(oos!=null) {
					oos.flush();
					oos.close();
				}
				
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
    	}
    	stateCnt++;   	
    }
    
    
    @FXML
    public void restoreState() {
    FileChooser fileChooser = new FileChooser();
   	 fileChooser.setTitle("Open Resource File");
   	 fileChooser.getExtensionFilters().addAll(
   	         new ExtensionFilter("Graph Files", "*.csv", "*.gml", "*.graphico"));
   	 File selectedFile = fileChooser.showOpenDialog(Main.pStage);
   	 if (selectedFile != null) { 	     
   	    Graph.clear();
   	 	filePath=selectedFile.getPath();
   	 state=new OperationList();
		 try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fis);
			GraphicoFormat g=(GraphicoFormat) in.readObject();
			in.close();
			g.setGraph();
			paintGraph();
			
		 
		 } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   	 }
    }
    
    
    //proba zooma
    @FXML
    public void zoom(ScrollEvent se) {
    	double d=slider.getValue()*(se.getDeltaY()>0?1.2:0.8);
    	if(d>3) d=3;
    	if(d<1) d=1;
    	slider.setValue(d>3?3:d);
    }
    
   
    @FXML
    public void undo() {
    	undoCalled=true;
    	state.undo();
    	paintGraph();
    	undoCalled=false;
    }
    
    @FXML
    public void redo() {
    	state.redo();
    	paintGraph();
    }
    
    @FXML
    public void pickerChanged1() {
    	state.add(new ColorPickerChange(cpLabel,Graph.labelColor,cpLabel.getValue()));
    	paintGraph();
    }
    @FXML
    public void pickerChanged2() {
    	state.add(new ColorPickerChange(cpNode,Graph.nodeColor,cpLabel.getValue()));
    	paintGraph();   	
    }
    @FXML
    public void pickerChanged3() {
    	state.add(new ColorPickerChange(cpEdge,Graph.edgeColor,cpEdge.getValue()));
    	paintGraph();   	
    }
    @FXML
    public void pickerChanged4() {
    	state.add(new ColorPickerChange(cpsl,Graph.selectedColorLabel,cpsl.getValue()));
    	paintGraph();   	
    }
    @FXML
    public void pickerChanged5() {
    	state.add(new ColorPickerChange(cpsn,Graph.selectedColorNode,cpsn.getValue()));
    	paintGraph();   	
    }
    @FXML
    public void pickerChanged6() {
    	state.add(new ColorPickerChange(cpse,Graph.selectedColorEdge,cpse.getValue()));
    	paintGraph();    	
    }
    @FXML
    public void pickerChanged7() {
    	state.add(new ColorPickerChange(cpfl,Graph.formatColorLabel,cpfl.getValue()));
    	paintGraph();    	
    }
    @FXML
    public void pickerChanged8() {
    	state.add(new ColorPickerChange(cpfn,Graph.formatColorNode,cpfn.getValue()));
    	
    	paintGraph();   	
    }
    @FXML
    public void pickerChanged9() {
    	state.add(new ColorPickerChange(cpfe,Graph.formatColorEdge,cpfe.getValue()));
    	
    	paintGraph();   	
    }
    
    
    
}
