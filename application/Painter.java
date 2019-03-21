package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Painter extends Thread {

	private Canvas canvas;
	public boolean done=false;
	private boolean paused=false;
	public Painter(Canvas c) {
		canvas=c;
	}
	
	private synchronized void pause() {
		paused=true;
	}
	private synchronized void unpause() {
		paused=false;
		notifyAll();
	}
	
	public void run() {
		
			synchronized(this) {
				while(paused)
					try {
						wait();
					} catch (InterruptedException e) {e.printStackTrace();}
			}
			
	}
	
}
