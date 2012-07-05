package br.usp.pf.gl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;

import br.usp.pf.gl.core.Triangle;
import br.usp.pf.gl.core.Vertex;

public class CallBacks extends GLJPanelInteractive {

    private ProjectionLoader l;
    private Triangle[] triangles;
    private int shadeModel;

    // load mode -> 1 = smooth, 0 = normal
    public CallBacks(GLCapabilities glcaps, String filename, int shadeModel) throws Exception {
        super(glcaps);

        this.shadeModel = shadeModel;

        l = new ProjectionLoader();
        if (l.loadVertices(filename) < 1) {
            System.exit(1);
        }

        int loadMode = 0;
        if (shadeModel == GL.GL_SMOOTH) {
            loadMode = 1;
        }

        this.triangles = l.createTriangles(loadMode);
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        
        gl.glShadeModel(shadeModel);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.6f * 128.0f);

        lighting(drawable);
    }

    public void display(GLAutoDrawable drawable) {
        displaySmooth(drawable);
    }

    private void displayFlat(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        for (Triangle t : triangles) {
            gl.glBegin(GL.GL_TRIANGLES);
            float[] nv = t.getNormalVector();
            gl.glNormal3f(-nv[0], -nv[1], -nv[2]);
            for (Vertex v : t.getVertices()) {
            	setMaterial(drawable, v);
                gl.glVertex3f(v.getX(), v.getEnergy(), v.getY());
            }
            gl.glEnd();
        }

        gl.glFlush();
    }

    private void displaySmooth(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        for (Triangle t : triangles) {
            gl.glBegin(GL.GL_TRIANGLES);
            for (Vertex v : t.getVertices()) {
                float[] nv = v.getNormalVector();
                setMaterial(drawable, v);
                gl.glNormal3f(-nv[0], -nv[1], -nv[2]);
                gl.glVertex3f(v.getX(), v.getEnergy(), v.getY());
            }
            gl.glEnd();
        }

        gl.glFlush();
    }

    private void lighting(GLAutoDrawable glad) {
        GL gl = glad.getGL();

        float[] luzAmbiente = {0.15f, 0.15f, 0.15f, 1.0f};
        float[] luzDifusa = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
        float[] luzEspecular = new float[]{0.27f, 0.27f, 0.27f, 1.0f};
        float[] posicaoLuz = new float[]{
            GLJPanelInteractive.WINDOW_SIZE * 5.0f,
            GLJPanelInteractive.WINDOW_SIZE * 5.0f,
            GLJPanelInteractive.WINDOW_SIZE * 5.0f,
            1.0f
        };

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, luzDifusa, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, luzEspecular, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, posicaoLuz, 0);

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
    }
    
    private void setMaterial(GLAutoDrawable glad, Vertex v) {
        GL gl = glad.getGL();
        
        float[] mat = v.color.getColorComponents(null);
        
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat, 0);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}