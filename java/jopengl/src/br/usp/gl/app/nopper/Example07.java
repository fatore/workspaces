package br.usp.gl.app.nopper;

import java.util.Calendar;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import br.usp.gl.core.GLApp;
import br.usp.gl.core.Light;
import br.usp.gl.core.Material;
import br.usp.gl.effects.Texture2D;
import br.usp.gl.matrices.Matrix3;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Model;
import br.usp.gl.models.Plane;
import br.usp.gl.shaders.ShaderProgram;


public class Example07 extends GLApp {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/7/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	public static final String MODELS_FOLDER = "data/models/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	
	private Matrix4 modelViewMatrix;
	
	private Matrix3 normalMatrix;
	
	private Light light;
	
	private Material material;

	private Texture2D texture;
	private Texture2D normalMap;
	
	private Model model;
	
	private float angle = 0.0f;
	
	public Example07() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		
		modelViewMatrix = new Matrix4();
		
		normalMatrix = new Matrix3();
		
		light = new Light(
				new float[]{1.0f, 1.0f, 1.0f},
				new float[]{0.3f, 0.3f, 0.3f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);

		material = new Material(
				new float[]{0.0f, 0.0f, 1.0f, 1.0f},
				new float[]{0.0f, 0.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 20.0f);
		
		texture = new Texture2D(TEXTURES_FOLDER + "rock_color.png", GL3.GL_TEXTURE0, 0);
		normalMap = new Texture2D(TEXTURES_FOLDER + "rock_normal.png", GL3.GL_TEXTURE1, 1);
		
		model = new Plane(1.5f);
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
		
		light.init(gl, shaderProgram.getUniformLocation("u_lightDirection"),
				shaderProgram.getUniformLocation("u_light.ambientColor"),
				shaderProgram.getUniformLocation("u_light.diffuseColor"),
				shaderProgram.getUniformLocation("u_light.specularColor"));
		
		material.init(gl, shaderProgram.getUniformLocation("u_material.ambientColor"),
				shaderProgram.getUniformLocation("u_material.diffuseColor"),
				shaderProgram.getUniformLocation("u_material.specularColor"),
				shaderProgram.getUniformLocation("u_material.specularExponent"));
		
		texture.init(gl, shaderProgram.getUniformLocation("u_texture"));
		normalMap.init(gl, shaderProgram.getUniformLocation("u_normalMap"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"),
				shaderProgram.getAttribLocation("a_normal"), 
				shaderProgram.getAttribLocation("a_tangent"), 
				shaderProgram.getAttribLocation("a_bitangent"), 
				texture, shaderProgram.getAttribLocation("a_texCoord"));
		
		// View Matrix
		viewMatrix.loadIdentity();
		viewMatrix.lookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		normalMatrix.extract(viewMatrix);
		normalMatrix.bind();
		
		// MV = V * M (M is identity)
		modelMatrix.loadIdentity();
		modelViewMatrix.multiply(viewMatrix, modelMatrix);
		modelViewMatrix.bind();
	}

	@Override
	public void display() {

		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		light.setDirOrPos(new float[]{1,1,1});
		light.incDirection(new float[] {(float) (Math.cos(angle)), 0, 0});
		
		light.setDirOrPos(Matrix4.multiplyVector3(viewMatrix.getMatrix(), light.getDirOrPos()));
		
		light.bind();
		material.bind();
		texture.bind();
		normalMap.bind();
		
		model.bind();
		model.draw(GL3.GL_TRIANGLES);
		
		gl.glFlush();
		
		update();
	}
	
	private void update() {
		
		long currentTime = Calendar.getInstance().getTimeInMillis();
		long elapsedTime = currentTime - lastTime;
		
		angle += (elapsedTime / 1000.0f);
		
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

		Example07 app = new Example07();
		app.run(app.getClass().getName(), FPS);
	}
}