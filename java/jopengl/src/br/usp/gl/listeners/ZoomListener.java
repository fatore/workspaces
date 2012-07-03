package br.usp.gl.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class ZoomListener extends MouseAdapter implements MouseMotionListener {
	
	public static final float ZOOM_INTESITY = 0.05f;
	public static final float MAX_ZOOM = 10.0f;
	public static final float MIN_ZOOM = 0.0f;
	
	private boolean execute = false;
	private int prevy = 0;
	
	private final float initialZoom;
	
	private float zoom;
	
	public float getZoom() {return zoom;}
	
	public ZoomListener(float initialZoom) {
		
		this.zoom = initialZoom;
		this.initialZoom = initialZoom;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (this.execute) {
			if (e.getY() > this.prevy) {
				this.prevy = e.getY();
				if (zoom < initialZoom * MAX_ZOOM) {
					zoom += ZOOM_INTESITY;
				}
			} else {
				this.prevy = e.getY();
				if (zoom > MIN_ZOOM) {
					zoom -= ZOOM_INTESITY;
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		super.mousePressed(e);
		if (e.getButton() == MouseEvent.BUTTON3) {
			this.prevy = e.getY();
			this.execute = true;
		} else {
			this.execute = false;
		}
	}
}
