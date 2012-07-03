#version 150

uniform mat3 u_normalMatrix;

in vec3 a_position;
in vec3 a_normal;

out vec3 v_normal;

void main()
{
	v_normal = u_normalMatrix * a_normal;
		
	// Projection etc. is done in the geometry shader
	gl_Position = vec4(a_position, 1.0);
}   