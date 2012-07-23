package br.usp.gl.buffers;

import javax.media.opengl.GL4;

import com.jogamp.common.nio.Buffers;

public class ArrayElementsBuffer extends Buffer {
	
	int[] vbo;

	public ArrayElementsBuffer(GL4 gl, int[] array) {
		
		super(gl);
		
		load(array);
	}

	private void load(int[] array) {
		
		vbo = new int[1];
		gl.glGenBuffers(1, vbo, 0);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, array.length * Buffers.SIZEOF_INT,
				Buffers.newDirectIntBuffer(array), GL4.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void bind() {
		
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, vbo[0]);
	}

	@Override
	public void dispose() {
		
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);

		if (vbo[0] > 0) {
			gl.glDeleteBuffers(1, vbo, 0);
			vbo[0] = 0;
		}
	}
}
