#version 400 core

layout(location = 0) out vec4 color;

// Images and texture coords.
uniform sampler2D speedMeter;
in vec2 texSpeedMeter;

uniform sampler2D itemBox;
in vec2 texItemBox;

uniform sampler2D item;
uniform int itemNum;
in vec2 texItem;

uniform sampler2D positions;
uniform int positionNum;
in vec2 texPosition;

uniform sampler2D numbers;
uniform int time1;
uniform int time2;
uniform int time3;
uniform int time4;
in vec2 texNumber1;
in vec2 texNumber2;
in vec2 texColon;
in vec2 texNumber3;
in vec2 texNumber4;


// Constants and variables.
// Number sheet
#define NUMBER_SHEET_WIDTH 256
#define NUMBER_WIDTH 24.0f
#define NUMBER_COLON_WIDTH 16.0f
// Item sheet
#define ITEM_SHEET_SIZE vec2(256, 64)
#define ITEM_SIZE vec2(64, 64)
#define ITEM_DIM ITEM_SIZE / ITEM_SHEET_SIZE
// Position sheet
#define POS_SHEET_SIZE vec2(256, 64)
#define POS_SIZE vec2(64, 64)
#define POS_DIM ITEM_SIZE / ITEM_SHEET_SIZE


float numberFactor = NUMBER_WIDTH / NUMBER_SHEET_WIDTH;
float colonFactor = NUMBER_COLON_WIDTH / NUMBER_SHEET_WIDTH;


// Functions.
vec4 getColor(sampler2D img, vec2 tex);
vec4 getNumColor(sampler2D img, vec2 tex, int num);
vec4 getMultiImage(sampler2D img, vec2 tex, vec2 pos, vec2 f);
vec4 addColor(vec4 back, vec4 front);


void main() {
    color = vec4(0, 0, 0, 0);
    
    color += getColor(itemBox, texItemBox);
    color = addColor(color,
            getMultiImage(item, texItem, vec2(itemNum, 0), ITEM_DIM));

    color += getColor(speedMeter, texSpeedMeter);
    
    color += getMultiImage(positions, texPosition, vec2(positionNum, 0), POS_DIM);
    
    color += getNumColor(numbers, texNumber1, time1);
    color += getNumColor(numbers, texNumber2, time2);
    color += getNumColor(numbers, texColon, 10);
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
        if (num < 10) {
            return texture(img, vec2((tex.x + num) * numberFactor, tex.y));
        } else {
            return texture(img,
                vec2(tex.x * colonFactor + num * numberFactor, tex.y));
        }
    }
}

vec4 getMultiImage(sampler2D img, vec2 tex, vec2 pos, vec2 f) {
    if (tex.x <= 0 || tex.x >= 1 ||
            tex.y <= 0 || tex.y >= 1) {
        return vec4(0, 0, 0, 0);
        
    } else {
        return texture(img, (tex + pos) * f);
    }
}

vec4 addColor(vec4 back, vec4 front) {
    if (front.a <= 0) return back;
    if (back.a <= 0) return front;
    if (front.a >= 1) return front;
    if (back.a + front.a <= 1) return back + front;
    return vec4(back.rgb, 1 - front.a) + front;
}
