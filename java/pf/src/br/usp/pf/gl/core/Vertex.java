package br.usp.pf.gl.core;

import java.awt.Color;
import java.util.HashMap;

class SmoothVertex {

    float x;
    float y;
    float energy;
}

public class Vertex {

    private int id;
    private float x;
    private float y;
    private float energy;
    public Color color;
    private SmoothVertex smoothVertex;
    float[] normalVector;

    public Vertex(int id, float x, float y, float energy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energy = energy;
    }
    
    public Vertex() {}

    public void normalize(float[][] exValues) {
        x = ((x - exValues[0][0]) / (exValues[1][0] - exValues[0][0]));
        x = (x - 0.5f) * 2;

        y = ((y - exValues[0][1]) / (exValues[1][1] - exValues[0][1]));
        y = (y - 0.5f) * 2;

        energy = ((energy - exValues[0][2]) / (exValues[1][2] - exValues[0][2]));
        energy = (energy - 0.5f) * 2;
    }

    private void divideByf(float f) {
        this.x = this.x / f;
        this.y = this.y / f;
        this.energy = this.energy / f;
    }

    public void setSmoothVertex(Vertex vts[]) {

        smoothVertex = new SmoothVertex();

        //soma os vizinhos
        for (Vertex v : vts) {
            smoothVertex.x += v.getX();
            smoothVertex.y += v.getY();
            smoothVertex.energy += v.getEnergy();
        }

        //soma o proprio vertice
        smoothVertex.x += this.getX();
        smoothVertex.y += this.getY();
        smoothVertex.energy += this.getEnergy();

        //tira a media, div = numeros de vizinhos + o proprio vertice
        int div = vts.length + 1;
        smoothVertex.x /= div;
        smoothVertex.y /= div;
        smoothVertex.energy /= div;
    }

    public void smooth() {
        this.x = smoothVertex.x;
        this.y = smoothVertex.y;
        this.energy = smoothVertex.energy;
    }

    public void setNormalVector(float[][] normals) {
        float[] vn = new float[3];
        for (float[] n : normals) {
            vn[0] += n[0];
            vn[1] += n[1];
            vn[2] += n[2];
        }
        float div = normals.length;
        vn[0] /= div;
        vn[1] /= div;
        vn[2] /= div;

        this.normalVector = vn;
    }
    
    public void setId(int id) {this.id = id;}    
    public void setX(float x) {this.x = x;}    
    public void setY(float y) {this.y = y;}    
    public void setEnergy(float energy) {this.energy = energy;}

    public float[] getNormalVector() {return this.normalVector;}
    public float getEnergy() {return energy;}
    public int getId() {return id;}
    public float getX() {return x;}
    public float getY() {return y;}
}
