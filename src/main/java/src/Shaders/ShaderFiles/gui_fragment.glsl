#version 400 core

in vec2 texPass;

layout(location = 0) out vec4 color;

uniform sampler2D gui;

void main() {
    color = texture(gui, texPass).rgba;
}
