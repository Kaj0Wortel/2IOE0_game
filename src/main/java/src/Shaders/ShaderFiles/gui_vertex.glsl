#version 400 core

layout(location = 0) in vec2 position;

out vec2 texPass;

uniform mat4 transformationMatrix;

//out vec2 texTest;

/*
out vec2 texSpeedMeter;
out vec2 texSpeedNeedle;
out vec2 texTime;
out vec2 texPlace;
out vec2 texItemInv;
out vec2 texItem;
*/
void main() {
    
    texPass = vec2((position.x + 1.0) / 2, 1 - ((position.y + 1.0) / 2));
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
    
    //texTest = position;
    /*
    texTest = vec2(0, 0);
    texSpeedMeter = vec2(0, 0);
    texSpeedNeedle = vec2(0, 0);
    texTime = vec2(0, 0);
    texPlace = vec2(0, 0);
    texItemInv = vec2(0, 0);
    texItem = vec2(0, 0);
    */
    
    
}
