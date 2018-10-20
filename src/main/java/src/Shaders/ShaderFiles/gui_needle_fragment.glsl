#version 400 core

layout(location = 0) out vec4 color;

// Images
uniform sampler2D speedNeedle;
in vec2 texSpeedNeedle;


void main() {
    color = vec4(0, 0, 0, 0);
    color += getColor(speedMeter, texSpeedMeter);
    color += getColor(itemBox, texItemBox);
}