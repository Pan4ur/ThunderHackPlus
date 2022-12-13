
#define _NUMSHEETS 8.
#define _NUMFLAKES 400.

vec2 uv;


uniform vec2 resolution;
uniform float     time;


float rnd(float x)
{
    return fract(sin(dot(vec2(x+47.49,38.2467/(x+2.3)), vec2(12.9898, 78.233)))* (43758.5453));
}

float drawFlake(vec2 center, float radius)
{
    return 1.0 - sqrt(smoothstep(0.0, radius, length(uv - center)));
}

void main( void )
{
    uv = (gl_FragCoord.xy / resolution.x)/2;
    vec3 col = vec3(0.05, .05, .1);
    for (float i = 1.; i <= _NUMSHEETS; i++){
        for (float j = 1.; j <= _NUMFLAKES; j++){
            if (j > _NUMFLAKES/i) break;
            float size = 0.0013 * i * (1. + rnd(j)/2.);
            float speed = size * .75 + rnd(i) / 10.5;
            vec2 center = vec2(0., 0.);
            center.x = -.3 + rnd(j*i) * 1.4 + 0.1*cos(time+sin(j*i));
            center.y = fract(sin(j) - speed *time) / 1.3;
            col += vec3( (1. - i/_NUMSHEETS) * drawFlake(center, size));
        }
    }
	gl_FragColor = vec4(col,1.0);
}