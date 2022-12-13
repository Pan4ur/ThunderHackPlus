#version 120


#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable


uniform int NUM_OCTAVES;
uniform float time;
uniform float Creepy;
uniform float moreGradient;
uniform float alpha;

uniform sampler2D texture;
uniform vec2 texelSize;
uniform float alpha0;

uniform vec3 color;

uniform float radius;
uniform float divider;
uniform float maxSample;
uniform vec2 resolution;


float random(vec2 pos) {
    return fract(sin(dot(pos.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 pos) {
    vec2 i = floor(pos);
    vec2 f = fract(pos);
    float a = random(i + vec2(0.0, 0.0));
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 pos) {
    float v = 0.0;
    float a = 0.5;
    mat2 rot = mat2(cos(0.1), sin(0.5), -sin(0.5), cos(0.5));
    for (int i=0; i<NUM_OCTAVES; i++) {
        v += a * noise(pos);
        a *= 0.5;
    }
    return v;
}

vec3 getColor(vec4 centerCol) {
    float minrz = min(resolution.x, resolution.y);
    vec2 p = (((vec2(2.0, 2.0) * gl_FragCoord.xy) - resolution.xy) * vec2((moreGradient / min(resolution.x, resolution.y)), (moreGradient / min(resolution.x, resolution.y))));

    float t = 0.0;

    float time2 = 3.0 * time / 2.0;

    vec2 q = vec2(0.0);
    q.x = fbm(p + 0.00);
    q.y = fbm(p + vec2(1.0));

    vec4 temp = vec4(vec3(noise(p + vec2(1.0)),
    noise(p + Creepy * q + vec2(1.7, 9.2) + 0.15 * time2),
    noise(p + Creepy * q + vec2(8.3, 2.8) + 0.126 * time2)),
    alpha);
    return vec3(temp[0], temp[1], temp[2]);
}

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if(centerCol.a != 0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {

        float alphaOutline = 0;
        vec3 colorFinal = vec3(-1);

        for (float x = -radius; x < radius; x++) {
            for (float y = -radius; y < radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

                if (currentColor.a != 0)
                if (alpha0 == -1.0) {

                    alphaOutline += divider > 0 ? max(0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
                }
                else {
                    gl_FragColor = vec4(getColor(centerCol), alpha0);
                    return;
                }
            }
        }
        if (alphaOutline > 0) {
            colorFinal = getColor(centerCol);
        }
        gl_FragColor = vec4(colorFinal, alphaOutline);
    }
}