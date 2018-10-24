#version 400 core

layout(location = 0) in vec2 position;

uniform mat4 transformationMatrix;
uniform float screenRatio;

// Texture output.
out vec2 texSpeedMeter;

out vec2 texItemBox;
out vec2 texItem;

out vec2 texPosition;

out vec2 texNumber1;
out vec2 texNumber2;
out vec2 texColon;
out vec2 texNumber3;
out vec2 texNumber4;

out vec2 texLap;
out vec2 texCurLap;
out vec2 texMaxLap;

out vec2 texWin;

// Functions
vec2 calcTexCoords(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioTop(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioBottom(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioAnchor(vec2 pos, vec2 size, vec2 anchor);


void main() {
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
    
    texSpeedMeter = calcTexCoordsKeepRatioAnchor(
            vec2(0.725f, 0.075f),
            vec2(0.4f, 0.4f),
            vec2(0.0f, 0.5f));
    
    texItemBox = calcTexCoordsKeepRatioAnchor(
            vec2(0.0f, 1.0f),
            vec2(0.1f, 0.1f),
            vec2(0.0f, 1.0f));
    texItem = calcTexCoordsKeepRatioAnchor(
            vec2(0.0f, 1.0f),
            vec2(0.1f, 0.1f),
            vec2(0.0f, 1.0f));
    
    texPosition = calcTexCoordsKeepRatioAnchor(
            vec2(1.0f, 0.85f),
            vec2(0.1f, 0.1f),
            vec2(1.0f, 1.0f));

    texWin = calcTexCoordsKeepRatioAnchor(
                 vec2(0.5f, 0.5f),
                 vec2(0.5f, 0.5f),
                 vec2(0.5f, 0.5f));
    
    vec2 start = vec2(0.45, 0.975f);
    texNumber1 = calcTexCoordsKeepRatioAnchor(
            start,
            vec2(0.02f, 0.04f),
            vec2(0.0f, 1.0f));
    texNumber2 = calcTexCoordsKeepRatioAnchor(
            start + vec2(0.025f, 0),
            vec2(0.02f, 0.04f),
            vec2(0.0f, 1.0f));
    texColon = calcTexCoordsKeepRatioAnchor(
            start + vec2(0.050f, 0),
            vec2(0.02f, 0.04f),
            vec2(0.0f, 1.0f));
    texNumber3 = calcTexCoordsKeepRatioAnchor(
            start + vec2(0.075f, 0),
            vec2(0.02f, 0.04f),
            vec2(0.0f, 1.0f));
    texNumber4 = calcTexCoordsKeepRatioAnchor(
            start + vec2(0.100f, 0),
            vec2(0.02f, 0.04f),
            vec2(0.0f, 1.0f));
    
    start = vec2(0.99f, 0.98f);
    texLap = calcTexCoordsKeepRatioAnchor(
            start,
            vec2(0.12f, 0.051f),
            vec2(1.0f, 1.0f));
    texCurLap = calcTexCoordsKeepRatioAnchor(
            start + vec2(-0.045f, -0.01f),
            vec2(0.02f, 0.04f),
            vec2(1.0f, 1.0f));
    texMaxLap = calcTexCoordsKeepRatioAnchor(
            start + vec2(0, -0.01f),
            vec2(0.02f, 0.04f),
            vec2(1.0f, 1.0f));
}

/**
 * Calculates the texture coordinates such that the image has
 * the desired position and size, centered around the given yAnker,
 * and scaled with the screen ratio.
 * 
 * @param pos the position of the image, lower-left corner is (0, 0),
 *     upper-right corner is (1, 1).
 * @param size the size of the image.
 * @param anchor the anchor of the image.
 */
vec2 calcTexCoordsKeepRatioAnchor(vec2 pos, vec2 size, vec2 anchor) {
    vec2 dim = vec2(size.x, size.y);
    vec2 scaleWindow = vec2(1, screenRatio);
    
    return ((position - pos) / scaleWindow) / dim + anchor;
}

