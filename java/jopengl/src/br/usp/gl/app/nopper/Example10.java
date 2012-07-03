package br.usp.gl.app.nopper;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLApp;
import br.usp.gl.core.Light;
import br.usp.gl.matrices.Matrix3;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Model;
import br.usp.gl.models.Sphere;
import br.usp.gl.shaders.ShaderProgram;


public class Example10 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/10/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	public static final String MODELS_FOLDER = "data/models/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	private Matrix4 modelViewMatrix;
	
	private Matrix3 normalMatrix;
	
	private Light light;

	private Model model;
	
	public Example10() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		modelViewMatrix = new Matrix4();
		
		normalMatrix = new Matrix3();
		
		light = new Light(
				new float[]{0.0f, 0.5f, 2.0f}, true,
				new float[]{0.3f, 0.3f, 0.3f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);

		model = new Sphere(1.0f, 32);
	}

	@Override
	public void init() {
		
		shaderProgram.init(gl);
		shaderProgram.bind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		
		projectionMatrix.init(gl, shaderProgram.getUniformLocation("u_projectionMatrix"));
		modelViewMatrix.init(gl, shaderProgram.getUniformLocation("u_modelViewMatrix"));
		normalMatrix.init(gl, shaderProgram.getUniformLocation("u_normalMatrix"));
		
		light.init(gl, shaderProgram.getUniformLocation("u_lightPosition"),
				shaderProgram.getUniformLocation("u_light.ambientColor"),
				shaderProgram.getUniformLocation("u_light.diffuseColor"),
				shaderProgram.getUniformLocation("u_light.specularColor"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"),
				shaderProgram.getAttribLocation("a_normal"));
		
		// Initialize with the identity matrix ...
		modelMatrix.loadIdentity();
		
		// Create the view matrix.
		viewMatrix.loadIdentity();
		viewMatrix.lookAt( 0.0f, 0.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// The calculations are done in camera / view space. 
		// So pass the view matrix, which is a rigid body transform.
		normalMatrix.extract(viewMatrix);
		
		// MV = V * M (M is identity)
		modelViewMatrix.multiply(viewMatrix, modelMatrix);
		
		modelViewMatrix.bind();
		normalMatrix.bind();
	}

	@Override
	public void display() {

		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		light.setDirOrPos(new float[]{0.0f, 0.5f, 2.0f});
		light.setDirOrPos(Matrix4.multiplyPoint3(viewMatrix.getMatrix(), light.getDirOrPos()));
		
		light.bind();
		
		model.bind();
		model.draw(GL3.GL_TRIANGLES);
		
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

		Example10 app = new Example10();
		app.run(app.getClass().getName(), FPS);
	}
}