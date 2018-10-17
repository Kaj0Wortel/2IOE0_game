
#version 400 core

in vec3 normalVector;
in vec3 toLight;
in vec3 toCamera;
in vec4 shadowTextureCoords;

layout(location = 0) out vec4 color;

uniform vec3 lightColor;
uniform float shininess;
uniform float reflectivity;

uniform vec3 matAmbient;
uniform vec3 matDiffuse;
uniform vec3 matSpecular;

uniform sampler2D shadowMap;
const int pcfPixels = 3;
const float totalPcfPixels = pow((pcfPixels * 2 + 1),2);

void main() {


    float depthMapSize = 4096.0;
    float pixelSize = 1 / depthMapSize;

    float visible = 0.0;
    for(int i = -pcfPixels; i < pcfPixels + 1; i++){
        for(int j = -pcfPixels; j < pcfPixels + 1; j++){
            float depth = texture(shadowMap, shadowTextureCoords.xy + vec2(i,j)*pixelSize).r;
            if(shadowTextureCoords.z - 0.01f > depth){
                    visible += 1.0;
            }
        }
    }


    visible /= totalPcfPixels;

    float inShadow = 1.0 - (shadowTextureCoords.w*visible*0.6f);

    vec3 unitNormal = normalize(normalVector);
    vec3 unitLight = normalize(toLight);
    float d = dot(unitNormal, unitLight);
    d = max(d,0.3);
    vec3 change = d * lightColor * matDiffuse;

    vec3 unitCamera = normalize(toCamera);
    vec3 invLight = -unitLight;
    vec3 ref = reflect(invLight, unitNormal);
    float dotSpec = dot(unitCamera, ref);
    dotSpec = max(dotSpec, 0.0);
    dotSpec = pow(dotSpec, shininess);
    vec3 specular = dotSpec * lightColor * matSpecular;


	color = vec4(change,1.0) * inShadow + vec4(specular,1.0);
}
