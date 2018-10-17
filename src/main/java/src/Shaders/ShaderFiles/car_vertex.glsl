
#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tex;
layout(location = 2) in vec3 normal;

out vec3 normalVector;
out vec3 toLight;
out vec3 toCamera;
out vec4 shadowTextureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 lightPosition;
uniform vec3 camera;
uniform int time;

uniform mat4 shadowMatrix;

const float shadowDistance = 300;
const float dist = 10;

void main(void) {
    vec4 pos = modelMatrix * vec4(position,1.0);
    shadowTextureCoords = shadowMatrix * pos;
    normalVector = (modelMatrix * vec4(normal,0.0)).xyz;
    toLight = lightPosition - pos.xyz;
    toCamera = camera - pos.xyz;
    gl_Position = projectionMatrix * viewMatrix * pos;

    vec4 viewPos = viewMatrix * pos;
    float lengthToCamera = length(viewPos.xyz);
    lengthToCamera = lengthToCamera - (shadowDistance - dist);
    lengthToCamera = lengthToCamera / dist;
    shadowTextureCoords.w = clamp(1.0-lengthToCamera, 0.0, 1.0);
}
