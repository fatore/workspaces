package br.usp.gl.app.nopper;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;

import br.usp.gl.core.Camera;
import br.usp.gl.core.GLApp;
import br.usp.gl.core.Light;
import br.usp.gl.effects.Texture2D;
import br.usp.gl.matrices.Matrix4;
import br.usp.gl.models.Model;
import br.usp.gl.models.Plane;
import br.usp.gl.shaders.ShaderProgram;


public class Example14 extends GLApp {

	private static final int FPS = 60;
	private static final String SHADERS_FOLDER = "shaders/nopper/14/";
	private static final String TEXTURES_FOLDER = "resources/textures/";
	
	private static final String HEIGHT_MAP = "grand_canyon_height.tga";
	private static final String COLOR_MAP = "grand_canyon_color.tga";
	private static final String NORMAL_MAP = "grand_canyon_normal.tga";
	
	private static final float TURN_DURATION = 20.0f;
	private static final float TURN_RADIUS = 6000.0f;
	private static final float HORIZONTAL_PIXEL_SPACING = 60.0f;
	private static final float VERTICAL_PIXEL_RANGE = 10004.0f;
	private static final float METERS_TO_VIRTUAL_WORLD_SCALE = 5.0f;
	private static final int MINIMUM_DETAIL_LEVEL = 4;
	private static final int DETAIL_LEVEL_FIRST_PASS = 2;
	private static final float FOV_RADIUS = 10000.0f;
	private static final int QUADRANT_STEP = 2;
	
	private ShaderProgram shaderProgram;
	
	private Matrix4 modelMatrix;
	private Matrix4 viewMatrix;
	private Matrix4 projectionMatrix;
	private Matrix4 modelViewMatrix;
	
	private Light light;
	
	private Camera activeView;
	private Camera topView;
	private Camera personView;
	
	private Texture2D normalMap;
	private Texture2D heightMap;
	
	private float sMapExtend;
	private float tMapExtend;
	
	private float sMaxDetailLevel;
	private float tMaxDetailLevel;
	
	private int sNumPoints;
	private int tNumPoints;
	
	private float overallMaxDetailLevel;
	
	private float detailStep;
	
	private Model model;
	
	public Example14() {
		
		shaderProgram = new ShaderProgram(SHADERS_FOLDER);
		
		modelMatrix = new Matrix4();
		viewMatrix = new Matrix4();
		projectionMatrix = new Matrix4();
		modelViewMatrix = new Matrix4();
		
		light = new Light(
				new float[]{1.0f, 1.0f, 1.0f}, false,
				new float[]{0.3f, 0.3f, 0.3f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f},
				new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);
		
		topView = new Camera(
				new float[]{0.0f, 30000.0f * METERS_TO_VIRTUAL_WORLD_SCALE, 
						0.0f, 1.0f}, 
				new float[]{0.0f, -1.0f, 0.0f},
				new float[]{0.0f, 0.0f, -1.0f},
				40.0f);
		
		personView = new Camera(
				new float[]{0.0f, 4700.0f * METERS_TO_VIRTUAL_WORLD_SCALE, 
						0.0f, 1.0f}, 
				new float[]{0.0f, 0.0f, -1.0f},
				new float[]{0.0f, 1.0f, 0.0f},
				60.0f);
		
		activeView = personView;
		
		normalMap = new Texture2D(TEXTURES_FOLDER + NORMAL_MAP, GL4.GL_TEXTURE0, 
				0, GL4.GL_CLAMP_TO_EDGE, GL4.GL_NEAREST, GL4.GL_NEAREST);
		heightMap = new Texture2D(TEXTURES_FOLDER + HEIGHT_MAP, GL4.GL_TEXTURE0,
				0, GL4.GL_CLAMP_TO_EDGE, GL4.GL_NEAREST, GL4.GL_NEAREST);
		
		sMapExtend = normalMap.getImage().getWidth();
		tMapExtend = normalMap.getImage().getHeight();
		
		sMaxDetailLevel = (float) Math.floor(Math.log(sMapExtend) / Math.log(2.0));
		tMaxDetailLevel = (float) Math.floor(Math.log(tMapExtend) / Math.log(2.0));
		
		overallMaxDetailLevel = (sMaxDetailLevel > tMaxDetailLevel) ?
				sMaxDetailLevel : tMaxDetailLevel;
		
		detailStep = (float) Math.pow(2.0f, overallMaxDetailLevel - MINIMUM_DETAIL_LEVEL);
		
		sNumPoints = (int) (Math.ceil(sMapExtend / detailStep) - 1);
		tNumPoints = (int) (Math.ceil(tMapExtend / detailStep) - 1);
		
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

		Example14 app = new Example14();
		app.run(app.getClass().getName(), FPS);
	}
}