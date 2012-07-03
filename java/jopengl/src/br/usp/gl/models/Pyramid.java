package br.usp.gl.models;

public class Pyramid extends Model {
	
	private static final float[] sPositions = new float[] {
		
	   -1.0f, -1.0f, 1.0f,
	    1.0f, -1.0f, 1.0f,
	    1.0f, -1.0f, -1.0f,
	   -1.0f, -1.0f, -1.0f,
	    0.0f,  1.0f,  0.0f  
	};
	
	private static final int[] sIndices = new int[] {
		
		0,1,4,	// Front
		3,0,4,	// Left
		0,1,3,	
		1,2,3, 	// Bottom
		1,2,4,	// Right
		2,3,4	// Back
	};

	public Pyramid() {
		
		this.positions = sPositions;
		this.indices = sIndices;
	}
        
	
}























