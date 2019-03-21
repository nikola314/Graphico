package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

public class Graph {
	
	static Node root;
	static LinkedList<Node> inSelection=new LinkedList<Node>();
	static LinkedList<Node> nodes=new LinkedList<Node>();
	static LinkedList<Edge> edges=new LinkedList<Edge>();
	static LinkedList<Node> changedNodes=new LinkedList<Node>();
	static double currw=765, currh=481;
	static int nodeR=10;
	static int nodeRC=10;
	static double edgeWC=0.1;
	static double edgeW=0.1;
	static Color nodeColor=Color.BLACK;
	static Color edgeColor=Color.BLACK;
	static Color labelColor=Color.BLACK;
	public static double zoomFactor=1;
	static Color formatColorEdge=Color.BLACK;
	static Color formatColorNode=Color.YELLOW;
	static Color selectedColorEdge=Color.AQUAMARINE;
	static Color selectedColorNode=Color.BLACK;
	static Color selectedColorLabel=Color.BLACK;
	static Color formatColorLabel=Color.BLACK;	
	static Node holded=null;
	static Node selected=null;
	static Node selectedC=null;
	static boolean formatLess=false;
	static Node clicked=null;
	//stanja za undo i redo
//	static LinkedList<State> state=new LinkedList<State>();
	
	static void saveCurrentState() {
	//	State s=new State();		
	}
	
	static synchronized void loadGraph(String path) {
		Format.readFile(path);	
	}
	
	public static synchronized void format(int x) {
		formatRemove();
		for(Node n:nodes) {
			if(formatLess==false) {
				if(n.level>=x) {
					n.formatted=true;
				}
				else n.formatted=false;
			}
			else {			
				if(n.level<=x) {
					n.formatted=true;
				}
				else n.formatted=false;		
			}
		}
	}
	
	public static synchronized  void formatRemove() {
		for(Node n:nodes)
			n.formatted=false;
	}
	
	public static synchronized  void select(Node n) {
		selectRemove();
		n.selected=true;
		for(Node nd:n.neighbors) {
			nd.selected=true;
		}
		
	}
	
	public static synchronized  void selectRemove() {
		for(Node n:nodes) {
			n.selected=false;	
		}
	}
	
	
	public static synchronized  void removeNode(String s) {
		Node n=getNode(s);
		if(n!=null) {
			for(Node nn:nodes) {
				nn.neighbors.remove(n);
			}
			for(Node n2:nodes) {
				removeEdge(n.name(),n2.name());
			}
			nodes.remove(n);
		}
	}
	
	public static  synchronized void removeEdge(String n1,String n2) {
		Edge e=getEdge(getNode(n1),getNode(n2));
		if(e!=null) {
			edges.remove(e);
			getNode(n1).level--;
			getNode(n2).level--;
			e.nodeFrom().neighbors.remove(e.nodeTo());
		}
	}
	
	static synchronized  String printGraph() {
		StringBuilder sb=new StringBuilder();
		for(Node n: nodes) {
			sb.append(n.name()).append("\n");
		}
		return sb.toString();
	}
	
	static synchronized  boolean addNode(String name) {
		if(getNode(name)==null) {
			nodes.add(new Node(name));
			return true;
		}
		return false;
	}
	
	static synchronized  Node getNode(String s) {
		for(Node n:nodes) {
			if(n.name().equals(s)) return n;
		}
		return null;
	}
	
	static synchronized  Edge getEdge(Node n1, Node n2) {
		if(n1==null || n2==null) return null;
		Edge temp=new Edge(n1,n2);
		for(Edge e:edges) {
			if(e.equals(temp)) return e;
		}
		return null;
	}
	
	static synchronized  boolean addEdge(Node n1, Node n2) {
		if(getEdge(n1,n2)==null) {
			edges.add(new Edge(n1,n2));
			return true;
		}
		return false;
	}
	
	static synchronized  boolean addEdge(String s1, String s2) {
		Node n1=getNode(s1);
		Node n2=getNode(s2);
		if(n1==null) {
			addNode(s1);
			n1=getNode(s1);
		}
		
		if(n2==null) {
			addNode(s2);
			n2=getNode(s2);
		}
		
		if(addEdge(n1,n2)) {
			n1.incLevel();
			n2.incLevel();
			n1.addNeighbor(n2);
		}
		return false;
	}
	
