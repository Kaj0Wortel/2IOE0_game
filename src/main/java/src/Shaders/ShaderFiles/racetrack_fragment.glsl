
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 toCamera;
in vec2 texPass;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

uniform sampler2D sampler;

void main() {

    vec2 tex = texPass;
    tex.x *= 10;
    tex.x -= floor(tex.x);

    vec3 unitNormal = normalize(normalVector);
    vec3 unitLight = normalize(toLight);
    float d = dot(unitNormal, unitLight);
    d = max(d,0.3);
    vec3 change = d * lightColor;

	color = vec4(change,1.0) * texture(sampler,tex);
}
