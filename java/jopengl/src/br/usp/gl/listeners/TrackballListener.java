package br.usp.gl.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import br.usp.gl.matrices.Matrix4;

public class TrackballListener extends MouseAdapter implements MouseMotionListener {
	
	private final static int EPS2 = 25;
	
	private boolean execute;
	
	private int startX;
    private int startY;
    
    private int prevX;
    private int prevY;
    
    private boolean spin = false;
    
    private Matrix4 rotationMatrix;
    
    private Quaternion lastQuaternion;
    private Quaternion currentQuaternion;
    
    public TrackballListener() {
    	
    	rotationMatrix = new Matrix4();
    	
    	lastQuaternion = new Quaternion();
    	currentQuaternion = new Quaternion();
    	
    	lastQuaternion.build(0.0f, 0.0f, 0.0f, 0.0f);
    	currentQuaternion.build(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Matrix4 getRotationMatrix() {
    	
        if (this.execute) {
        	rotationMatrix.setMatrix(currentQuaternion.buildMatrix());
            if (spin) {
                currentQuaternion.addQuats(lastQuaternion.getQuaternion(),
                		currentQuaternion.getQuaternion());
            }
        }
        return rotationMatrix;
    }
	
	@Override
    public void mouseReleased(MouseEvent evt) {
		
        if (execute) {
            int dx = startX - evt.getX();
            int dy = startY - evt.getY();
            spin = (dx * dx + dy * dy > EPS2);
            execute = false;
        }
    }
	
    @Override
    public void mousePressed(MouseEvent evt) {
    	
        if (evt.getButton() == MouseEvent.BUTTON1) {
            execute = true;
            startX = prevX = evt.getX();
            startY = prevY = evt.getY();
            spin = false;
        } else {
            execute = false;
        }
    }
    
    @Override
	public void mouseDragged(MouseEvent evt) {
    	
		if (execute) {
			int aWidth = evt.getComponent().getSize().width;
			int aHeight = evt.getComponent().getSize().height;
			int currX = evt.getX();
			int currY = evt.getY();

			lastQuaternion.build((float) (2.0f * prevX - aWidth) / (float) aWidth,
					(float) (aHeight - 2.0f * prevY) / (float) aHeight,
					(float) (2.0f * currX - aWidth) / (float) aWidth,
					(float) (aHeight - 2.0f * currY) / (float) aHeight);
			
			currentQuaternion.addQuats(lastQuaternion.getQuaternion(),
					currentQuaternion.getQuaternion());
			prevX = currX;
			prevY = currY;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
