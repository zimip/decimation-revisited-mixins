#version 120

uniform sampler2D depthTex;
uniform vec2 resolution;
uniform float worldTime;

const vec3 luceTorcia = vec3(0.77, 0.8, 0.9);
const float intensitaBase = 1.25;
const float zNear = 0.096;
const float zFar = 256.0;

const float swayVelocita = 1;
const float swayAmpiezza = 0.004;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    float tempo = worldTime * swayVelocita;
    vec2 offsetSway = vec2(
    sin(tempo) * swayAmpiezza,
    cos(tempo * 2.0) * swayAmpiezza * 0.5
    );

    float depthZ = texture2D(depthTex, uv).r;
    float ndc = 2.0 * depthZ - 1.0;
    float linearDepth = (2.0 * zNear * zFar) / (zFar + zNear - ndc * (zFar - zNear));

    vec2 centerDist = uv - (vec2(0.5, 0.5) + offsetSway);
    centerDist.x *= (resolution.x / resolution.y);
    float distFromCenter = length(centerDist);

    float expansion = 0.15 / (linearDepth + 0.5);

    float core = smoothstep(0.288 + expansion, 0.0, distFromCenter);
    float halo = smoothstep(0.4 + expansion, 0.0, distFromCenter) * 0.4;
    float totalSpot = core + halo;

    vec4 finalColor = vec4(0.0);

    if (depthZ < 1.0) {
        float attenuation = 1.0 / (1.0 + (linearDepth * 0.07) + (linearDepth * linearDepth * 0.0025));
        float intensity = totalSpot * attenuation * intensitaBase;

        finalColor.rgb = luceTorcia * intensity;
        finalColor.a = intensity * 0.9;
    }

    gl_FragColor = finalColor;
}