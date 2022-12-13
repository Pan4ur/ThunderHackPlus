#version 120
uniform float radius;
uniform float divider;
uniform float maxSample;
uniform vec2 resolution;
uniform float time;

#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D texture;
uniform vec2 texelSize;
uniform float alpha0;

uniform float alpha;
uniform float WAVELENGTH;
#define C 555.0
uniform int R;
uniform int RSTART;
#define R_CENTER vec2(RSTART, R)
uniform int G;
uniform int GSTART;
#define G_CENTER vec2(GSTART, G)
uniform int B;
uniform int BSTART;
#define B_CENTER vec2(BSTART, B)

float wave(vec2 c, vec2 pos)
{
    float d = distance(c, pos);
    return 1000000.0 * sin((d - time * C) / WAVELENGTH);
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
                    vec2 pos = gl_FragCoord.xy;
                    vec4 test = vec4(
                    wave(R_CENTER, pos),
                    wave(G_CENTER, pos),
                    wave(B_CENTER, pos),
                    alpha);
                    gl_FragColor = vec4(vec3(test[0], test[1], test[2]), alpha0);
                    return;
                }
            }
        }
        if (alphaOutline > 0) {
            vec2 pos = gl_FragCoord.xy;
            vec4 test = vec4(
            wave(R_CENTER, pos),
            wave(G_CENTER, pos),
            wave(B_CENTER, pos),
            alpha);
            colorFinal = vec3(test[0], test[1], test[2]);
        }
        gl_FragColor = vec4(colorFinal, alphaOutline);
    }
}