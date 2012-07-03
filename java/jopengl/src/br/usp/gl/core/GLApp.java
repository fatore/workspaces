package br.usp.gl.core;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;


public abstract class GLApp implements GLEventListener {

	// OpenGL Pipeline Object
	protected GL3 gl;

	// Constants
	public static final int INITIAL_WINDOW_WIDTH = 800;
	public static final int INITIAL_WINDOW_HEIGTH = 600;

	// GLCanvas
	protected GLCanvas glCanvas;
	protected int canvasWidth;
	protected int canvasHeight;
	protected float aspect;


	// Misc
	protected long lastTime;

	public GLApp() {

		GLProfile profile = GLProfile.getDefault();

		GLCapabilities glcaps = new GLCapabilities(profile);
		glcaps.setAccumBlueBits(16);
		glcaps.setAccumGreenBits(16);
		glcaps.setAccumRedBits(16);
		glcaps.setDoubleBuffered(true);
		glcaps.setHardwareAccelerated(true);

		this.canvasWidth = INITIAL_WINDOW_WIDTH;
		this.canvasHeight = INITIAL_WINDOW_HEIGTH;

		glCanvas = new GLCanvas(glcaps);
		glCanvas.setSize(canvasWidth, canvasHeight);

		glCanvas.addGLEventListener(this);

		lastTime = Calendar.getInstance().getTimeInMillis();
	}

	public void run(String title, int fps) {

		Frame frame = new Frame(title);
		frame.add(this.getGLCanvas());
		frame.setSize(this.getGLCanvas().getWidth(), this.getGLCanvas().getHeight());

		final AnimatorBase animator;
		if (fps > 0) {
			animator = new FPSAnimator(this.getGLCanvas(), fps);
		} else {
			animator = new Animator(this.getGLCanvas());
		}
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				new Thread(new Runnable() {
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});
		frame.setVisible(true);
		this.getGLCanvas().requestFocusInWindow();

		animator.start();
	}

	public GLCanvas getGLCanvas() {return glCanvas;}
	public void setGlCanvas(GLCanvas glCanvas) {this.glCanvas = glCanvas;}

	@Override
	public void init(GLAutoDrawable drawable) {

		gl = drawable.getGL().getGL3();

		System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

		init();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

		dispose();
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		display();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		if (height == 0) {height = 1;}
		
		canvasWidth = width; 
		canvasHeight = height;
		
		aspect = (float) canvasWidth / canvasHeight;
		
		reshape(x, y, canvasWidth, canvasHeight);
	}

	public abstract void init();
	public abstract void display();
	public abstract void reshape(int x, int y, int width, int height);
	public abstract void dispose();
}
