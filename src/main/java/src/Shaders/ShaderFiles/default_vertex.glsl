
#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tex;
layout(location = 2) in vec3 normal;

out vec3 normalVector;
out vec3 toLight;
out vec3 toCamera;
out vec3 bug;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat3 normalMatrix;
uniform vec3 lightPosition;
uniform int time;

void main(void) {
    normalVector = (modelMatrix * vec4(normal,0.0)).xyz;
    toLight = lightPosition - (modelMatrix * vec4(position,1.0)).xyz;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position,1.0);
}
