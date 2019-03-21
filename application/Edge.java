package application;

import java.io.Serializable;

public class Edge implements Serializable {
	private Node nodeFrom;
	private Node nodeTo;
	
	public Node nodeFrom() {
		return nodeFrom;
	}
	
	public Node nodeTo() {
		return nodeTo;
	}
	
	public Edge(Node nf,Node nt) {
		nodeFrom=nf;
		nodeTo=nt;
	}
	
	public boolean connects(Node n1,Node n2) {
		if(n1==n2) return false;
		if(n1==nodeFrom || n1==nodeTo) 
			if(n2==nodeFrom || n2==nodeTo) return true;
		return false;
	}
	
	public Node myNeighbor(Node n) {
		if(n==nodeFrom) return nodeTo;
		if(n==nodeTo) return nodeFrom;
		return null;
	}
	
	public boolean equals(Edge e) {
		if(nodeFrom==e.nodeFrom)
			if(nodeTo==e.nodeTo) return true;
		if(nodeFrom==e.nodeTo)
			if(nodeTo==e.nodeFrom) return true;
		return false;
	}
	
}
