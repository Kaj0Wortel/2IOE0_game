#version 400 core

layout(location = 0) in vec2 position;

out vec2 texPass;

uniform mat4 transformationMatrix;

void main() {

    texPass = vec2((position.x + 1.0) / 2, 1 - ((position.y + 1.0) / 2));
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);

}
