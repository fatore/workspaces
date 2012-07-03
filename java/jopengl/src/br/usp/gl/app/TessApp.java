package br.usp.gl.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLOrthoApp;
import br.usp.gl.core.Light;
import br.usp.gl.models.Icosahedron;
import br.usp.gl.models.Model;


public class TessApp extends GLOrthoApp implements KeyListener {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/tess/";
	public static final String FUNNEL_FILE = "data/models/funnel.json";
	public static final String TEA_POT_FILE = "data/models/teapot.json";
	
	private Light light;
	
	private float tessInnerLevel;
	private float tessOuterLevel;
	
	private int tessInnerLevelHandle;
	private int tessOuterLevelHandle;
	
	private Model model;
	
	public TessApp() {
		
		super(SHADERS_FOLDER);

		this.glCanvas.addKeyListener(this);
		
		model = new Icosahedron();
		
		light = new Light(
				new float[]{1.0f, 1.0f, 1.0f},
				new float[]{0.3f, 0.3f, 0.3f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);
		
		tessInnerLevel = 1;
		tessOuterLevel = 1;
	}

	@Override
	public void init() {
		
		super.init();

		gl.glClearColor(0.7f, 0.6f, 0.5f, 0.0f);
		
		gl.glEnable(GL3.GL_DEPTH_TEST);
		
		gl.glClearDepth(1.0f);
		
		gl.glPatchParameteri(GL3.GL_PATCH_VERTICES, 3);
		
		light.init(gl, shaderProgram.getUniformLocation("uLightDirection"),
				shaderProgram.getUniformLocation("uLightAmbientColor"),
				shaderProgram.getUniformLocation("uLightDiffuseColor"),
				shaderProgram.getUniformLocation("uLightSpecularColor"));
		
		
		tessInnerLevelHandle = shaderProgram.getUniformLocation("uTessInnerLevel");
		tessOuterLevelHandle = shaderProgram.getUniformLocation("uTessOuterLevel");
		
		model.init(gl, shaderProgram.getAttribLocation("aVertexPosition"));
		model.bind();
	}

	@Override
	public void display() {
		
		super.display();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glUniform1f(tessInnerLevelHandle, tessInnerLevel);
	    gl.glUniform1f(tessOuterLevelHandle, tessOuterLevel);
	    
	    light.bind();
		
		gl.glUniform3f(light.getAmbientColorHandle(), 0.04f, 0.04f, 0.04f);
	    gl.glUniform3f(light.getDiffuseColorHandle(),0f, 0.75f, 0.75f);
		
	    model.draw(GL3.GL_PATCHES);
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
			tessInnerLevel -= (tessInnerLevel > 1) ? 1 : 0;
			break;
			
		case 38: // up arrow
			tessOuterLevel++;
			break;
			
		case 39: // right arrow
			tessInnerLevel++;
			break;
			
		case 40: // down arrow
			tessOuterLevel -= (tessOuterLevel > 1) ? 1 : 0;
			break;
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public static void main(final String args[]) {

		TessApp app = new TessApp();
		app.run("Tess App", FPS);
	}
}