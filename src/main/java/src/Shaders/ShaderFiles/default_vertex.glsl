
#version 400 core

in vec3 position;
in vec3 normal;
in vec2 tex;

out vec3 normalVector;
out vec3 toLight;
out vec3 toCamera;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 lightPosition;
uniform int time;

void main(void) {
    vec4 p = modelMatrix * vec4(position,1.0);
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * p;

    normalVector = (modelMatrix * vec4(normal,0.0)).xyz;
    toLight = lightPosition - p.xyz;
    //toCamera = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPos.xyz;

}
