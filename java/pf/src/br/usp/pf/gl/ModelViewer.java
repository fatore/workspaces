/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2010 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Java Wavefront (JWavefront) reader project.
 *
 * JWavefront is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * JWavefront is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * This code was developed by Fernando V. Paulovich <fpaulovich@gmail.com>,
 * member of Visualization, Imaging and Computer Graphics Group
 * (http://www.lcad.icmc.usp.br) at Instituto de Ciencias Matematicas e de
 * Computacao - ICMC - (http://www.icmc.usp.br) of Universidade de Sao Paulo,
 * Sao Carlos/SP, Brazil.
 *
 * You should have received a copy of the GNU General Public License along
 * with JWavefront. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

package br.usp.pf.gl;


import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

/**
 * JWavefront object viewer.
 *
 * @author Fernando V. Paulovich
 */
public class ModelViewer extends GLJPanelInteractive {

    public ModelViewer(GLCapabilities glcaps, JWavefrontModel model) {
        super(glcaps);
        this.model = model;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        //OpenGl Parameters
        GL gl = glad.getGL();
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);

        //compiling the object
        model.compile(glad, JWavefrontModel.WF_MATERIAL | JWavefrontModel.WF_TEXTURE | JWavefrontModel.WF_SMOOTH);

        lighting(glad);
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL gl = glad.getGL();

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); //backgroung color
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        //draw the object
        model.draw(glad);

        gl.glFlush(); //execute all commands
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
    }

    @Override
    public void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1) {
    }

    private void lighting(GLAutoDrawable glad) {
        GL gl = glad.getGL();

        float[] luzAmbiente = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] luzDifusa = new float[]{0.75f, 0.75f, 0.75f, 1.0f};
        float[] luzEspecular = new float[]{0.7f, 0.7f, 0.7f, 1.0f};
        float[] posicaoLuz = new float[]{GLJPanelInteractive.WINDOW_SIZE * 5.0f,
            GLJPanelInteractive.WINDOW_SIZE * 5.0f, GLJPanelInteractive.WINDOW_SIZE * 5.0f, 1.0f
        };

        // Define os parametros da luz de numero 0
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, luzDifusa, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, luzEspecular, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, posicaoLuz, 0);
    }

    public static void main(String args[]) throws IOException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        String filename = "./data/apples.obj";
        JWavefrontModel model = new JWavefrontModel(new File(filename));
        model.unitize();
        model.facetNormals();
        model.vertexNormals(90);
        model.dump(true);

        //creting the projection panel
        GLCapabilities glcaps = new GLCapabilities();
        glcaps.setAccumBlueBits(16);
        glcaps.setAccumGreenBits(16);
        glcaps.setAccumRedBits(16);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        ModelViewer viewer = new ModelViewer(glcaps, model);
        viewer.setOpaque(true);

        frame.getContentPane().add(viewer);
        frame.setVisible(true);
    }

    /**
	 */
    private JWavefrontModel model;
}
