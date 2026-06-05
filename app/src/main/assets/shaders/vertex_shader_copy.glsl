#version 300 es
precision mediump float;

in vec4 aFramePosition;
in vec2 aTexSamplingCoord;
out vec2 vTexSamplingCoord;

void main() {
    gl_Position = aFramePosition;
    vTexSamplingCoord = aTexSamplingCoord;
}

