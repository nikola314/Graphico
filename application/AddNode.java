package application;

import java.util.LinkedList;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

interface OperationPerformed {	
	public abstract void undo();
	public abstract void redo();
}


public class AddNode implements OperationPerformed {
	Node node;
	LinkedList<Node> neighbors=new LinkedList<Node>();
	public AddNode(Node n) {
		node=n;
		for(Node nn:Graph.nodes) {
			if(nn.neighbors.contains(node)) neighbors.add(nn);
		}
	}
	@Override
	public void undo() {
		Graph.removeNode(node.name());
	}

	@Override
	public void redo() {
		Graph.nodes.add(node);
		for(Node n:neighbors) {
			Graph.addEdge(n.name(), node.name());			
		}
		for(Node n:node.neighbors) {
			Graph.addEdge(node.name(), n.name());
		}
	}
}

class AddEdge implements OperationPerformed {
	Edge edge;
	public AddEdge(Edge e) {
		edge=e;
	}
	
	@Override
	public void undo() {
		Graph.removeEdge(edge.nodeFrom().name(),edge.nodeTo().name());
	}

	@Override
	public void redo() {
		Graph.addEdge(edge.nodeFrom().name(), edge.nodeTo().name());
	}
}

class DeleteNode implements OperationPerformed {
	Node node;
	LinkedList<Node> neighbors=new LinkedList<Node>();
	LinkedList<Node> myNeighbors=new LinkedList<Node>();
	public DeleteNode(Node n) {
		node=n;
		for(Node nn:Graph.nodes) {
			if(nn!=node && nn.neighbors.contains(node)) neighbors.add(nn);
		}
		for(Node nn:node.neighbors) myNeighbors.add(nn);
	}
	
	@Override
	public void undo() {
		Graph.nodes.add(node);
		for(Node n:neighbors) {
			Graph.addEdge(n.name(), node.name());			
		}
		for(Node n:myNeighbors) {
			Graph.addEdge(node.name(), n.name());
		}
	}

	@Override
	public void redo() {
		Graph.removeNode(node.name());
	}
}

class SetFormat implements OperationPerformed {
	boolean less;
	int value;
	public SetFormat(boolean lss, int val) {
		value=val;
		less=lss;
	}
	@Override
	public void undo() {
		Graph.formatRemove();
	}

	@Override
	public void redo() {
		Graph.formatLess=less;
		Graph.format(value);
	}
}

//add delete edge
class DeleteEdge implements OperationPerformed{
	Node n1;
	Node n2;
	
	public DeleteEdge(Edge e) {
		if(e==null) return;
		n1=e.nodeFrom();
		n2=e.nodeTo();
	}
	
	@Override
	public void undo() {
			Graph.addEdge(n1.name(), n2.name());
	}

	@Override
	public void redo() {
		Graph.removeEdge(n1.name(), n2.name());
	}
}


class NodeSize implements OperationPerformed {
	String oldSize,newSize;
	ChoiceBox box;
	public NodeSize(ChoiceBox size,String old, String newS) {
		box=size;
		oldSize=new String(old);
		newSize=newS;
	}
	@Override
	public void undo() {
		box.setValue(oldSize);
	}

	@Override
	public void redo() {
		box.setValue(newSize);
	}
}

class ColorPickerChange implements OperationPerformed {
	ColorPicker picker;
	Color oldc,newc;
	public ColorPickerChange(ColorPicker cp, Color ol, Color newc) {
		oldc=ol;
		this.newc=newc;
		picker=cp;
	}
	@Override
	public void undo() {
		picker.setValue(oldc);
	}

	@Override
	public void redo() {
		picker.setValue(newc);
	}
}


class EdgeSize implements OperationPerformed {
	double oldSize,newSize;
	ChoiceBox box;
	public EdgeSize(ChoiceBox size,double old, double newS) {
		box=size;
		oldSize=old;
		newSize=newS;
	}
	@Override
	public void undo() {
		//mozda treba .0 da se skine
		box.setValue(Double.toString(oldSize));
	}

	@Override
	public void redo() {
		box.setValue(Double.toString(newSize));
	}
}



//iskucati!
class NodeChange implements OperationPerformed {
	Node node;
	Color oldColor;
	Color newColor;
	String oldName,newName;
	double oldSize,newSize;
	boolean wasModified;
	public NodeChange(Node n, Color oldC,Color newC, String oldN,String newN,double oldS,double newS,boolean wasModified) {
		node=n;
		oldColor=oldC;
		newColor=newC;
		oldName=oldN;
		newName=newN;
		oldSize=oldS;
		newSize=newS;
		this.wasModified=wasModified;
	}
	@Override
	public void undo() {
		node.modified=wasModified;
		node.name=oldName;
		if(wasModified) {
			node.modifiedC=oldColor;
			node.modifiedS=oldSize;
		}
	}

	@Override
	public void redo() {
		node.modified=true;
		node.name=newName;
		node.modifiedC=newColor;
		node.modifiedS=newSize;
	}
}



