package br.usp.gl.effects;

import javax.media.opengl.GL3;

public abstract class Effect {
	
	protected GL3 gl;
	
	protected int handle;
	
	public void init(GL3 gl, int handle) {
		
		this.gl = gl;
		
		this.handle = handle;
	}
	
	public abstract void bind();
}
