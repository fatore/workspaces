#version 420 core

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelViewMatrix;
uniform mat3 u_normalMatrix;

uniform vec3 uLightDirection;

in vec3 aVertexPosition;
in vec3 aVertexNormal;

out vec3 vNormal;
out vec3 vEye;

uniform bool uPancake;

void main()
{
    vec4 vertex = u_modelViewMatrix * vec4(aVertexPosition, 1);

	vEye = -vec3(vertex);
	
	vNormal = u_normalMatrix * aVertexNormal;
    
    if (!uPancake) {
    	gl_Position = u_projectionMatrix * u_modelViewMatrix * vec4(aVertexPosition, 1);
    } else {
	    gl_Position = u_projectionMatrix * u_modelViewMatrix * vec4(aVertexPosition.x, 0.0, aVertexPosition.z, 1.0);
    }
}