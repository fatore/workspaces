package br.usp.gl.buffers;

import javax.media.opengl.GL4;

import com.jogamp.common.nio.Buffers;

public class ArrayBuffer extends Buffer {
	
	private int[] vbo;
	
	private int itemSize;
	
	private int handle;
	
	public ArrayBuffer(GL4 gl, float[] array, int itemSize, int handle) {
		
		super(gl);
		
		this.itemSize = itemSize;
		this.handle = handle;
		
		load(array);
	}

	private void load(float[] array) {
		
		vbo = new int[1];
		gl.glGenBuffers(1, vbo, 0);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, array.length * Buffers.SIZEOF_FLOAT,
				Buffers.newDirectFloatBuffer(array), GL4.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void bind() {
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(handle, itemSize, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(handle);
	}
	
	public void dispose() {
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

		if (vbo[0] > 0) {
			gl.glDeleteBuffers(1, vbo, 0);
			vbo[0] = 0;
		}
	}
}
