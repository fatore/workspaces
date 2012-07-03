#version 420 core

layout(vertices = 3) out;

in vec3 vPosition[];

out vec3 tcPosition[];

uniform float uTessInnerLevel;
uniform float uTessOuterLevel;

#define ID gl_InvocationID

void main()
{
    tcPosition[ID] = vPosition[ID];
    if (ID == 0) {
        gl_TessLevelInner[0] = uTessInnerLevel;
        gl_TessLevelOuter[0] = uTessOuterLevel;
        gl_TessLevelOuter[1] = uTessOuterLevel;
        gl_TessLevelOuter[2] = uTessOuterLevel;
    }
}