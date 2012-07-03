package br.usp.gl.core;

import javax.media.opengl.GL3;

import br.usp.gl.vectors.Vector4;

import com.jogamp.common.nio.Buffers;

public class Material {

private GL3 gl;
	
	private Vector4 ambientColor;
	private Vector4 diffuseColor;
	private Vector4 specularColor;
	private float specularExponent;
	
	private int ambientColorHandle;
	private int diffuseColorHandle;
	private int specularColorHandle;
	private int specularExponentHandle;
	
	public Material(float[] ambientColor, float[] diffuseColor,
			float[] specularColor, float specularExponent) {
		
		setAmbientColor(ambientColor);
		setDiffuseColor(diffuseColor);
		setSpecularColor(specularColor);
		setSpecularExponent(specularExponent);
	}

	public float[] getAmbientColor() {return ambientColor.getVector();}
	public float[] getDiffuseColor() {return diffuseColor.getVector();}
	public float[] getSpecularColor() {return specularColor.getVector();}
	public float getSpecularExponent() {return specularExponent;}

	public void setAmbientColor(float[] ambientColor) {this.ambientColor = new Vector4(ambientColor);}
	public void setDiffuseColor(float[] diffuseColor) {this.diffuseColor = new Vector4(diffuseColor);}
	public void setSpecularColor(float[] specularColor) {this.specularColor = new Vector4(specularColor);}
	public void setSpecularExponent(float expoent) {this.specularExponent = expoent;}

	public int getAmbientColorHandle() {return ambientColorHandle;}
	public int getDiffuseColorHandle() {return diffuseColorHandle;}
	public int getSpecularColorHandle() {return specularColorHandle;}
	public int getSpecularExpoentHandle() {return specularExponentHandle;}
	
	public void init(GL3 gl, int ambientColorHandle, int diffuseColorHandle,
			int specularColorHandle, int specularExponentHandle) {
		
		this.gl = gl;
		
		this.ambientColorHandle = ambientColorHandle;
		this.diffuseColorHandle = diffuseColorHandle;
		this.specularColorHandle = specularColorHandle;
		this.specularExponentHandle = specularExponentHandle;
	}
	
	public void bind() {
		
		gl.glUniform4fv(ambientColorHandle, 1, Buffers.newDirectFloatBuffer(getAmbientColor()));
		gl.glUniform4fv(diffuseColorHandle, 1, Buffers.newDirectFloatBuffer(getDiffuseColor()));
		gl.glUniform4fv(specularColorHandle, 1, Buffers.newDirectFloatBuffer(getSpecularColor()));
		gl.glUniform1f(specularExponentHandle, getSpecularExponent());
	}
}
