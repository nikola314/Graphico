package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class Format {
	public static void init() {
		csvReader=new CSV();
		gmlReader=new GML();
	}
	static CSV csvReader=null;
	static GML gmlReader=null;
	
	public static void readFile(String path) {
		try {
		BufferedReader br;
		br = new BufferedReader(new FileReader(path));
		String line;	
		line = br.readLine();	
		String[] f=path.split("\\.");	
		if(f[1].equals("csv")) {		
			if(line.charAt(0)==';') {
			}
			else {
				csvReader.generate(br,line);
			}			
		}
		else if(f[1].equals("gml")) {
			gmlReader.generate(br, line);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	}
	
	public abstract void generate(BufferedReader b,String s);
}

class CSV extends Format{

	@Override
	public void generate(BufferedReader br,String l) {
		String line=l;
		while(line!=null) {			
			String[] nodes =line.split(";");
			String n1=nodes[0];
			for(int i=1;i<nodes.length;i++) {
				Graph.addEdge(n1,nodes[i]);
			}
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

class GML extends Format{

	@Override
	public void generate(BufferedReader br,String l) {
		String line=l;
		StringBuilder sb=new StringBuilder();
		while(line!=null) {				
			sb.append(line);			
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String[] s=sb.toString().split("\\[|\\]");

		for(int i=0;i<s.length;i++) {
			String temp=s[i];
			if(temp.equals("graph")) continue;
			if(temp.equals("edge")) {
				String[] t=s[++i].split("source |target ");
				Graph.addEdge(t[1], t[2]);
			}
		//	if(temp.equals("node")) continue;					
		}
	}
	
}

