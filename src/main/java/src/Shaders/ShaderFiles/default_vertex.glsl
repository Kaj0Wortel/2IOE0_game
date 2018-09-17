
#version 400 core

in vec3 position;
in vec3 normal;
in vec2 tex;

out vec3 normalVector;
out vec3 toLight;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 lightPosition;

void main(void) {
    vec4 worldPos = modelMatrix * vec4(position,1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPos;

    normalVector = (modelMatrix * vec4(normal,0.0)).xyz;
    toLight = lightPosition - worldPos.xyz/worldPos.w;
}
