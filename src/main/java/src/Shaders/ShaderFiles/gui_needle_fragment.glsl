#version 400 core

layout(location = 0) out vec4 color;

// Images
uniform sampler2D speedNeedle;
in vec2 texSpeedNeedle;

uniform bool finished;


// Functions
vec4 getColor(sampler2D img, vec2 tex);


void main() {
    color = vec4(0, 0, 0, 0);
    if(!finished){
        color += getColor(speedNeedle, texSpeedNeedle);
    }
}

vec4 getColor(sampler2D img, vec2 tex) {
    if (tex.x <= 0 || tex.x >= 1 ||
            tex.y <= 0 || tex.y >= 1) {
        return vec4(0, 0, 0, 0);
    } else {
        return texture(img, tex);
    }
}