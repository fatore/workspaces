package br.usp.gl.models;

import javax.media.opengl.GL3;

import br.usp.gl.buffers.ArrayBuffer;
import br.usp.gl.buffers.ArrayElementsBuffer;
import br.usp.gl.effects.Effect;
import br.usp.gl.effects.Texture2D;

public abstract class Model {
	
	protected GL3 gl;
	
	protected float[] positions;
	protected float[] normals;
	protected float[] tangents;
	protected float[] biTangents;
	protected float[] texCoords;
	protected int[] indices; 
	
	private Effect effect;
	
	private ArrayBuffer positionsBuffer;
	private ArrayBuffer normalsBuffer;
	private ArrayBuffer tangentsBuffer;
	private ArrayBuffer biTangentsBuffer;
	private ArrayBuffer textureCoordsBuffer;
	private ArrayElementsBuffer indicesBuffer;
	
	private int positionHandle;
	private int normalsHandle;
	private int tangentsHandle;
	private int biTangentsHandle;
	private int textureCoordHandle;
	
	int[] vaoHandle;
	
	
	public void init(GL3 gl, int positionHandle) {
		
		init(gl, positionHandle, -1);
	}
	
	public void init(GL3 gl, int positionHandle, int normalsHandle) {
		
		init(gl, positionHandle, normalsHandle, null, -1);
	}
	
	public void init(GL3 gl, int positionHandle,
			Texture2D texture, int textureCoordHandle) {
		
		init(gl, positionHandle, -1, texture, textureCoordHandle);
	}
	
	public void init(GL3 gl, int positionHandle, int normalsHandle, Effect effect) {
		
		init(gl, positionHandle, normalsHandle, -1, -1, effect, -1);
	}
	
	public void init(GL3 gl, int positionHandle, int normalsHandle, 
			Effect texture, int textureCoordHandle) {
		
		init(gl, positionHandle, normalsHandle, -1, -1, texture, textureCoordHandle);
	}
	
	public void init(GL3 gl, int positionHandle, int normalsHandle, 
			int tangentsHandle, int biTangentsHandle,
			Effect texture, int textureCoordHandle) {
		
		this.gl = gl;
		
		this.positionHandle = positionHandle;
		
		this.normalsHandle = normalsHandle;
		this.tangentsHandle = tangentsHandle;
		this.biTangentsHandle = biTangentsHandle;
		
		this.effect = texture;
		this.textureCoordHandle = textureCoordHandle;
		
		initBuffers();
	}
	
	private void initBuffers() {
		
		if (positions != null) {
			positionsBuffer = new ArrayBuffer(gl, positions, 3, positionHandle);
		}
		
		if (normals != null && normalsHandle >= 0) {
			normalsBuffer = new ArrayBuffer(gl, normals, 3, normalsHandle);
		}
		
		if (tangents != null && tangentsHandle >= 0) {
			tangentsBuffer = new ArrayBuffer(gl, tangents, 3, tangentsHandle);
		}
		
		if (biTangents != null && biTangentsHandle >= 0) {
			biTangentsBuffer = new ArrayBuffer(gl, biTangents, 3, biTangentsHandle);
		}
		
		if (texCoords != null && textureCoordHandle >= 0) {
			textureCoordsBuffer = new ArrayBuffer(gl, texCoords, 2, textureCoordHandle);
		}
		
		if (indices != null) {
			indicesBuffer = new ArrayElementsBuffer(gl, indices);
		}
		
		vaoHandle = new int[1];
		gl.glGenVertexArrays(1, vaoHandle, 0);
		gl.glBindVertexArray(vaoHandle[0]);
	}
	
	public void bind() {

		if (positionsBuffer != null) {
			positionsBuffer.bind();
		}
		
		if (normalsBuffer != null) {
			normalsBuffer.bind();
		}
		
		if (tangentsBuffer != null) {
			tangentsBuffer.bind();
		}
		
		if (biTangentsBuffer != null) {
			biTangentsBuffer.bind();
		}
		
		if (textureCoordsBuffer != null) {
			textureCoordsBuffer.bind();
			effect.bind();
		}
		
		if (indicesBuffer != null) {
			indicesBuffer.bind();
		}
		
		gl.glBindVertexArray(vaoHandle[0]);
	}
	
	public void draw(int primitive) {
		
		if (indicesBuffer != null) {
			gl.glDrawElements(primitive, indices.length, GL3.GL_UNSIGNED_INT, 0);
		} else {
			gl.glDrawArrays(primitive, 0, positions.length / 3);
		}
	}
	
	public void dispose() {
		
		if (positionsBuffer != null) {
			positionsBuffer.dispose();
		}
		
		if (normalsBuffer != null) {
			normalsBuffer.dispose();
		}
		
		if (tangentsBuffer != null) {
			tangentsBuffer.dispose();
		}
		
		if (biTangentsBuffer != null) {
			biTangentsBuffer.dispose();
		}
		
		if (indicesBuffer != null) {
			indicesBuffer.dispose();
		}
	}
}
