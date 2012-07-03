package br.usp.gl.app.nopper;

import java.util.Calendar;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLApp;
import br.usp.gl.effects.CubeMap;
import br.usp.gl.effects.Effect;
import br.usp.gl.matrices.Matrix3;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Cube;
import br.usp.gl.models.Model;
import br.usp.gl.shaders.ShaderProgram;
import br.usp.gl.util.Maths;


public class Example08 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/8/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	public static final String MODELS_FOLDER = "data/models/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	
	private Matrix4 modelViewMatrix;
	
	private Matrix3 inverseViewMatrix;
	
	private Matrix3 normalMatrix;
	
	private Effect cubeMap;
	
	private Model model;
	
	private float angle = 0.0f;
	
	public Example08() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		
		modelViewMatrix = new Matrix4();
		inverseViewMatrix = new Matrix3();
		
		normalMatrix = new Matrix3();

		cubeMap = new CubeMap(TEXTURES_FOLDER + "water_", "png", true, GL3.GL_TEXTURE0, 0);

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
		
		projectionMatrix.init(gl, shaderProgram.getUniformLocation("u_projectionMatrix"));
		
		modelViewMatrix.init(gl, shaderProgram.getUniformLocation("u_modelViewMatrix"));
		inverseViewMatrix.init(gl, shaderProgram.getUniformLocation("u_inverseViewMatrix"));
		
		normalMatrix.init(gl, shaderProgram.getUniformLocation("u_normalMatrix"));
		
		cubeMap.init(gl, shaderProgram.getUniformLocation("u_cubemapTexture"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"),
				shaderProgram.getAttribLocation("a_normal"));
		
		// View Matrix
		viewMatrix.loadIdentity();
		viewMatrix.lookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
	}

	@Override
	public void display() {

		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

		// Calculate the model matrix ...
		modelMatrix.loadIdentity();
		// ... by rotating the cube.
		modelMatrix.rotate(Maths.degToRad(15), new float[]{1,0,0});
		modelMatrix.rotate(Maths.degToRad(angle), new float[]{0,1,0});
		
		// MV = V * M
		modelViewMatrix.multiply(viewMatrix, modelMatrix);
		modelViewMatrix.bind();
		
		// Again, extract the normal matrix. 
		// Remember, so far the model view matrix (rotation part) is orthogonal.
		normalMatrix.extract(modelViewMatrix);
		normalMatrix.bind();
		
		// Extract the rotation part of the view matrix.
		inverseViewMatrix.extract(viewMatrix);
		
		// Pass this matrix to the shader with the transpose flag. 
	    // As the view matrix is orthogonal, the transposed is the inverse view matrix.
		inverseViewMatrix.bind(true);
		
		cubeMap.bind();
		
		model.bind();
		model.draw(GL3.GL_TRIANGLES);
		
		gl.glFlush();
		
		update();
	}
	
	private void update() {
		
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long elapsedTime = currentTime - lastTime;
		
		angle += 20.0f * (elapsedTime / 1000.0f);
		
		lastTime = currentTime;
	}

	@Override
	public void reshape(final int x, final int y, final int width, final int height) {
		
		gl.glViewport(0, 0, width, height);
		
		// Projection Matrix
		projectionMatrix.loadIdentity();
		projectionMatrix.perspective(40f, aspect, 1.0f, 100.0f);
		projectionMatrix.bind();
	}

	@Override
	public void dispose() {
		
		model.dispose();
	}
	
	public static void main(final String args[]) {

		Example08 app = new Example08();
		app.run(app.getClass().getName(), FPS);
	}
}