package br.usp.gl.core;

import javax.media.opengl.GL3;

import br.usp.gl.vectors.Vector3;
import br.usp.gl.vectors.Vector4;

import com.jogamp.common.nio.Buffers;

public class Light {
	
	private GL3 gl;
	
	private Vector3 dirOrPos;
	private boolean positional;
	
	private Vector4 ambientColor;
	private Vector4 diffuseColor;
	private Vector4 specularColor;
	
	private int directionHandle;
	private int ambientColorHandle;
	private int diffuseColorHandle;
	private int specularColorHandle;
	
	private boolean on;
	
	public Light(float[] dirOrPos, float[] ambientColor,
			float[] diffuseColor, float[] specularColor, boolean on) {
		
		this(dirOrPos, false, ambientColor, diffuseColor, specularColor, on);
	}
	
	public Light(float[] dirOrPos, boolean positional, float[] ambientColor,
			float[] diffuseColor, float[] specularColor, boolean on) {
		
		this.positional = positional;
		
		setDirOrPos(dirOrPos);
		setAmbientColor(ambientColor);
		setDiffuseColor(diffuseColor);
		setSpecularColor(specularColor);
		
		this.on = on;
	}

	public float[] getDirOrPos() {return dirOrPos.getVector();}
	public float[] getAmbientColor() {return ambientColor.getVector();}
	public float[] getDiffuseColor() {return diffuseColor.getVector();}
	public float[] getSpecularColor() {return specularColor.getVector();}
	
	public void incDirection(float[] inc) {
		
		this.dirOrPos.getVector()[0] += inc[0];
		this.dirOrPos.getVector()[1] += inc[1];
		this.dirOrPos.getVector()[2] += inc[2];
		
		this.dirOrPos.normalize();
	}

	public void setDirOrPos(float[] dirOrPos) {
		this.dirOrPos = new Vector3(dirOrPos);
		if (!positional) {
			this.dirOrPos.normalize();
		}
	}
	public void setAmbientColor(float[] ambientColor) {this.ambientColor = new Vector4(ambientColor);}
	public void setDiffuseColor(float[] diffuseColor) {this.diffuseColor = new Vector4(diffuseColor);}
	public void setSpecularColor(float[] specularColor) {this.specularColor = new Vector4(specularColor);}

	public int getDirectionHandle() {return directionHandle;}
	public int getAmbientColorHandle() {return ambientColorHandle;}
	public int getDiffuseColorHandle() {return diffuseColorHandle;}
	public int getSpecularColorHandle() {return specularColorHandle;}
	
	public boolean isOn() {return on;}
	public void turnOn() {this.on = true;}
	public void turnOff() {this.on = false;}
	public void switchOn() {this.on = !this.on;}

	public void init(GL3 gl, int directionHandle, int ambientColorHandle,
			int diffuseColorHandle, int specularColorHandle) {
		
		this.gl = gl;
		
		this.directionHandle = directionHandle;
		this.ambientColorHandle = ambientColorHandle;
		this.diffuseColorHandle = diffuseColorHandle;
		this.specularColorHandle = specularColorHandle;
	}
	
	public void bind() {
		
		gl.glUniform3fv(directionHandle, 1, Buffers.newDirectFloatBuffer(getDirOrPos()));
		gl.glUniform4fv(ambientColorHandle, 1, Buffers.newDirectFloatBuffer(getAmbientColor()));
		gl.glUniform4fv(diffuseColorHandle, 1, Buffers.newDirectFloatBuffer(getDiffuseColor()));
		gl.glUniform4fv(specularColorHandle, 1, Buffers.newDirectFloatBuffer(getSpecularColor()));
	}
}
