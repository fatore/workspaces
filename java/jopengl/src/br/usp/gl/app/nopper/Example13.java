package br.usp.gl.app.nopper;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;

import br.usp.gl.core.GLApp;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Model;
import br.usp.gl.models.Plane;
import br.usp.gl.shaders.ShaderProgram;


public class Example13 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/13/";
	public static final String TEXTURES_FOLDER = "resources/textures/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	private Matrix4 modelViewMatrix;
	
	private Model model;
	
	public Example13() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		modelViewMatrix = new Matrix4();
		
		model = new Plane(1.0f);
		model.setIndices(new int[]{0,1,3,2});
	}

	@Override
	public void init() {
		
		shaderProgram.init(gl);
		shaderProgram.bind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		
		gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 4);
		
		projectionMatrix.init(gl, 
				shaderProgram.getUniformLocation("u_projectionMatrix"));
		
		modelViewMatrix.init(gl, 
				shaderProgram.getUniformLocation("u_modelViewMatrix"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"));
		
		// Initialize with the identity matrix ...
		modelMatrix.loadIdentity();
		
		// Create the view matrix.
		viewMatrix.loadIdentity();
		viewMatrix.lookAt( 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// MV = V * M (M is identity)
		modelViewMatrix.multiply(viewMatrix, modelMatrix);
		
		modelViewMatrix.bind();
	}

	@Override
	public void display() {

		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		
		model.bind();
		model.draw(GL4.GL_PATCHES);
		
		gl.glFlush();
	}

	@Override
	public void reshape(final int x, final int y, final int width, final int height) {
		
		gl.glViewport(0, 0, width, height);
		
		projectionMatrix.loadIdentity();
		projectionMatrix.perspective(40f, aspect, 1.0f, 100.0f);
		projectionMatrix.bind();
	}

	@Override
	public void dispose() {
		
		model.dispose();
	}
	
	public static void main(final String args[]) {

		Example13 app = new Example13();
		app.run(app.getClass().getName(), FPS);
	}
}