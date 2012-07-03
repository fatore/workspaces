package br.usp.gl.buffers;

import javax.media.opengl.GL3;

public abstract class Buffer {
	
	protected GL3 gl;
	
	public Buffer(GL3 gl) {
		
		this.gl = gl;
	}
	
	public abstract void bind();
	public abstract void dispose();
}
