
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 toCamera;

out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

void main() {
    vec3 result = vec3(0,0,0);
    vec3 N = normalize(normalVector);
    vec3 L = normalize(toLight);
	result +=  max(dot(N,L),0.0) * lightColor;
	color = vec4(1.0,0.0,0.0,1.0) * vec4(result,1.0);
}
