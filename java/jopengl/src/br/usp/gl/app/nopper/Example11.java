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


public class Example11 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/11/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	public static final String MODELS_FOLDER = "data/models/";
	
	public static final float CIRCLE_RADIUS = 5.0f;
	
	private ShaderProgram shaderProgram;
	private ShaderProgram backgroundProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	
	private Matrix4 viewProjectionMatrix;
	
	private Matrix3 normalMatrix;
	
	private Effect cubeMap;
	
	private Model model;
	
	private float angle = 0.0f;
	
	public Example11() {
		
		super();
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		backgroundProgram = new ShaderProgram(SHADERS_FOLDER + "bg/");
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		
		viewProjectionMatrix = new Matrix4();
		
		normalMatrix = new Matrix3();

		cubeMap = new CubeMap(TEXTURES_FOLDER + "water_", "png", true, 
				GL3.GL_TEXTURE0, 0, GL3.GL_CLAMP_TO_EDGE);

		model = new Cube(0.5f);
	}

	@Override
	public void init() {
		
		shaderProgram.init(gl);
		shaderProgram.bind();
		
		backgroundProgram.init(gl);
		backgroundProgram.bind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		
		projectionMatrix.init(gl, shaderProgram.getUniformLocation("u_projectionMatrix"));
		viewProjectionMatrix.init(gl, shaderProgram.getUniformLocation("u_modelViewMatrix"));
		normalMatrix.init(gl, shaderProgram.getUniformLocation("u_normalMatrix"));
		cubeMap.init(gl, shaderProgram.getUniformLocation("u_cubemap"));
		
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
		
		shaderProgram.dispose();
		backgroundProgram.dispose();
		
		model.dispose();
	}
	
	public static void main(final String args[]) {

		Example11 app = new Example11();
		app.run(app.getClass().getName(), FPS);
	}
}