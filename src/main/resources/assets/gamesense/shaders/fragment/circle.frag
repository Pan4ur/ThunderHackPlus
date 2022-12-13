#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform sampler2D texture;

uniform float time;
uniform vec2 resolution;

uniform float PI;
uniform float rad;

uniform vec4 colors;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if(centerCol.a == 0.0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {

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

        gl_FragColor = vec4( color, color2, color3, colors[3] );
    }
}