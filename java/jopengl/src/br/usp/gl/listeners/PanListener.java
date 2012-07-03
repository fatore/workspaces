package br.usp.gl.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.awt.GLCanvas;


public class PanListener extends MouseAdapter implements MouseMotionListener {
	
	public static final float WINDOW_SIZE = 5;

	private GLCanvas glCanvas;
	
	private boolean execute = false;
	
	private int prevy = 0;
	private int prevx = 0;
	
	private float panX = 0;
	private float panY = 0;
	
	public float getPanX() {return panX;}
	public float getPanY() {return panY;}
	
	public PanListener(GLCanvas glCanvas) {
		
		this.glCanvas = glCanvas;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (this.execute) {
			float diffx = ((float) (e.getX() - this.prevx));
			this.prevx = e.getX();
			panX -= (diffx / glCanvas.getSize().width) * WINDOW_SIZE;

			float diffy = ((float) (e.getY() - this.prevy));
			this.prevy = e.getY();
			panY += (diffy / glCanvas.getSize().height) * WINDOW_SIZE;

		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		super.mousePressed(e);
		if (e.getButton() == MouseEvent.BUTTON2) {
			this.prevx = e.getX();
			this.prevy = e.getY();
			this.execute = true;
		} else {
			this.execute = false;
		}
	}
}
