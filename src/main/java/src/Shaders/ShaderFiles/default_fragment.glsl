
#version 400 core

in vec3 normalVector;
in vec3 toLight;

out vec4 color;

uniform vec3 lightColor;

void main(void) {

    vec3 normal = normalize(normalVector);
    vec3 light = normalize(toLight);
    float dt = dot(normal,light);
    float b = max(dt,0.0);
	vec3 result = b * lightColor;

    color = vec4(result,1.0) * vec4(1.0,0.0,0.0,1.0);
 }

