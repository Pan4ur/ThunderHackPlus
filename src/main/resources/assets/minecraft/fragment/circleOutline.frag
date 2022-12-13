#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform float alpha0;

uniform vec3 colors;

uniform float radius;
uniform float divider;
uniform float maxSample;
uniform vec2 resolution;
uniform float time;

uniform float PI;
uniform float rad;


vec3 getColor(vec4 centerCol) {
    vec2 position = ( gl_FragCoord.xy / resolution.xy );
    float y = sin(time * 4.0) * rad;
    float x = cos(time * 4.0) * rad;
    float y2 = sin(time * 3.1) * rad;
    float x2 = cos(time * 3.1) * rad;
    float y3 = sin(time * 1.2) * rad;
    float x3 = cos(time * 1.2) * rad;

    float color = colors[0];
    color += sin(x + position.x * PI * 1.0) * sin(y + position.y * PI * 1.0);
    float color2 = colors[1];
    color2 += sin(x2 + position.x * PI * 1.0) * sin(y2 + position.y * PI * 1.0);
    float color3 = colors[2];
    color3 += sin(x3 + position.x * PI * 1.0) * sin(y3 + position.y * PI * 1.0);
    return vec3(color, color2, color3);
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