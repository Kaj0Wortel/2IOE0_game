
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 toCamera;
in vec2 texPass;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

uniform sampler2D textureRoad;
uniform sampler2D bumpmap;
uniform mat4 modelMatrix;

void main() {

    vec2 tex = texPass;
    tex.x *= 10;
    tex.x -= floor(tex.x);

    vec2 normalTex = texPass;
    bool normalMapping = true;
    vec3 unitNormal;
    if(normalMapping){
        normalTex.x *= 30;
        normalTex.x -= floor(normalTex.x);
        normalTex.y *= 3;
        normalTex.y -= floor(normalTex.y);

        if(0.1 < tex.y && tex.y < 0.9){
            unitNormal = normalize(texture(bumpmap,normalTex).rgb * 2.0 - 1.0);
        }else{
            unitNormal = normalize(normalVector);
        }
    }else {
        unitNormal = normalize(normalVector);
    }

    vec3 unitLight = normalize(toLight);
    float d = dot(unitNormal, unitLight);
    d = max(d,0.3);
    vec3 change = d * lightColor;

	color = vec4(change,1.0) * texture(textureRoad,tex);
}
