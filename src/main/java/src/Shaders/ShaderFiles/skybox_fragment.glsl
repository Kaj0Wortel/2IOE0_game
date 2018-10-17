#version 400 core

in vec3 texturePos;

layout(location = 0) out vec4 color;

uniform samplerCube cubeMap;

void main() {
    color = texture(cubeMap, texturePos);
}
