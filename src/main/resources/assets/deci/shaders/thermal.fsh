#version 120
uniform sampler2D diffuseTex;
uniform sampler2D depthTex;
uniform vec2 resolution;

void main() {
    float pixelScale = 4.0;
    vec2 pixelatedUV = floor(gl_FragCoord.xy / pixelScale) * pixelScale / resolution;

    vec4 sceneCol = texture2D(diffuseTex, pixelatedUV);
    float depth = texture2D(depthTex, pixelatedUV).r;

    if (depth >= 0.999) {
        gl_FragColor = vec4(0.0, 0.0, 0.05, 1.0);
        return;
    }

    float lum = dot(sceneCol.rgb, vec3(0.299, 0.587, 0.114));

    float blueMask = smoothstep(0.4, 0.7, sceneCol.b / (sceneCol.r + sceneCol.g + 0.1));

    float chroma = abs(sceneCol.r - sceneCol.g) + abs(sceneCol.g - sceneCol.b);
    if (sceneCol.b > sceneCol.r && sceneCol.b > sceneCol.g) {
        chroma *= 0.2;
    }

    float heat = (smoothstep(0.3, 0.9, lum) * 3.0) + (chroma * 3.0);

    heat *= (1.0 - blueMask);

    heat = clamp(heat, 0.0, 1.0);

    vec3 color;
    if (heat < 0.2) {
        color = mix(vec3(0.0, 0.0, 0.15), vec3(0.0, 0.3, 0.6), heat / 0.2);
    } else if (heat < 0.5) {
        color = mix(vec3(0.0, 0.3, 0.6), vec3(1.0, 1.0, 0.0), (heat - 0.2) / 0.3);
    } else if (heat < 0.8) {
        color = mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 0.2, 0.0), (heat - 0.5) / 0.3);
    } else {
        color = vec3(1.0, 1.0, 1.0);
    }

    gl_FragColor = vec4(color, 1.0);
}