package application;

import javafx.scene.control.MenuItem;

public class OperationList {
	
	private class Elem{
		OperationPerformed info;
		Elem next,prev;
		public Elem(OperationPerformed p) {
			info=p;
			next=prev=null;
		}
	}

	Elem head=null, curr=null;
	public void add(OperationPerformed p) {
		if(head==null || curr==null) head=curr=new Elem(p);
		else {
			curr.next=new Elem(p);
			curr.next.prev=curr;
			curr=curr.next;
		}
	}
	public void undo() {
		if(curr!=null) {
			curr.info.undo();
			curr=curr.prev;
		}
	}
	public void redo() {
		if(curr==null && head==null) return;
		if(curr!=null && curr.next==null) return;
		curr=(curr==null)?head:curr.next;	
		curr.info.redo();
		
	}
	
}
