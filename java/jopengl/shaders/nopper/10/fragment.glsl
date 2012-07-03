#version 150

in vec3 g_normal;
in vec3 g_eye;
in vec4 g_color;
in vec3 g_lightDirection;

out vec4 fragColor;

void main(void)
{
	// Note: All calculations are in camera space.

	// Ambient
	vec4 color = 0.3 * g_color;


	// Normalize normal =P
	vec3 normal = normalize(g_normal);
	
	// Normalize light direction
	vec3 lightDirection = normalize(g_lightDirection);

	// dot(N,L) -> diffuse 
	float nDotL = max(dot(lightDirection, normal), 0.0);
		
	if (nDotL > 0.0) {
	
		// Normalize eye 
		vec3 eye = normalize(g_eye);
	
		// Incident vector is opposite light direction vector.
		vec3 reflection = reflect(-lightDirection, normal);
		
		// dot(E,R) -> specular
		float eDotR = max(dot(eye, reflection), 0.0);
	
		// add diffuse color
		color += 1.0 * g_color * nDotL;
		
		// add specular color 
		color += 1.0 * vec4(1.0, 1.0, 1.0, 1.0) * pow(eDotR, 20.0);
	}

	fragColor = color;
}