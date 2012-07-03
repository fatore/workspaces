#version 150

// A triangle with three points come in
layout(triangles) in;

// Out of three triangle points, we create a triangle strip with two triangles
layout(triangle_strip, max_vertices = 6) out;

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelViewMatrix;
uniform vec3 u_lightPosition;

in vec3 v_normal[3];

out vec3 g_eye;
out vec3 g_normal;
out vec4 g_color;
out vec3 g_lightDirection;

void main(void) {

	// Counter.
	int i;
	
	// Load identity.
	mat4 translateMatrix = mat4(1.0);
	
	vec4 position;

	// Translate -1.5 in x
	translateMatrix[3][0] = -1.5;
	
	// Input
	for (i = 0; i < gl_in.length(); i++) {
	
		// New vertex position is previous position applied to the transformation
        position = u_modelViewMatrix * translateMatrix * gl_in[i].gl_Position;

		// Final position is obtained by appling the projection matrix
		gl_Position = u_projectionMatrix * position;

		// Normal is already in camera space
		g_normal = v_normal[i];
		
		g_eye = -vec3(position);
		
		// Blue color
		g_color = vec4(1.0, 0.0, 0.0, 1.0);

		// Light position is in camera space
		g_lightDirection = vec3(vec4(u_lightPosition, 1.0) - position);

        EmitVertex();
    }

	EndPrimitive();    

	// Translate +1.5 in x
	translateMatrix[3][0] = +1.5;
	
	for (i = 0; i < gl_in.length(); i++)
    {
        position = u_modelViewMatrix * translateMatrix * gl_in[i].gl_Position;

		gl_Position = u_projectionMatrix * position;

		g_normal = v_normal[i];
		
		g_eye = -vec3(position);
		
		// Red color
		g_color = vec4(0.0, 0.0, 1.0, 1.0);

		g_lightDirection = vec3(vec4(u_lightPosition, 1.0) - position);

        EmitVertex();
    }
    
    EndPrimitive();  
}














   