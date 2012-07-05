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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

/**
 * Panel that add mouse control over a GLJPanel. Left button is a trackball.
 * Middle button is pam, and the right button is zoom.
 *
 * @author Fernando V. Paulovich
 */
public abstract class GLJPanelInteractive extends GLJPanel {

    public static final float WINDOW_SIZE = 5;

    public GLJPanelInteractive() {
        this(null);
    }

    public GLJPanelInteractive(GLCapabilities glcap) {
        super(glcap);
        addMouseListener(new TrackballMouseListener());
        addMouseMotionListener(new TrackballMouseMotionListener());
        addGLEventListener(new GLListener());

        ZoomListener zoomListener = new ZoomListener();
        addMouseMotionListener(zoomListener);
        addMouseListener(zoomListener);

        PanListener panListener = new PanListener();
        addMouseListener(panListener);
        addMouseMotionListener(panListener);
    }

    public abstract void init(GLAutoDrawable glad);

    public abstract void display(GLAutoDrawable glad);

    public abstract void reshape(GLAutoDrawable glad, int x, int y, int w, int h);

    public abstract void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1);

    
    /** return rotation Matrix representing current rotation of trackball
     * @param rotMat
     * @return
     */
    private float[] getRotMatrix(float[] rotMat) {
        if (this.execute) {
            rotMat = buildMatrix(curQuat);
            if (spin) {
                curQuat = addQuats(lastQuat, curQuat);
            }
        }

        return rotMat;
    }

    /*
     * Ok, simulate a track-ball.  Project the points onto the virtual
     * trackball, then figure out the axis of rotation, which is the cross
     * product of P1 P2 and O P1 (O is the center of the ball, 0,0,0)
     * Note:  This is a deformed trackball-- is a trackball in the center,
     * but is deformed into a hyperbolic sheet of rotation away from the
     * center.  This particular function was chosen after trying out
     * several variations.
     *
     * It is assumed that the arguments to this routine are in the range
     * (-1.0 ... 1.0)
     */
    private float[] buildQuaternion(float p1x, float p1y, float p2x, float p2y) {
        float[] a = new float[3]; /* Axis of rotation */
        float phi;  /* how much to rotate about axis */
        float[] p1 = new float[3];
        float[] p2 = new float[3];
        float[] d = new float[3];
        float t;

        if (p1x == p2x && p1y == p2y) {
            /* Zero rotation */
            float[] q = {0.0f, 0.0f, 0.0f, 1.0f};
            return q;
        }

        /*
         * First, figure out z-coordinates for projection of P1 and P2 to
         * deformed sphere
         */
        vset(p1, p1x, p1y, projectToSphere(trackballSize, p1x, p1y));
        vset(p2, p2x, p2y, projectToSphere(trackballSize, p2x, p2y));

        /*
         *  Now, we want the cross product of P1 and P2
         */
        vcross(p2, p1, a);

        /*
         *  Figure out how much to rotate around that axis.
         */
        vsub(p1, p2, d);
        t = vlength(d) / (2.0f * trackballSize);

        /*
         * Avoid problems with out-of-control values...
         */
        if (t > 1.0) {
            t = 1.0f;
        }
        if (t < -1.0) {
            t = -1.0f;
        }
        phi = 2.0f * (float) Math.asin(t);

        return axisToQuat(a, phi);
    }

    /** Create a unit quaternion that represents the rotation about axis
     * by theta */
    private float[] axisToQuat(float[] axis, float theta) {
        float[] q = new float[4];
        q[3] = (float) Math.cos(theta / 2.0f); //scalar part
        vnormal(axis);
        vcopy(axis, q);
        vscale(q, (float) Math.sin(theta / 2.0));
        return q;
    }

    private float[] renormalizeQuat(float[] q) {
        float len = 0.0f;
        for (int i = 0; i < q.length; i++) {
            len += q[i] * q[i];
        }
        len = (float) Math.sqrt(len);
        float[] ans = new float[q.length];
        for (int i = 0; i < q.length; i++) {
            ans[i] = q[i] / len;
        }
        return ans;
    }

    /**
     * Given two rotations, e1 and e2, expressed as quaternion rotations,
     * figure out the equivalent single rotation and stuff it into dest.
     *
     * This routine also normalizes the result every RENORMCOUNT times it is
     * called, to keep error from creeping in.
     *
     * NOTE: This routine is written so that q1 or q2 may be the same
     * as dest (or each other).
     */
    private float[] addQuats(float[] q1, float[] q2) {
        float[] ans = new float[4];
        ans[3] = q2[3] * q1[3] - q2[0] * q1[0] - q2[1] * q1[1] - q2[2] * q1[2];
        ans[0] = q2[3] * q1[0] + q2[0] * q1[3] + q2[1] * q1[2] - q2[2] * q1[1];
        ans[1] = q2[3] * q1[1] + q2[1] * q1[3] + q2[2] * q1[0] - q2[0] * q1[2];
        ans[2] = q2[3] * q1[2] + q2[2] * q1[3] + q2[0] * q1[1] - q2[1] * q1[0];
        if (++count > RENORMCOUNT) {
            count = 0;
            renormalizeQuat(ans);
        }
        return ans;
    }

    /**
     * Project an x,y pair onto a sphere of radius r OR a hyperbolic sheet
     * if we are away from the center of the sphere.
     */
    private float projectToSphere(float r, float x, float y) {
        float z;
        float d = (float) Math.sqrt(x * x + y * y);
        if (d < r * 0.70710678118654752440f) {    /* Inside sphere */
            z = (float) Math.sqrt(r * r - d * d);
        } else {           /* On hyperbola */
            float t = r / 1.41421356237309504880f;
            z = t * t / d;
        }
        return z;
    }

    /*
     * Build a rotation matrix, given a quaternion rotation.
     *
     */
    private float[] buildMatrix(float q[]) {
        float[] m = new float[16];
        m[0] = 1.0f - 2.0f * (q[1] * q[1] + q[2] * q[2]);
        m[1] = 2.0f * (q[0] * q[1] - q[2] * q[3]);
        m[2] = 2.0f * (q[2] * q[0] + q[1] * q[3]);
        m[3] = 0.0f;

        m[4] = 2.0f * (q[0] * q[1] + q[2] * q[3]);
        m[5] = 1.0f - 2.0f * (q[2] * q[2] + q[0] * q[0]);
        m[6] = 2.0f * (q[1] * q[2] - q[0] * q[3]);
        m[7] = 0.0f;

        m[8] = 2.0f * (q[2] * q[0] - q[1] * q[3]);
        m[9] = 2.0f * (q[1] * q[2] + q[0] * q[3]);
        m[10] = 1.0f - 2.0f * (q[1] * q[1] + q[0] * q[0]);
        m[11] = 0.0f;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
        return m;
    }

    private static void vset(float[] v, float x, float y, float z) {
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }
    private static void vsub(float[] src1, float[] src2, float[] dst) {
        dst[0] = src1[0] - src2[0];
        dst[1] = src1[1] - src2[1];
        dst[2] = src1[2] - src2[2];
    }
    private static void vcopy(float[] v1, float[] v2) {
        System.arraycopy(v1, 0, v2, 0, 3);
    }
    private static void vcross(float[] v1, float[] v2, float[] cross) {
        float[] temp = new float[3];
        temp[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
        temp[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
        temp[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
        vcopy(temp, cross);
    }
    private static float vlength(float[] v) {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }
    private static void vscale(float[] v, float div) {
        v[0] *= div;
        v[1] *= div;
        v[2] *= div;
    }
    private static void vnormal(float[] v) {
        vscale(v, 1.0f / vlength(v));
    }

    private void defineVisualParameters(GLAutoDrawable glad) {
        GL gl = glad.getGL();

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        if (width <= height) {
            gl.glOrtho(-win + panx, 
            		win + panx, -win * (height / width) + pany,
                    win * (height / width) + pany, -ModelViewer.WINDOW_SIZE * 10.0f,
                    ModelViewer.WINDOW_SIZE * 10.0f);
        } else {
            gl.glOrtho(-win * (width / height) + panx,
            		win * (width / height) + panx,
                    -win + pany, win + pany, -ModelViewer.WINDOW_SIZE * 10.0f,
                    ModelViewer.WINDOW_SIZE * 10.0f);
        }
    }
    class GLListener implements GLEventListener {

        public void init(GLAutoDrawable glad) {
            rotmat = null;
            viewport = new int[4];
            mvmatrix = new double[16];
            projmatrix = new double[16];

            panx = 0;
            pany = 0;
            win = ModelViewer.WINDOW_SIZE;

            GLJPanelInteractive.this.init(glad);
        }

        public void display(GLAutoDrawable glad) {
            GL gl = glad.getGL();

            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();

            rotmat = getRotMatrix(rotmat);
            if (rotmat != null) {
                gl.glMultMatrixf(rotmat, 0);
            }

            //used for gluUnproject
            gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
            gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
            gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

            defineVisualParameters(glad);

            GLJPanelInteractive.this.display(glad);
        }

        public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
            GL gl = glad.getGL();

            if (h == 0) {
                h = 1;
            }

            width = w;
            height = h;

            gl.glViewport(0, 0, w, h);            
            defineVisualParameters(glad);

            GLJPanelInteractive.this.reshape(glad, x, y, w, h);
        }

        public void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1) {
            GLJPanelInteractive.this.displayChanged(glad, bln, bln1);
        }

    }
    class TrackballMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent evt) {
            if (execute) {
                int dx = startX - evt.getX();
                int dy = startY - evt.getY();
                spin = (dx * dx + dy * dy > EPS2);
            }
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                execute = true;
                startX = prevX = evt.getX();
                startY = prevY = evt.getY();
                spin = false;
            } else {
                execute = false;
            }
        }

    }
    class TrackballMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent evt) {
            if (execute) {
                int aWidth = evt.getComponent().getSize().width;
                int aHeight = evt.getComponent().getSize().height;
                int currX = evt.getX();
                int currY = evt.getY();

                lastQuat = buildQuaternion((float) (2.0f * prevX - aWidth) / (float) aWidth,
                        (float) (aHeight - 2.0f * prevY) / (float) aHeight,
                        (float) (2.0f * currX - aWidth) / (float) aWidth,
                        (float) (aHeight - 2.0f * currY) / (float) aHeight);
                curQuat = addQuats(lastQuat, curQuat);
                repaint();
                prevX = currX;
                prevY = currY;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

    }
    public class ZoomListener extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (this.execute) {
                if (e.getY() > this.prevy) {
                    this.prevy = e.getY();
                    if (win < ModelViewer.WINDOW_SIZE * 10) {
                        win *= 1.10f;
                    }
                } else {
                    this.prevy = e.getY();
                    if (win > 0) {
                        win *= 0.809f;
                    }
                }

                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            if (e.getButton() == MouseEvent.BUTTON3) {
                this.prevy = e.getY();
                this.execute = true;
            } else {
                this.execute = false;
            }
        }

        private boolean execute = false;
        private int prevy = 0;
    }
    public class PanListener extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (this.execute) {
                float diffx = ((float) (e.getX() - this.prevx));
                this.prevx = e.getX();
                panx -= (diffx / getSize().width) * WINDOW_SIZE;

                float diffy = ((float) (e.getY() - this.prevy));
                this.prevy = e.getY();
                pany += (diffy / getSize().height) * WINDOW_SIZE;

                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            if (e.getButton() == MouseEvent.BUTTON2) {
                this.prevx = e.getX();
                this.prevy = e.getY();
                this.execute = true;
            } else {
                this.execute = false;
            }
        }

        private boolean execute = false;
        private int prevy = 0;
        private int prevx = 0;
    }
    
    private float height;
    private float width;
    private float win;
    private float panx;
    private float pany;
    private int viewport[];
    private double mvmatrix[];
    private double projmatrix[];
    private float rotmat[];
    private final float trackballSize = 0.8f;
    private int prevX = 0;
    private int prevY = 0;
    private int startX = 0;
    private int startY = 0;
    private float[] curQuat = buildQuaternion(0.0f, 0.0f, 0.0f, 0.0f);
    private float[] lastQuat = curQuat;
    private boolean spin = false;
    private static final int RENORMCOUNT = 97;
    private int count = 0;
    private final static int EPS2 = 25;  //only spin if mouse moved this far
    private boolean execute = false;
}
