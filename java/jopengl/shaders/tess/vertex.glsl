#version 420 core

in vec3 aVertexPosition;
out vec3 vPosition;

void main()
{
    vPosition = aVertexPosition;
}