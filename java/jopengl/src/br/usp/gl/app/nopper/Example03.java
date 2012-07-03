package br.usp.gl.app.nopper;

import javax.media.opengl.GL3;

import br.usp.gl.core.GLApp;
import br.usp.gl.effects.Texture2D;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Model;
import br.usp.gl.models.Plane;
import br.usp.gl.shaders.ShaderProgram;


public class Example03 extends GLApp  {

	public static final int FPS = 60;
	public static final String SHADERS_FOLDER = "shaders/nopper/3/";
	public static final String TEXTURES_FOLDER = "data/textures/";
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	private Matrix4 modelViewProjectionMatrix;
	
	private Model model;
	
	private Texture2D texture;
	
	public Example03() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		modelViewProjectionMatrix = new Matrix4();

		texture = new Texture2D(TEXTURES_FOLDER + "desert.png", GL3.GL_TEXTURE0, 0);
		
		model = new Plane((float) texture.getImage().getWidth() / 2.0f, 
				(float) texture.getImage().getHeight() / 2.0f);
	}

	@Override
	public void init() {
		
		shaderProgram.init(gl);
		shaderProgram.bind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		modelViewProjectionMatrix.init(gl, 
				shaderProgram.getUniformLocation("u_modelViewProjectionMatrix"));
		
		texture.init(gl, shaderProgram.getUniformLocation("u_texture"));
		
		model.init(gl, shaderProgram.getAttribLocation("a_position"), 
				texture, shaderProgram.getAttribLocation("a_texCoord"));
	}

	@Override
	public void display() {

		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
			
		texture.bind();
		
		model.bind();
		model.draw(GL3.GL_TRIANGLES);
		
		gl.glFlush();
	}

	@Override
	public void reshape(final int x, final int y, final int width, final int height) {
		
		gl.glViewport(0, 0, width, height);
		
		// Create the model matrix.
		modelMatrix.loadIdentity();
		
		// Create the view matrix.
		viewMatrix.loadIdentity();
		viewMatrix.lookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// Create a orthogonal projection matrix.
		projectionMatrix.loadIdentity();
		projectionMatrix.ortho(
				-(float) width / 2.0f, (float) width / 2.0f,
				-(float) height / 2.0f, (float) height / 2.0f,
				-1.0f, 100.0f);
		
		// MVP = P * V * M (M is identity).
		modelViewProjectionMatrix.loadIdentity();
		modelViewProjectionMatrix.multiply(projectionMatrix, viewMatrix);
		
		modelViewProjectionMatrix.bind();
	}

	@Override
	public void dispose() {
		
		model.dispose();
	}
	
	public static void main(final String args[]) {

		Example03 app = new Example03();
		app.run(app.getClass().getName(), FPS);
	}
}