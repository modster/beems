#version 300 es
precision mediump float;

uniform sampler2D uTexSampler;
in vec2 vTexSamplingCoord;
out vec4 outColor;

void main() {
    // 1. Fetch the raw original frame texture pixel
    vec4 texColor = texture(uTexSampler, vTexSamplingCoord);

    // 2. Map coordinates to center-relative positions (-0.5 to 0.5)
    vec2 centerCoord = vTexSamplingCoord - vec2(0.5, 0.5);

    // 3. Compute distance from center to determine falloff radius
    float distanceToCenter = length(centerCoord);

    // 4. Calculate smooth interpolation vignette boundaries
    // Inner radius: 0.35, Outer edge boundary: 0.75
    float vignetteFactor = smoothstep(0.35, 0.75, distanceToCenter);

    // 5. Darken RGB channels progressively toward the screen borders
    vec3 finalRgb = texColor.rgb * (1.0 - vignetteFactor * 0.75);

    outColor = vec4(finalRgb, texColor.a);
}
