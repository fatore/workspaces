package br.usp.gl.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeysListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {
		System.err.println(e.getKeyCode());
		switch(e.getKeyCode())
		{
		case 33: // page up
			
			break;
			
		case 34: // page down
			
			break;
			
		case 37: // left arrow
			
			break;
			
		case 38: // up arrow
			
			break;
			
		case 39: // right arrow
			
			break;
			
		case 40: // down arrow
			
			break;
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyCode()) {
		case 70:
//			cube.filter = (cube.filter == 2) ? 0 : cube.filter + 1;
			break;
		}
	}
}