	public static synchronized  void updatePositions(double w,double c) {
		double neww=w;
		double newh=c;
		double mulw=1, mulh=1;
		if(neww/currw >0) 
			mulw=neww/currw;
		if(newh/currh >0) 
			mulh=newh/currh;
		currw=neww;
		currh=newh;
		for(Node n:nodes) {
			n.x*=mulw;
			n.y*=mulh;
		}	
	}
	
	public static synchronized  void paint(Canvas canvas) {	
		GraphicsContext gc=canvas.getGraphicsContext2D();
		gc.setStroke(edgeColor);
		gc.setFill(nodeColor);
		gc.setLineWidth(edgeW);
		
		for(Edge e:Graph.edges) {
			if(e.nodeFrom().selected && e.nodeTo().selected) gc.setStroke(selectedColorEdge);
			else if(e.nodeFrom().formatted && e.nodeTo().formatted) gc.setStroke(formatColorEdge);
			else gc.setStroke(edgeColor);
			
			double movf=e.nodeFrom().modified?(e.nodeFrom().modifiedS*zoomFactor/2):nodeR/2;
			double movt=e.nodeTo().modified?(e.nodeTo().modifiedS*zoomFactor/2):nodeR/2;
		//	gc.beginPath();
		//	gc.moveTo(e.nodeFrom().x+movf, e.nodeFrom().y+movf);
		//	gc.lineTo(e.nodeTo().x+movt, e.nodeTo().y+movt);	
		//	gc.stroke();
		//	gc.closePath();
			gc.strokeLine(e.nodeFrom().x+movf, e.nodeFrom().y+movf, e.nodeTo().x+movt, e.nodeTo().y+movt);
		}
		
		for(Node n:nodes) {
			if(n.formatted) gc.setFill(formatColorNode);
			else if(n.selected) gc.setFill(selectedColorNode);
			else if(n.modified) gc.setFill(n.modifiedC);
			else gc.setFill(nodeColor);
			
			if(n.modified) {
				gc.fillOval(n.x, n.y, n.modifiedS*zoomFactor, n.modifiedS*zoomFactor);
			}
			else {
				gc.fillOval(n.x, n.y, nodeR, nodeR);
			}
			
			if(n.formatted) gc.setFill(formatColorLabel);
			else if(n.selected) gc.setFill(selectedColorLabel);
			else gc.setFill(labelColor);
			gc.fillText(n.name(), n.x, n.y);
		}
	}
	
	public static synchronized  void clear() {
		nodes=new LinkedList<Node>();
		edges=new LinkedList<Edge>();		
	}
	
/*	public  synchronized LinkedList<Node> shortestPath(String s1,String s2){	
		return retval;
	}*/
	
	public static void forceAtlas(double width, double height) {
		if(nodes==null) return;
		if(edges==null) return;
		LinkedList<HashSet<Node>> groups=new LinkedList<HashSet<Node>>();
		HashSet<Node> visited=new HashSet<Node>();
		LinkedList<Node> fathers=new LinkedList<>();
		
		for(Node n:nodes) {
			if(visited.contains(n)) continue;
			HashSet<Node> currGroup=new HashSet<>();
			currGroup.add(n);
			fathers.add(n);
			visited.add(n);
			
			LinkedList<Node> pending=new LinkedList<Node>();
			for(Node nn:n.neighbors) {
				if(!visited.contains(nn))
					pending.add(nn);
			}
			
			while(!pending.isEmpty()) {
				Node curr=pending.getLast();
				pending.removeLast();
				
				visited.add(curr);
				currGroup.add(curr);
				for(Node nn:curr.neighbors) {
					if(!visited.contains(nn))
						pending.add(nn);
				}			
			}
			groups.add(currGroup);
			
		}
		
		int numOfGroups=groups.size();
		dist=width/3;
		HashSet<Node> brute=new HashSet<>();
		for(Node n:fathers) {
			brute.add(n);
		}
		
		
		Node n=new Node("hehe");
		n.x=width;
		n.y=height;
		//pola kanvasa
		rearange(n,brute);
		
		for(HashSet<Node> s:groups) {
			n=fathers.getFirst();
			fathers.removeFirst();
			rearange(n,s);
			
			
		}
		
	}
	static double dist=100;
	
	static void rearange(Node father, HashSet<Node> set) {
		double angle=2*Math.PI*Math.random();
		double fx=father.x;
		double fy=father.y;
		
		for(Node n:set) {
			if(n==father) continue;
			n.x=father.x+Math.cos(angle)*dist;
			n.y=father.y+Math.sin(angle)*dist;
			angle+=(2*Math.PI)/set.size();
		}
		
	}
	
	
}


