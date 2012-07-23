package br.usp.gl.effects;

import javax.media.opengl.GL4;

public abstract class Effect {
	
	protected GL4 gl;
	
	protected int handle;
	
	public void init(GL4 gl, int handle) {
		
		this.gl = gl;
		
		this.handle = handle;
	}
	
	public abstract void bind();
}
