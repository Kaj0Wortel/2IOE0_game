#version 400 core

layout(location = 0) out vec4 color;

// Images and texture coords.
uniform sampler2D speedMeter;
in vec2 texSpeedMeter;

uniform sampler2D itemBox;
in vec2 texItemBox;

uniform sampler2D numbers;
uniform int time1;
uniform int time2;
uniform int time3;
uniform int time4;
in vec2 texNumber1;
in vec2 texNumber2;
in vec2 texNumber3;
in vec2 texNumber4;


// Functions.
vec4 getColor(sampler2D img, vec2 tex);
vec4 getNumColor(sampler2D img, vec2 tex, int num);

// Constants and variables.
#define NUMBER_WIDTH 25.0f
#define NUMBER_SHEET_WIDTH 256

float numberFactor = NUMBER_WIDTH / NUMBER_SHEET_WIDTH;


void main() {
    color = vec4(0, 0, 0, 0);
    color += getColor(speedMeter, texSpeedMeter);
    color += getColor(itemBox, texItemBox);
    color += getNumColor(numbers, texNumber1, time1);
    color += getNumColor(numbers, texNumber2, time2);
    color += getNumColor(numbers, texNumber3, time3);
    color += getNumColor(numbers, texNumber4, time4);
}

vec4 getColor(sampler2D img, vec2 tex) {
    if (tex.x <= 0 || tex.x > 1 ||
            tex.y <= 0 || tex.y > 1) {
        return vec4(0, 0, 0, 0);
    } else {
        return texture(img, tex);
    }
}

vec4 getNumColor(sampler2D img, vec2 tex, int num) {
    if (tex.x <= 0 || tex.x >= 1 ||
            tex.y <= 0 || tex.y >= 1) {
        return vec4(0, 0, 0, 0);
    } else {
        return texture(img, vec2((tex.x + num) * numberFactor, tex.y));
    }
}

