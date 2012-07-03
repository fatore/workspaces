package br.usp.gl.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLOrthoApp;
import br.usp.gl.core.Light;
import br.usp.gl.core.Material;
import br.usp.gl.models.JsonModel;
import br.usp.gl.models.Model;


public class SimpleApp extends GLOrthoApp implements KeyListener {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/simple/";
	public static final String FUNNEL_FILE = "data/models/funnel.json";
	public static final String TEA_POT_FILE = "data/models/teapot.json";

	private Light light;
	private Material material;
	
	private int pancakeHandle = -1;
	private boolean wireframe = false;
	private boolean pancake = false;
	
	private Model model;
	
	public SimpleApp() {
		
		super(SHADERS_FOLDER);

		this.glCanvas.addKeyListener(this);
		
		model = new JsonModel(TEA_POT_FILE);
		
		light = new Light(
				new float[]{1.0f, 1.0f, 1.0f},
				new float[]{0.3f, 0.3f, 0.3f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);
		
		material = new Material(
				new float[]{0.0f, 0.0f, 1.0f, 1.0f},
				new float[]{0.0f, 0.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 20.0f);
	}

	@Override
	public void init() {
		
		super.init();

		gl.glClearColor(0.7f, 0.6f, 0.5f, 0.0f);
		
		gl.glEnable(GL3.GL_DEPTH_TEST);
		
		light.init(gl, shaderProgram.getUniformLocation("uLightDirection"),
				shaderProgram.getUniformLocation("uLightAmbientColor"),
				shaderProgram.getUniformLocation("uLightDiffuseColor"),
				shaderProgram.getUniformLocation("uLightSpecularColor"));
		
		material.init(gl, shaderProgram.getUniformLocation("uMaterialAmbientColor"),
				shaderProgram.getUniformLocation("uMaterialDiffuseColor"),
				shaderProgram.getUniformLocation("uMaterialSpecularColor"),
				shaderProgram.getUniformLocation("uMaterialSpecularExpoent"));
		
		pancakeHandle = shaderProgram.getUniformLocation("uPancake");
		
		model.init(gl, shaderProgram.getAttribLocation("aVertexPosition"),
				shaderProgram.getAttribLocation("aVertexNormal"));
	}

	@Override
	public void display() {
		
		super.display();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
	    if (wireframe) {
	    	gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
	    } else {
	    	gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL);
	    }
	    
	    gl.glUniform1i(pancakeHandle, (pancake) ? 1 : 0);
	    
	    mvMatrix.bind();
	    
	    light.bind();
	    material.bind();
	    
		model.bind();
	    model.draw(GL3.GL_TRIANGLES);
	}

	@Override
	public void dispose() {
		
		super.dispose();
		
		model.dispose();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
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
		switch (e.getKeyChar()) {
		case 'w':
			wireframe = !wireframe;
			break;
			
		case 'p':
			pancake = !pancake;
			break;

		default:
			break;
		}
		
	}

	public static void main(final String args[]) {

		SimpleApp app = new SimpleApp();
		app.run("Simple App", FPS);
	}
}