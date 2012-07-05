package br.usp.pf.gl.core;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Fatore
 */
public class Triangle {

    /**
	 */
    Vertex[] vertices;
    /**
	 */
    float[] normalVector;

    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        vertices = new Vertex[3];

        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.vertices[2] = v3;
    }

    /**
	 * @return
	 */
    public float[] getNormalVector() {
        return normalVector;
    }

    /**
	 * @return
	 */
    public Vertex[] getVertices() {
        return vertices;
    }

    public void calculateNormalVector() {

        float Qx, Qy, Qz, Px, Py, Pz;

        Qx = vertices[1].getX() - vertices[0].getX();
        Qy = vertices[1].getEnergy() - vertices[0].getEnergy();
        Qz = vertices[1].getY() - vertices[0].getY();

        Px = vertices[2].getX() - vertices[0].getX();
        Py = vertices[2].getEnergy() - vertices[0].getEnergy();
        Pz = vertices[2].getY() - vertices[0].getY();

        float  fNormalX = Py * Qz - Pz * Qy;
        float  fNormalY = Pz * Qx - Px * Qz;
        float  fNormalZ = Px * Qy - Py * Qx;

        this.normalVector = new float[]{fNormalX, fNormalY, fNormalZ};
    }



}
