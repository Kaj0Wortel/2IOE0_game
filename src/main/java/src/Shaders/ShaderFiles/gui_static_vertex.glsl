#version 400 core

layout(location = 0) in vec2 position;

uniform mat4 transformationMatrix;
uniform float screenRatio;

// Texture output.
out vec2 texSpeedMeter;
out vec2 texItemBox;
out vec2 texNumber1;
out vec2 texNumber2;
out vec2 texNumber3;
out vec2 texNumber4;


// Functions
vec2 calcTexCoords(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioTop(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioBottom(vec2 pos, vec2 size);
vec2 calcTexCoordsKeepRatioAnkerY(vec2 pos, vec2 size, float anker);


void main() {
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);

    texItemBox = calcTexCoordsKeepRatioAnkerY(
            vec2(0.9f, 1.0f),
            vec2(0.1f, 0.1f), 1.0f);
    texSpeedMeter = calcTexCoordsKeepRatioAnkerY(
            vec2(0.8f, 0.0f),
            vec2(0.4f, 0.4f), 0.5f);
    
    texNumber1 = calcTexCoordsKeepRatioAnkerY(
            vec2(0.4f, 0.975f),
            vec2(0.02f, 0.04f), 1.0f);
    texNumber2 = calcTexCoordsKeepRatioAnkerY(
            vec2(0.425f, 0.975f),
            vec2(0.02f, 0.04f), 1.0f);
    texNumber3 = calcTexCoordsKeepRatioAnkerY(
            vec2(0.45f, 0.975f),
            vec2(0.02f, 0.04f), 1.0f);
    texNumber4 = calcTexCoordsKeepRatioAnkerY(
            vec2(0.475f, 0.975f),
            vec2(0.02f, 0.04f), 1.0f);
}


vec2 calcTexCoords(vec2 pos, vec2 size) {
    return (position - pos) / size;
}

/**
 *  Old funcion.
 * Equivalent to:
 * {@code calcTexCoordsKeepRatioAnkerY(pos, size, 1.0f)}
 */
vec2 calcTexCoordsKeepRatioTop(vec2 pos, vec2 size) {
    vec2 p = vec2(position.x - pos.x,
            position.y - (pos.y + size.y * (1 - screenRatio)));
    return vec2(p.x / size.x, p.y / (size.y * screenRatio));
}

/**
 *  Old funcion.
 * Equivalent to:
 * {@code calcTexCoordsKeepRatioAnkerY(pos, size, 0.0f)}
 */
vec2 calcTexCoordsKeepRatioBottom(vec2 pos, vec2 size) {
    vec2 p = vec2(position.x - pos.x,
            position.y - pos.y);
    return vec2(p.x / size.x, p.y / (size.y * screenRatio));
}

/**
 * Calculates the texture coordinates such that the image has
 * the desired position and size, centered around the given yAnker,
 * and scaled with the screen ratio.
 * 
 * @param pos the position of the image, lower-left corner is (0, 0),
 *     upper-right corner is (1, 1).
 * @param size the size of the image.
 * @param yAnker the anker of the image on the y axis.
 * 
 * Note that the image is only scale in the y-axis.
 */
vec2 calcTexCoordsKeepRatioAnkerY(vec2 pos, vec2 size, float yAnker) {
    vec2 dim = vec2(size.x, size.y * screenRatio);
    vec2 p = vec2(position.x - pos.x,
            (position.y - (pos.y - yAnker*dim.y)));
    return vec2(p.x / dim.x, p.y / dim.y);
}


