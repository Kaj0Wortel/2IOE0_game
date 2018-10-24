
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 toCamera;
in vec2 texPass;
in vec4 shadowTextureCoords;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

uniform sampler2D textureRoad;
uniform sampler2D bumpmap;
uniform sampler2D shadowMap;

const int pcfPixels = 3;
const float totalPcfPixels = pow((pcfPixels * 2 + 1),2);
const vec2 poisson[4] = vec2[](
                vec2( -0.94201624, -0.39906216 ),
                vec2( 0.94558609, -0.76890725 ),
                vec2( -0.094184101, -0.92938870 ),
                vec2( 0.34495938, 0.29387760 ));

void main() {


    float depthMapSize = 4096.0;
    float pixelSize = 1 / depthMapSize;

    float visible = 0.0;
    for(int i = -pcfPixels; i < pcfPixels + 1; i++){
        for(int j = -pcfPixels; j < pcfPixels + 1; j++){
            float depth = texture(shadowMap, shadowTextureCoords.xy + vec2(i,j)*pixelSize).r;
            if(shadowTextureCoords.z - 0.001f > depth){
                    visible += 1.0;
            }
        }
    }

    visible /= totalPcfPixels;

    float inShadow = 1.0 - (shadowTextureCoords.w*visible*0.7f);
/*
    float inShadow;
    float depth = texture(shadowMap, shadowTextureCoords.xy).r;
    if(shadowTextureCoords.z - 0.001f > depth){
        inShadow = 1.0 - (shadowTextureCoords.w*0.4f);
    }else{
        inShadow = 1.0f;
    }*/

    vec2 tex = texPass;
    tex.x *= 10;
    tex.x -= floor(tex.x);

    vec2 normalTex = texPass;
    bool normalMapping = true;
    vec3 unitNormal;
    if(normalMapping){
        normalTex.x *= 10;
        normalTex.x -= floor(normalTex.x);
        normalTex.y *= 2;
        normalTex.y -= floor(normalTex.y);

        if(0.15 < tex.y && tex.y < 0.85){
            unitNormal = reflect(normalize(texture(bumpmap,normalTex).rgb * 2.0 - 1.0), normalize(normalVector));
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

	color = vec4(change,1.0) * texture(textureRoad,tex) * inShadow;
}