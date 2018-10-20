#version 400 core

layout(location = 0) in vec2 position;

uniform mat4 transformationMatrix;
uniform float screenRatio;
uniform float sinAngle;
uniform float cosAngle;


// Texture output.
out vec2 texSpeedNeedle;


// Functions
vec2 calcTexCoordsKeepRatioAnchor(vec2 pos, vec2 size, vec2 anchor,
            float sinAngle, float cosAngle);


void main() {
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
    texSpeedNeedle = calcTexCoordsKeepRatioAnchor(
            vec2(1.0f, 0.0f),
            vec2(0.05f, 0.2f),
            vec2(0.5, 0.11), sinAngle, cosAngle);
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
 * @param sinAngle the sinus of the angle to rotate over.
 * @param cosAngle the cosine of the angle to rotate over.
 * 
 * Note that the image is only scale in the y-axis.
 */
vec2 calcTexCoordsKeepRatioAnchor(vec2 pos, vec2 size, vec2 anchor,
            float sinAngle, float cosAngle) {
    vec2 dim = vec2(size.x, size.y);
    mat2 rotMat = mat2(cosAngle, sinAngle, -sinAngle, cosAngle);
    vec2 scaleWindow = vec2(1, screenRatio);
    
    return (rotMat * ((position - pos) / scaleWindow)) / dim + anchor;
}
