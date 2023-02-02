
//не читай это сообщение)) не нуж но))) конык ига га смийеться над тобою аа))) ах ты ж воображение пахаха))
//подпись форева: Cyber DragOn С🐉йбер
// russian fag ^^
 
#ifdef GL_ES
precision highp float;
#endif
uniform vec2 resolution;
uniform float time; //время идти всем на...) баю баю не зеваю в сон твой не вхожу с хуя я это пишу аа?)) а в твоей глове одни хуи а?) айай ц ц )) йа йа йа онли форевар я)))
 
vec3 Smeg()
{
	vec2 position = ( gl_FragCoord.xy /resolution.xy );
	position.x = dot(position,position)*0.77;
 
	position.y += 0.2;		// MEAT
	position.y *= 0.8;		// TWO VEG
	
	float smeg = 0.4;							// JUNK
	if (position.y < 0.3 + (sin(position.x * 25.+time*2.) + cos(position.x * 35.+time*1.5)) / 11. || 
	   position.y > 0.8 + (sin(position.x * 12.+time*2.1) ) / 8.) 		// LOVE MUSCLE
		smeg = .0;							// PINK OBOE
 
	return vec3(smeg, smeg, smeg*0.9)*length(position)*1.65;		// TALLY WACKER
}
 
float pMod1(inout float p, float size)
{
	float halfsize = size*0.5;						// FUCK STICK
	float c = floor((p + halfsize)/size);					// BULGE
	p = mod(p + halfsize, size) - halfsize;					// PUSSY HAMMER
	return c;								// PEN15
}
vec2 cum(vec2 v, float angle,float cadj)
{
    float c = cos(cadj+angle);							// MANFAT
    float s = sin(angle);							// ONE-EYED TROUSER SNAKE
    return v*mat2(c, -s, s, c);							// WANG
}
float rect(vec2 p, vec2 s ){
if(length(max(abs(p)-s,0.0))==0.0){
return 0.75;					// GONAD
}
return 0.0;
}
void main( void ) {
vec2 pos = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.y;
	float rf = sqrt(dot(pos, pos)) * .75;
	float rf2_1 = rf * rf + 1.0;
	float e = 1.0 / (rf2_1 * rf2_1);	
	
vec3 col = vec3(0.4,0.35,0.45)*(1.0-abs(pos.y))*1.95;			// WILLY
	col.b *= 0.9+sin(time*1.8+pos.x+pos.y)*0.2;
	col.g *= 1.0+sin(time*1.4+pos.y*0.5*pos.x*6.0)*0.2;		// PURPLE BATTERING RAM
	col.r *= 0.8+sin(time*1.31+pos.x*0.7)*0.25;			// PACKAGE
	col -= vec3(0.1);						// DOME
	col+=Smeg()*2.2;						// GIRTH
	
	pos *= 2.1+sin(time*0.64)*0.4;					// PRICK
	
pos = cum(pos,time*0.2,0.0);
	
pos = cum(pos+vec2(0.0,-0.8),pos.x*((sin(time*1.31)*.15)),sin(pos.x*.125));
	pos.y += 0.8;
	
	pos.x -= time;							// CHOAD
	float cc = pMod1(pos.y,0.58);
	pos.x += sin(cc);						// BONER
	float cc2 = pMod1(pos.x,1.15);					// SCROTUM
	
	pos *= 1.0+sin(cc*5.5+cc2*0.2+time*.24+pos.x*0.8)*0.15;		// ROD
	
float ppy = pos.y;							// WEINER
pos+=vec2(0.9,0.5);							// BELLEND
 
float x = 0.9 + 0.1 * sin(cc2*2.175+cc*0.75+pos.x+pos.y+13.0* time);
float y = 0.55; 							// HELMET
float b = 0.0;
b += rect(pos-vec2(x,y), vec2(0.3, 0.09));				// SHAFT
if( length(pos - (vec2(x,y) - vec2(0.3,0.1))) < 0.1) {			// WOOD
b += 0.75; 								// NUTSACK
}
 
if( length(pos - (vec2(x,y) - vec2(0.3,-0.1))) < 0.1) {
b += 0.75; 								// KNOB
}
if( length(pos - (vec2(x,y) - vec2(-0.3,0.0))) < 0.1) {
b += 0.75; 								// TROUSER SNAKE
}
 
vec3 col2 = col;
if( rect(pos-vec2(x+.42,y), vec2(0.06, 0.005)) == 0.0 )			// BONE
{
	col2.x = clamp( b, 0.0, 0.75); 				// WANG
	col2.y = clamp( b, 0.0, .28); 				// DICK
	col2.z = clamp( b, 0.0, 0.34); 				// PECKER
}
	
	b = 1.0-step(b,0.1);					// PENIS
	col2 = (col2*(0.95+sin(-1.2+cos(ppy*10.0))*0.3))*1.3;	// NUT
	col = mix(col,col2,b);					// JAPS EYE
 
	gl_FragColor = vec4(col*e, 1.0 );		// COMBINE FOR DANCING COCKS
}
