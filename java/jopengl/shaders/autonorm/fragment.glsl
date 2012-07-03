#version 150

out vec4 FragColor;

in vec3 gFacetNormal;
in vec3 gTriDistance;
in float gPrimitive;

uniform vec3 uLightDirection;
uniform vec4 uLightAmbientColor;
uniform vec4 uLightDiffuseColor;

float amplify(float d, float scale, float offset)
{
    d = scale * d + offset;
    d = clamp(d, 0, 1);
    d = 1 - exp2(-2*d*d);
    return d;
}

void main()
{
    vec3 N = normalize(gFacetNormal);
    vec3 L = uLightDirection;
    float df = abs(dot(N, L));
    vec3 color = vec3(uLightAmbientColor.xyz) + df * vec3(uLightDiffuseColor.xyz);

    float d1 = min(min(gTriDistance.x, gTriDistance.y), gTriDistance.z);

    FragColor = vec4(color, 1.0);
}