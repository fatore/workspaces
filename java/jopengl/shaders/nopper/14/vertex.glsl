#version 150

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelViewMatrix;

in vec3 a_position;

void main() {

	gl_Position = u_projectionMatrix * u_modelViewMatrix * vec4(a_position, 1.0);
}   