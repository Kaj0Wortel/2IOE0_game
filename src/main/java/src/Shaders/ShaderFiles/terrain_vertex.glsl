
#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tex;
layout(location = 2) in vec3 normal;

out vec3 normalVector;
out vec3 toLight;
out vec3 toCamera;
out vec2 texPass;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 lightPosition;
uniform vec3 cameraPos;
uniform int time;

void main(void) {
    texPass = tex;
    vec4 pos = modelMatrix * vec4(position,1.0);
    normalVector = (modelMatrix * vec4(normal,0.0)).xyz;
    toLight = lightPosition - pos.xyz;
    gl_Position = projectionMatrix * viewMatrix * pos;
}
