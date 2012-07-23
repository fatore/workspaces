package br.usp.gl.core;

public class Camera {

	private float[] position;
	private float[] direction;
	private float[] up;
	private float fov;
	
	public Camera(float[] position, float[] direction, float[] up, float fov) {
		
		this.position = position;
		this.direction = direction;
		this.up = up;
		this.fov = fov;
	}

	public float[] getPosition() {return position;}
	public float[] getDirection() {return direction;}
	public float[] getUp() {return up;}
	public float getFov() {return fov;}
}
