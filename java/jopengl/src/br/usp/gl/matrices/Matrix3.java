package br.usp.gl.matrices;

import javax.media.opengl.GL4;

public class Matrix3 extends Matrix {

	private GL4 gl;
	private int handle;
	
	public Matrix3() {
		
		this.matrix = new float[9];
	}
	
	public void init(final GL4 gl, int handle) {

		this.gl = gl;
		this.handle = handle;
	}
	
	public void bind() {

		bind(false);
	}
	
	public void bind(boolean transposed) {
		
		gl.glUniformMatrix3fv(handle, 1, transposed, this.matrix, 0);
	}
	
	public void loadIdentity() {
		
        this.matrix[0] = 1;
        this.matrix[1] = 0;
        this.matrix[2] = 0;
        this.matrix[3] = 0;
        this.matrix[4] = 1;
        this.matrix[5] = 0;
        this.matrix[6] = 0;
        this.matrix[7] = 0;
        this.matrix[8] = 1;
	}
	
	public void transpose() {
		
        float a01 = this.matrix[1], a02 = this.matrix[2],
            a12 = this.matrix[5];

        this.matrix[1] = this.matrix[3];
        this.matrix[2] = this.matrix[6];
        this.matrix[3] = a01;
        this.matrix[5] = this.matrix[7];
        this.matrix[6] = a02;
        this.matrix[7] = a12;
	}
	
	public void print() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.printf("%.5f ", this.matrix[i + j * 3]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void extractInverseAndTranspose(Matrix4 mvMatrix) {
		
		mvMatrix.toInverseMat3(this.matrix);
        this.transpose();
	}
	
	public void extract(Matrix mvMatrix) {
		
		float[] source = mvMatrix.getMatrix();
		
		matrix[0] = source[0];
	    matrix[1] = source[1];
	    matrix[2] = source[2];

	    matrix[3] = source[4];
	    matrix[4] = source[5];
	    matrix[5] = source[6];

	    matrix[6] = source[8];
	    matrix[7] = source[9];
	    matrix[8] = source[10];
	}
}
