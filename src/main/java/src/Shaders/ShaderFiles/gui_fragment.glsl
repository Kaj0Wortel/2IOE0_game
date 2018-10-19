#version 400 core

in vec2 texPass;

layout(location = 0) out vec4 color;

uniform sampler2D gui;

// Images
//in vec2 texTest
uniform sampler2D test;

/*
in vec2 texSpeedMeter;
//uniform sampler2D speedMeter;

in vec2 texSpeedNeedle;
//uniform sampler2D speedNeedle;

in vec2 texTime;
//uniform sampler2D time;

in vec2 texPlace;
//uniform sampler2D place;

in vec2 texItemInv;
//uniform sampler2D itemInv;

in vec2 texItem;
//uniform sampler2D item;
*/


void main() {
    color = texture(gui, texPass).rgba + texture(test, texPass).rgba;
}
