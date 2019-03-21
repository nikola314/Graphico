package application;

import java.io.Serializable;
import java.util.LinkedList;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;

//dodati zoomfactor, canvas size itd

public class GraphicoFormat implements Serializable {
	LinkedList<Node> nodes=null;
	LinkedList<Edge> edges=null;
	int noder,noderc;
	double edgew, edgewc;
	double currw,currh;
	double zoom;
	double canvash,canvasw;
	public GraphicoFormat() {
		nodes=Graph.nodes;
		edges=Graph.edges;
		noder=Graph.nodeR;
		noderc=Graph.nodeRC;
		edgew=Graph.edgeW;
		edgewc=Graph.edgeWC;
		zoom=Graph.zoomFactor;
		currw=Graph.currw;
		currh=Graph.currh;
	}
	
	public void setGraph() {
		Graph.nodes=nodes;
		Graph.edges=edges;
		Graph.nodeR=noder;
		Graph.nodeRC=noderc;	
		Graph.edgeW=edgew;
		Graph.edgeWC=edgewc;
		Graph.zoomFactor=zoom;
		Graph.currh=currh;
		Graph.currw=currw;
		Canvas canvas=(Canvas)Main.pStage.getScene().lookup("#canvas");
		ScrollPane scrollPane=(ScrollPane) Main.pStage.getScene().lookup("#scrollPane");
		canvas.setWidth(Graph.zoomFactor*(scrollPane.getWidth()-5));
	    canvas.setHeight(Graph.zoomFactor*(scrollPane.getHeight()-5));
		Graph.updatePositions(canvas.getWidth(), canvas.getHeight());
		Slider slider=(Slider) Main.pStage.getScene().lookup("#slider");
		slider.setValue(zoom);
	}
}
