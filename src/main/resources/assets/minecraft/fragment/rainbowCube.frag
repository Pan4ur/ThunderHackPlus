#ifdef GL_ES
precision highp float;
#endif


uniform float time;
uniform vec2 resolution;
uniform sampler2D texture;

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

void main( void )
{
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
if(centerCol.a == 0.0) {
    gl_FragColor = vec4(centerCol.rgb, 0);
} else {
    vec2 pos = gl_FragCoord.xy;
    gl_FragColor = vec4(
    wave(R_CENTER, pos),
    wave(G_CENTER, pos),
    wave(B_CENTER, pos),
    alpha);
}
}