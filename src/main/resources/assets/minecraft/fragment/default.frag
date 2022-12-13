#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform float alpha0;

uniform vec3 color;

uniform float radius;
uniform float divider;
uniform float maxSample;
uniform vec2 resolution;
uniform float time;

vec3 getColor(vec4 centerCol) {
    return vec3(-1);
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
                    if (colorFinal[0] == -1) {
                        colorFinal = getColor(centerCol);
                    }
                    alphaOutline += divider > 0 ? max(0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
                }
                else {
                    gl_FragColor = vec4(getColor(centerCol), alpha0);
                    return;
                }
            }
        }
        gl_FragColor = vec4(colorFinal, alphaOutline);
    }
}