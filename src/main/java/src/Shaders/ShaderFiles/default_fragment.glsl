
#version 400 core

in vec3 normalVector;
in vec3 toLight;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

void main() {
    vec3 unitNormal = normalize(normalVector);
    vec3 unitLight = normalize(toLight);
    float d = dot(unitNormal, unitLight);
    d = max(d,0.0);
    vec3 change = d * lightColor;
	color = vec4(change,1.0) * vec4(1.0,0.0,0.0,1.0);
}
