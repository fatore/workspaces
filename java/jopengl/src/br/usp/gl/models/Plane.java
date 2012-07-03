package br.usp.gl.models;

import br.usp.gl.vectors.Vector3;

public class Plane extends Model {
	
	private static final float[] positions_ = new float[] {
		
		   -1.0f, -1.0f, 0.0f,
		   +1.0f, -1.0f, 0.0f,
		   -1.0f, +1.0f, 0.0f,
		   +1.0f, +1.0f, 0.0f,
	};
	
	private static final float[] normals_ = new float[] {
		
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f
	};
	
	private static final float[] tangents_ = new float[] {
		
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f
	};
	
	private static final float[] textureCoords_ = new float[] {
		
		0.0f, 0.0f, 
		1.0f, 0.0f, 
		0.0f, 1.0f, 
		1.0f, 1.0f
	};
	
	private static final int[] indices_ = new int[] {
		
		0, 1, 2, 1, 3, 2
	};
	
	
	public Plane() {
		
		this(1);
	}
	
	public Plane(float halfExtend) {
		
		this(halfExtend, halfExtend);
	}
	
	public Plane(float horizontalExtend, float verticalExtend) {
		
		this.positions = positions_;
		this.normals = normals_;
		this.tangents = tangents_;
		this.texCoords = textureCoords_;
		this.indices = indices_;
		
		for (int i = 0; i < positions.length / 3; i++) {
			positions[i * 3 + 0] *= horizontalExtend;
			positions[i * 3 + 1] *= verticalExtend;
		}
		
		if (tangents != null) {
			biTangents = new float[positions.length];
			for (int i = 0; i < positions.length / 3; i++) {
				
				float[] cross = Vector3.cross(
						new float[]{normals[i * 3 + 0], normals[i * 3 + 1], normals[i * 3 + 2]},
						new float[]{tangents[i * 3 + 0], tangents[i * 3 + 1], tangents[i * 3 + 2]});
				
				biTangents[i * 3 + 0] = cross[0];
				biTangents[i * 3 + 1] = cross[1];
				biTangents[i * 3 + 2] = cross[2];
		    }
		}
	}
}























