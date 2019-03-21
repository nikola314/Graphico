package application;

import java.io.Serializable;
import java.util.LinkedList;

import javafx.scene.paint.Color;



public class Node implements Serializable{
	public int level=0;
	public LinkedList<Node> neighbors=new LinkedList<Node>();
	String name;
	public double x;
	public double y;
	public boolean formatted=false;
	public boolean selected=false;
	public boolean modified=false;
	public transient Color modifiedC=Color.BLACK;
	public double modifiedS=15;

	public Node(String s) {
		name=s;
	}
	
	public void incLevel() {
		level++;
	}
	
	public void addNeighbor(Node n) {
		if(neighbors.contains(n)) return;
		neighbors.add(n);
	}
	
	public String name() {
		return this.name;
	}
}
