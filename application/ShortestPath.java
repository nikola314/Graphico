package application;

import java.util.LinkedList;

public class ShortestPath {
	int[][] matrix;
	int size;
	static class DNode{
		Node node;
		int index;
		public DNode(Node n, int i) {
			index=i;
			node=n;
		}
	}
	
	LinkedList<DNode> dList=new LinkedList<DNode>();
	int[] dist;
	boolean[] sptSet;
	int[] parent;
	
	public ShortestPath(LinkedList<Node> nodes) {
		matrix=new int[nodes.size()][nodes.size()];
		int i=0;
		size=nodes.size();
		for(Node n:nodes) {
			dList.add(new DNode(n,i++));
		}		
		//proci i napraviti pocetnu matricu	
		for(DNode n:dList) {
			for(Node nd:n.node.neighbors) {
				for(DNode dn:dList) {
					if(dn.node.name().equals(nd.name()))
						matrix[n.index][dn.index]=1;
				}
			}		
		}
		dist=new int[size]; 
		sptSet=new boolean[size]; 
		parent=new int[size];
	}

	
	public LinkedList<Node> findPath(Node n1,Node n2){
		if(n1==null || n2==null) return null;
		LinkedList<Node> path=new LinkedList<Node>();
		DNode d1=null,d2=null;
		for(DNode d:dList) {
			if(d.node.name().equals(n1.name())) d1=d;
			if(d.node.name().equals(n2.name())) d2=d;
		}
		if(d1==null || d2==null) return null;
		dijkstra(matrix,d1.index);
		if(dist[d2.index]>dList.size()) return null;
		//ubacivanje putanje u listu
		int j=d2.index;
		while(true) {
			if(parent[j]==-1) break;
			for(DNode d:dList) {
				if(d.index==j) {
					path.add(d.node);
					break;
				}
			}
			j=parent[j];
		}
		return path;
	}

	
	int minDistance(int dist[], boolean sptSet[]){	 
		int min = Integer.MAX_VALUE, min_index=0;
		for (int v = 0; v < size; v++)
			if (sptSet[v] == false && dist[v] <= min) {
				min = dist[v]; min_index = v;
			}
	
		return min_index;
	}

	void dijkstra(int graph[][], int src){
	
		for (int i = 0; i < size; i++)
		{
		    parent[0] = -1;
		    dist[i] = Integer.MAX_VALUE;
		    sptSet[i] = false;
		}
		
		dist[src] = 0;
	
		for (int count = 0; count < size - 1; count++){	
		    int u = minDistance(dist, sptSet);	
		    sptSet[u] = true;	
		    for (int v = 0; v < size; v++)
		        if (!sptSet[v] && (graph[u][v]!=0) && dist[u] + graph[u][v] < dist[v])
		        {
		            parent[v] = u;
		            dist[v] = dist[u] + graph[u][v];
		        } 
		}
	}
	
	
	
	
}
