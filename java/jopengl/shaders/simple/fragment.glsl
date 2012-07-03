#version 150

uniform vec3 uLightDirection;
uniform vec4 uLightAmbientColor;
uniform vec4 uLightDiffuseColor;
uniform vec4 uLightSpecularColor;

uniform vec4 uMaterialAmbientColor;
uniform vec4 uMaterialDiffuseColor;
uniform vec4 uMaterialSpecularColor;
uniform float uMaterialSpecularExpoent;

in vec3 vNormal;
in vec3 vEye;

out vec4 fragColor;

void main()
{
	vec4 color = uLightAmbientColor * uMaterialAmbientColor;

	vec3 normal = normalize(vNormal);

	float nDotL = max(dot(uLightDirection, normal), 0.0);
		
	if (nDotL > 0.0)
	{
		vec3 eye = normalize(vEye);
	
		vec3 reflection = reflect(-uLightDirection, normal);
		
		float eDotR = max(dot(eye, reflection), 0.0);
	
		color += uLightDiffuseColor * uMaterialDiffuseColor * nDotL;
		
		color += uLightSpecularColor * uMaterialSpecularColor * pow(eDotR, uMaterialSpecularExpoent);
	}

	fragColor = color;
}   