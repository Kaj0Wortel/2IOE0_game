
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 fragPosition;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;
uniform vec3 cameraPos;

void main() {
    vec3 unitNormal = normalize(normalVector);
    vec3 unitLight = normalize(toLight);
    float d = dot(unitNormal, unitLight);
    d = max(d,0.0);
    vec3 change = d * lightColor;

    vec3 lightVec = normalize(cameraPos - fragPosition);
    vec3 invLight = -unitLight;
    vec3 ref = reflect(invLight, unitNormal);
    float dotSpec = dot(lightVec, ref);
    dotSpec = max(d, 0.0);
    dotSpec = pow(d, 35);
    vec3 specular = dotSpec * lightColor;


	color = (vec4(change,1.0) + vec4(specular,1.0)) * vec4(1.0,0.0,0.0,1.0);
}
