#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform sampler2D texture;

uniform float time;
uniform vec2 resolution;
uniform vec4 rgba;
uniform int lines;
uniform float tau;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if(centerCol.a == 0.0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {
        float letime = time * .5+23.0;
        // uv should be the 0-1 uv of texture...
        vec2 uv = gl_FragCoord.xy / resolution.xy;

        #ifdef SHOW_TILING

        vec2 p = mod(uv*tau*2.0, tau)-250.0;
    #else
    vec2 p = mod(uv*tau, tau)-250.0;
    #endif
    vec2 i = vec2(p);
    float c = 1.0;
    float inten = .005;

    for (int n = 0; n < lines; n++)
    {
        float t = letime * (1.0 - (3.5 / float(n+1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten) ,p.y / (cos(i.y+t)/inten)));
    }
    c /= float(lines);
    c = 1.17-pow(c, 1.4);
    vec3 colour = vec3(pow(abs(c), 8.0));
    colour = clamp(colour + vec3(rgba[0], rgba[1], rgba[2]), 0.0, 1.0);


    #ifdef SHOW_TILING
    // Flash tile borders...
    vec2 pixel = 100000000.0 / iResolution.xy;
    uv *= 2.0;

    float f = floor(mod(iTime*.5, 2.0)); 	// Flash value.
    vec2 first = step(pixel, uv) * f;		   	// Rule out first screen pixels and flash.
    uv  = step(fract(uv), pixel);				// Add one line of pixels per tile.
    colour = mix(colour, vec3(1.0, 1.0, 0.0), (uv.x + uv.y) * first.x * first.y); // Yellow line

    #endif
    gl_FragColor = vec4(colour, rgba[3]);
        }
}