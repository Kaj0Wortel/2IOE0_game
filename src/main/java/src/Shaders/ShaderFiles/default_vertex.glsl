
#version 400 core

in vec3 position;
in vec2 tex;
in vec3 normal;

out vec3 P;
out vec3 normalVector;
//out vec3 toLight;
out vec3 toCamera;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform float time;


void main(void) {
    vec4 p = modelMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * p;
    P = gl_Position.xyz / gl_Position.w;

    normalVector = (modelMatrix * vec4(normal, 0.0)).xyz;
    //toLight = lightPosition - p.xyz;
    //toCamera = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPos.xyz;

}
