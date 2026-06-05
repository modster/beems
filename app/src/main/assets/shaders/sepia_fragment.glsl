#version 300 es
precision mediump float;

uniform sampler2D uTexSampler;
uniform mat4 uColorMatrix;
in vec2 vTexSamplingCoord;
out vec4 outColor;

void main() {
    // 1. Fetch the raw original frame texture pixel
    vec4 texColor = texture(uTexSampler, vTexSamplingCoord);

    // 2. Apply the sepia color transformation using the provided matrix
    outColor = uColorMatrix * texColor;
}
