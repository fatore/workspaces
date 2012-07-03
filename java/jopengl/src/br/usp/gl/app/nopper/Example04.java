package br.usp.gl.app.nopper;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLApp;
import br.usp.gl.core.Light;
import br.usp.gl.matrices.Matrix3;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Cube;
import br.usp.gl.models.Model;
import br.usp.gl.shaders.ShaderProgram;
import br.usp.gl.util.Maths;


public class Example04 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/4/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	private Matrix4 modelViewProjectionMatrix;
	
	private Matrix3 normalMatrix;
	
	private Light light;

	private Model model;
	
	public Example04() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		modelViewProjectionMatrix = new Matrix4();
		
		normalMatrix = new Matrix3();
		
		light = new Light(
				new float[]{1.0f, 1.0f, 1.0f},
				new float[]{0.0f, 0.0f, 0.0f, 1.0f},
				new float[]{1.0f, 0.0f, 0.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);

		model = new Cube(0.5f);
	}

	@Override
	public void init() {
		
		shaderProgram.init(gl);
		shaderProgram.bind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		
		modelViewProjectionMatrix.init(gl, 
				shaderProgram.getUniformLocation("u_modelViewProjectionMatrix"));
		
		normalMatrix.init(gl, shaderProgram.getUniformLocation("u_normalMatrix"));
		
		light.init(gl, shaderProgram.getUniformLocation("u_lightDirection"),
				shaderProgram.getUniformLocation("uLightAmbientColor"),
				shaderProgram.getUniformLocation("u_color"),
				shaderProgram.getUniformLocation("uLightSpecularColor"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"),
				shaderProgram.getAttribLocation("a_normal"));
	}

	@Override
	public void display() {

		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		light.bind();
			
		model.bind();
		model.draw(GL3.GL_TRIANGLES);
		
		gl.glFlush();
	}

	@Override
	public void reshape(final int x, final int y, final int width, final int height) {
		
		gl.glViewport(0, 0, width, height);
		
		// Initialize with the identity matrix ...
		modelMatrix.loadIdentity();
		// ... and rotate the cube at two axes that we do see some sides.
		modelMatrix.rotate(Maths.degToRad(45), new float[]{1,0,0});
		modelMatrix.rotate(Maths.degToRad(45), new float[]{0,1,0});
		
		// This model matrix is a rigid body transform. 
		// So no need for the inverse, transposed matrix.
		normalMatrix.extract(modelMatrix);
		
		// Create the view matrix.
		viewMatrix.loadIdentity();
		viewMatrix.lookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// Create a perspective projection matrix.
		projectionMatrix.loadIdentity();
		projectionMatrix.perspective(40.0f, aspect, 1.0f, 100.0f);
		
		// MVP = P * V * M.
		modelViewProjectionMatrix.loadIdentity();
		// P * V.
		modelViewProjectionMatrix.multiply(projectionMatrix, viewMatrix);
		// PV * M.
		modelViewProjectionMatrix.multiply(modelMatrix);
		
		modelViewProjectionMatrix.bind();
		normalMatrix.bind();
	}

	@Override
	public void dispose() {
		
		model.dispose();
	}
	
	public static void main(final String args[]) {

		Example04 app = new Example04();
		app.run(app.getClass().getName(), FPS);
	}
}