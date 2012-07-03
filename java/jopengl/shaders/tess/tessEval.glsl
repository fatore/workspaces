#version 420 core

layout(triangles, equal_spacing, cw) in;

in vec3 tcPosition[];

out vec3 tePosition;
out vec3 tePatchDistance;

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelViewMatrix;

void main() {

    vec3 p0 = gl_TessCoord.x * tcPosition[0];
    vec3 p1 = gl_TessCoord.y * tcPosition[1];
    vec3 p2 = gl_TessCoord.z * tcPosition[2];
    
    tePatchDistance = gl_TessCoord;
    
    tePosition = normalize(p0 + p1 + p2);
    
    gl_Position = u_projectionMatrix * u_modelViewMatrix * vec4(tePosition, 1);
}