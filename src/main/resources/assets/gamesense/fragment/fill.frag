#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;

uniform vec4 color;







void main()
{
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if (centerCol.a == 0.0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {
        gl_FragColor = color;
    }

}