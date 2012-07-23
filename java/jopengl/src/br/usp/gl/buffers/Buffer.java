package br.usp.gl.buffers;

import javax.media.opengl.GL4;

public abstract class Buffer {
	
	protected GL4 gl;
	
	public Buffer(GL4 gl) {
		
		this.gl = gl;
	}
	
	public abstract void bind();
	public abstract void dispose();
}
