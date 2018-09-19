
#version 400 core

in vec3 P;
in vec3 normalVector;
//in vec3 toLight;
in vec3 toCamera;

out vec4 color;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

uniform Material material = Material(
        vec4(1, 1, 1, 1),
        vec4(1, 1, 1, 1),
        vec4(1, 1, 1, 1),
        0);


struct Light {
    vec4 position;
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
};

uniform Light[] light = Light[1] (
    Light(
        vec4(0, 0, 0, 0),
        vec4(1, 1, 1, 1),
        vec4(1, 1, 1, 1),
        vec4(1, 1, 1, 1)
    )
);


uniform float time;




vec4 shading(vec3 P, vec3 N) {
    vec4 result = vec4(0, 0, 0, 1); // opaque black
    for (int i = 0; i < light.length; i++) {
        //if (ambient) {
            result += light[i].ambient * material.ambient;
        //}
        
        // Vector towards light source.
        vec3 L = light[i].position.xyz / light[i].position.w - P;
        
        // Angle between normal and light source
        float cosLightAngle = max(dot(N, L), 0) / (length(N) * length(L));
        
        //if (diffuse) {
            result += cosLightAngle * light[i].diffuse * material.diffuse;
        //}
	
        // Position of camera in view space.
        vec3 E = vec3(0);
	
        // Direction towards viewer
        vec3 V = E - P;
	
        //if (specular) {
            vec3 H = (L + V) / (length(L + V));
            float cosNormalH = max(dot(N, H), 0) / (length(N) * length(H));
            result += pow(cosNormalH, material.shininess) * light[i].specular * material.specular;
        //}
    }
    
    return result;
}



void main() {
    vec3 result = vec3(0,0,0);
    vec3 normal = normalize(normalVector);
    color = shading(P, normal);
}
