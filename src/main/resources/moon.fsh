// BEGIN: shadertoy porting template
// https://gam0022.net/blog/2019/03/04/porting-from-shadertoy-to-glslsandbox/
precision highp float;

uniform vec2 resolution;
uniform float time;
uniform vec2 mouse;

#define iResolution resolution
#define iTime time
#define iMouse mouse

void mainImage(out vec4 fragColor, in vec2 fragCoord);

void main(void) {
    vec4 col;
    mainImage(col, gl_FragCoord.xy);
    gl_FragColor = col;
}



// License: Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License

#define trail
#define letterboxfullscreen
//#define startinlake
//#define noplane
//#define onlymouse

#define resolution iResolution
#ifdef startinlake
    #define time mod(iTime*.83+120.,200.)
#else
    #define time mod(iTime*.83,200.)
#endif

float hashseed=0.;
float det=.01;
float maxdist=40.;
vec3 objcol;
vec3 ldir;
float id=0.,oc=1.,dcab=0.,t=0.,h=0.,speed=0.,baja=0.,ref=0.;
vec3 pos, pfus, pmot, pcab, parm, pwin, ptim, ptai, tpos;
vec3 ppos=vec3(0.),ppos2=vec3(0.);
mat2 rotyz, rotxy, rotxz;
float hash(float p) { p = fract(p * 0.011); p *= p + 7.5; p *= p + p; return fract(p); }
float hash(vec2 p) {vec3 p3 = fract(vec3(p.xyx) * 0.13); p3 += dot(p3, p3.yzx + 3.333); return fract((p3.x + p3.y) * p3.z); }

float interhash(float seed, float t) {
    t+=hash(seed+hashseed)*123.;
    return mix(hash(floor(t)),hash(floor(t+1.)),smoothstep(.3,1.,fract(t)));
}

float path(float t) {
    return sin(t+cos(t*.5687))*.5;
}


mat2 rot(float a) {
    float s=sin(a),c=cos(a);
    return mat2(c,s,-s,c);
}


float smin( float a, float b, float k )
{
    float h = max( k-abs(a-b), 0.0 )/k;
    return min( a, b ) - h*h*k*(1.0/4.0);
}

float ssub ( float d1, float d2, float k ) {
    float h = clamp( 0.5 - 0.5*(d2+d1)/k, 0.0, 1.0 );
    return mix( d2, -d1, h ) + k*h*(1.0-h); }

float box( vec3 p, vec3 b, float r )
{
  vec3 q = abs(p) - b;
  return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0) - r;
}

float capsule( vec3 p, float h, float r )
{
  p.z -= clamp( p.z, 0.0, h );
  return length( p ) - r;
}

float elipse( vec3 p, vec3 r )
{
  float k0 = length(p/r);
  float k1 = length(p/(r*r));
  return k0*(k0-1.0)/k1;
}

float cabina(vec3 p) {
    p.z+=1.9;
    p.y-=1.;
    p.y+=pow(max(0.,-p.z+1.)*.31,2.);
    p.y-=pow(max(0.,-p.z+3.5)*.3,1.5)*.1;
    float d=elipse(p,vec3(.35,.5,2.9));
    pcab=p;
    dcab=d;
    return d*.8;
}


float fuselaje(vec3 p, float cab) {
    vec3 p2=p;
    p.z+=5.5;
    p.y-=smoothstep(2.,0.,p.z)*.2*abs(p.y);
    p.y-=smoothstep(0.,4.,p.z)*.3;
    p.y*=1.+smoothstep(4.,0.,p.z)*.2;
    p.y-=smoothstep(2.,5.2,p.z)*.5*max(0.,p.y);
    p.y-=min(.5,max(0.,p.z-5.2)*.06);
    p.y*=1.+smoothstep(4.,13.,p.z)*.5;
    p.y-=smoothstep(6.,9.,p.z)*.7*max(0.,-p.y+.3);
    p.z*=1.+min(.2,max(0.,p.z-10.5-p.y)*.3);
    vec3 t=vec3(.55,.7,5.5);
    t.y*=1.-smoothstep(2.,11.,p.z)*.4;
    p.x*=1.+smoothstep(8.,9.,p.z)*.25;
    p.z-=5.5;
    float d=elipse(p,t);
    pfus=p;
    p2.y+=.1;
    d=max(d,-length(p2.xy)+.7*step(0.,p2.z));
    oc*=step(0.,d+.2);
    d=max(d,-cab);
    return d*.5;
}

float motor(vec3 p) {
    p*=1.05;
    p.z-=.4;
    p.y-=.25+exp(-5.*abs(p.x))*.1*smoothstep(2.,0.,p.z)+p.z*.03;
    float h=length(pow(abs(p.xy),vec2(1.5))*vec2(1,3.))-.62+max(0.,p.z+1.);
    h=min(h,length(p.xy)-.27);
    float sc=1.+min(2.,pow(max(0.,p.z+2.3)*.23,3.));
    p.x*=sc;
    p.y*=1.+min(.5,pow(abs(p.z)*.37,6.));
    p.y*=1.+min(1.,pow(max(0.,p.z+1.),2.)*.05);
    p.x*=1.+min(1.,pow(abs(p.y),2.5));
    float d=box(p,vec3(.65,.45,2.3),.2+max(0.,p.z)*.05);
    oc*=max(0.,sign(d+.1));
    d=ssub(h,d,.1*step(0.,-p.z));
    pmot=p;
    return d*.5/sc;
}

float alas(vec3 p) {
    p.y-=1.2-abs(p.x)*.07;
    p.z-=.3;
    p.x+=smoothstep(0.,1.8,-p.z)*sign(p.x);
    float w=abs(p.x)*.1;
    p.y*=.7+min(1.5,pow(abs(p.z+.3),3.)+abs(p.x)*.3);
    float d=box(p,vec3(4.5,-.1,.9-w),.2);
    p.z+=w;
    pwin=p;
    return d*.4;
}

float cola(vec3 p) {
    p.y-=1.-abs(p.x)*.15;
    p.z-=4.;
    p.z-=abs(p.x)*.3*smoothstep(-.2,0.2,-p.z);
    float w=abs(p.x)*.1;
    p.y*=.7+min(1.,pow(abs(p.z+.3),3.)+abs(p.x)*.5);
    float d=box(p,vec3(1.7,-.13,.5),.2);
    ptai=p;
    return d*.5;
}

float timon(vec3 p) {
    p.y-=1.;
    p.z-=3.8;
    p.z-=p.y*(step(p.z-.5,0.)+1.)*.4;;
    float d=box(p,vec3(-.05-p.z*.02,1.5,.8),.1);
    d=max(d,-p.y);
    ptim=p;
    return d*.5;

}

float arma1(vec3 p) {
    p.y-=.3;
    float b=box(p-vec3(0,0,p.y*.5),vec3(.0,.2,.35),0.05);
    p.y+=.3;
    p.z+=.9;
    p.z-=length(p.xy)*.5;
    float c=capsule(p,1.3,.23);
    float d=min(b,c);
    objcol+=max(0.,sign(.1-d))*vec3(1,0,0);
    return d*.5;
}


float armas(vec3 p) {
    vec3 p2=p;
    p.x=abs(p.x)-1.5;
    p.y-=.7;
    p2.x=abs(p2.x)-2.;
    p2.z-=.2;
    p2.y-=.45;
    float ar2=arma1(p2);
    parm=p2;
    return ar2;
}

/// Simple noise algorithm by Trisomie21
float snoise( vec2 p ) {
    p.y-=hashseed*100.;
    p.xy=p.yx;
	vec2 f = fract(p);
	p = floor(p);
	float v = p.x+p.y*1000.0;
	vec4 r = vec4(v, v+1., v+1000.0, v+1001.);
	r = fract(12345.*sin(r*.001));
	f = f*f*(3.0-2.0*f);
	return 2.0*(mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y))-1.0;
}


float terrain( vec2 p) {
	float h = 0.0;
	float w = .5;
	float m = .5;
	for (int i=0; i<5; i++) {
		h += w * snoise((p * m));
		w *= .25;
		m *= 4.;
	}
    return h;
}

float terrain(vec3 p) {
    if (p.y>2.) return p.y+2.;
    p.z+=time*3.;
    float ini=smoothstep(100.,70.,p.z);
    p.x+=ini*4.5;
    p.y+=.4+smoothstep(120.*3.,130.*3.,p.z)*.65;
    p.x-=path(p.z);
	h = terrain(p.xz*vec2(.5,1.)) * 1.2;
	h += smoothstep(-0.3, 1.5, h);
    h*=1.-ini*.7;
	float ap=.3+max(0.,sin(p.z*.05+cos(p.z*.123)*.0))*.5;
    h=mix(h,-1.,exp(-ap*(abs(p.x-.5*snoise(p.zz*.5)))));
	float d = p.y - h;
    tpos=p;
	return d*.5;
}

float pampa(vec3 p) {
    p.z+=2.8;
    p.y-=.8;
    p.yz*=rotyz;
    p.xz*=rotxz;
    p.z-=2.8;
    p.y+=.8;
    p.xy*=rotxy;
    float bound=box(p,vec3(5.,3.,6.),0.);
#ifdef noplane
    bound=box(p,vec3(1.,1.,2.3),0.);id=1.;return bound;
#endif
    if (bound>.65) return bound+.65;
    objcol=vec3(1.);
    p.z*=-1.05;
    float cab=cabina(p);
    float fus=fuselaje(p,cab);
    float mot=motor(p);
    float win=alas(p);
    float tai=cola(p);
    float tim=timon(p);
    cab=abs(cab)-.02;
    float arm=armas(p);
    float d=smin(fus,cab,max(.001,.02*p.z));
    d=min(d,mot);
    d=min(d,win);
    d=min(d,tai);
    d=min(d,tim);
    d=min(d,arm);
    if (d==fus) id=0.;
    if (d==mot) id=1.;
    if (d==cab) id=2.;
    if (d==win) id=3.;
    if (d==tai) id=4.;
    if (d==tim) id=5.;
    if (d==arm) id=6.;
    float t=step(abs(pcab.z+.3-pcab.y*.5),.07);
    t=max(t,step(abs(pcab.z+1.4+pcab.y*.3),.05));
    id-=step(1.,t);
    id=max(0.,id);
    pos=p;
    return d*1.2;
}


float water(vec3 p) {
    p.z+=time*3.*1.5;
    p.x-=path(p.z);
    p.x*=2.;
    return (p.y+1.75+snoise(p.xz*20.+time*0.)*.0002)*.9;
}

vec2 contrail(vec3 p) {
    vec3 pp=p-ppos;
    float t2=(tpos.z/3.)*speed-2.+.04*speed;
    float chx=(interhash(0.,t2)-.5)*4.5;
    float chy=.8+(interhash(-10.,t2*.75)-.5)*1.5+.018-baja;
    float w=.003+min(.05,-pp.z*.005)*2.;
    w=min(.03,w);
    float chorro=length(p.xy-vec2(chx,chy))-snoise(tpos.zz*15.)*w*.4;
    chorro=max(.003,abs(chorro));
    chorro=max(chorro,pp.z+.1);
    chorro=max(chorro,-pp.z-14.);
    return vec2(chorro,w);
}


float de(vec3 p) {
    vec3 pos=p-ppos;
    float pam=pampa(pos*20.)/20.;
    float ter=terrain(p/1.5)*1.5;
    float wat=water(p);
    float d=min(wat,ter);
    d=min(d,pam);
    if (d==ter) id=-1.;
    if (d==wat) id=-2.;
    return d;
}


float ao(vec3 p, vec3 n) {
    float st=.05;
    float ao=0.;
    for(float i=0.; i<8.; i++ ) {
        float td=.02+st*i;
        float d=de(p+n*td);
        ao+=max(0.,(td-d)/td);
    }
    return clamp(1.-ao*.15,0.,1.);
}

float shadows(vec3 p, vec3 ldir)
{
    float td=.0,sh=1.,d=.01,dt;
    for (int i=0; i<50; i++) {
		p+=ldir*d;
        float map=de(p);
        vec2 ctrail=contrail(p);
        dt=ctrail.x-ctrail.y*1.5+.05;
        d=min(map,dt);
        td+=d;
        if (dt<.05&&map>.01) {
            sh=.6+ctrail.y*8.; break;
        }
		if (d<.002) {
            sh=.5;
            break;
        }
        if (td>maxdist) break;
    }
    return clamp(sh,0.5,1.);
}


vec3 normal3(vec3 p) {
    vec2 e=vec2(0.,det*.5);
    return normalize(vec3(de(p+e.yxx),de(p+e.xyx),de(p+e.xxy))-de(p));
}

/// normal hack by Shane to reduce compilation time
/*vec3 normal(vec3 pos) {
    vec2 e=vec2(0.,det*.5);
    vec3[4] ev = vec3[4](e.yxx, e.xyx, e.xxy, e.xxx-.000001);
    vec3 nn = vec3(0);
    for(int i = 0; i<4; i++){
        nn += sign(ev[i])*de(pos + ev[i]);
        if(nn.x<-1e8) break; // Fake break.
    }
    return normalize(nn);
}
*/
vec3 normal2(vec3 p) {
    vec2 e=vec2(0.,det*.5);
    return normalize(vec3(water(p+e.yxx),water(p+e.xyx),water(p+e.xxy))-water(p));
}


float noise(vec3 x) {
    const vec3 step = vec3(110, 241, 171);

    vec3 i = floor(x);
    vec3 f = fract(x);

    float n = dot(i, step);

    vec3 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix( hash(n + dot(step, vec3(0, 0, 0))), hash(n + dot(step, vec3(1, 0, 0))), u.x),
                   mix( hash(n + dot(step, vec3(0, 1, 0))), hash(n + dot(step, vec3(1, 1, 0))), u.x), u.y),
               mix(mix( hash(n + dot(step, vec3(0, 0, 1))), hash(n + dot(step, vec3(1, 0, 1))), u.x),
                   mix( hash(n + dot(step, vec3(0, 1, 1))), hash(n + dot(step, vec3(1, 1, 1))), u.x), u.y), u.z);
}


const mat3 m3 = mat3( 0.00,  0.80,  0.60,
					-0.80,  0.36, -0.48,
					-0.60, -0.48,  0.64 );
float fbm(in vec3 q)
{
	float f  = 0.5000*noise( q ); q = m3*q*2.01;
	f += 0.2500*noise( q ); q = m3*q*2.02;
	f += 0.1250*noise( q ); q = m3*q*2.03;
	f += 0.0625*noise( q ); q = m3*q*2.04;
	return f;
}

vec3 pampaColors() {
    vec3 base=vec3(117.,170.,219.)/255.;
    vec3 sec=vec3(1.,.7,.3);
    vec3 ter=mix(sec,base,(1.+cos(pos.z*3.))*.5);
    ter=base;
    vec3 col=mix(base,vec3(.9),smoothstep(.55,.6,max(noise(floor(pos*2.)),noise(floor(pos*4.)))));
    col=mix(col,base,abs(pmot.x)*.25);
    if (id==0.) {
        col=mix(col,ter,step(abs(pfus.x+(noise(pos*5.+4.)-.5)*.0),.2));
    }
    if (id==2.) {
        vec3 cab=vec3(.1);
        float t=step(.07,abs(pcab.z+.3-pcab.y*.5));
        t=min(t,step(.05,abs(pcab.z+1.4+pcab.y*.3)));
        col=mix(ter,cab,t);
    }
    if (id==1.) {
        col=mix(col,sec,step(2.1,-pmot.z)*step(0.,dcab-.1));
        col=mix(col,sec,step(2.2,pmot.z));
    }
    if (id==3.) {
        pwin.x=abs(pwin.x);
        col=mix(col,sec,step(4.3,abs(pwin.x)));
        col-=step(abs(pwin.z-.7),.03)*.2*step(pwin.x,4.3);
        if (pwin.z>0.7) {
            col-=step(abs(pwin.x-3.5),.03)*.2;
            col-=step(abs(pwin.x-1.5),.03)*.2;
        }
    }
    if (id==4.) {
        ptai.x=abs(ptai.x);
        col=mix(col,sec,step(1.6,ptai.x));
        col-=step(abs(ptai.z-.35),.03)*.2*step(ptai.x,1.2);
        col-=step(abs(ptai.x-1.2),.03)*.2;
    }
    if (id==5.) {
        col=mix(col,sec,step(1.3,ptim.y));
        col-=step(abs(ptim.z-.5),.03)*.2*step(ptim.y,1.3);
    }
    if (id==6.) {
        col=mix(col,ter,step(length(parm.xy),.25));
    }

    col*=.5+oc*.5;
    col*=1.05-noise(pos*20.)*.1;
    return col*vec3(1.,.95,.9);
}

vec3 terrainColors(vec3 p, vec3 n, float y) {
    vec3 col = mix( vec3(0.2, 0.18, 0.18)*.7, vec3(0.22, 0.19, 0.16)*.8, smoothstep(0.7, 1.0, n.y) *.5) * 3.;
    float r=snoise(p.xy*vec2(7., 50.0)*2.)*.8;
    col = mix( r*vec3(.5, 0.4, 0.4), col, n.y);
    float clear=smoothstep(.0,.07,h+.75);
    vec3 veg=vec3(.9, .77, .6)*.4;
    veg=mix(veg,vec3(1.1,.95,0.7)*.23,smoothstep(0.,.5,snoise(p.xz*.5)));
    veg*=(1.+snoise(p.xz*90.)*.3);
    col = mix( col, veg, clear*smoothstep(.8, .9, n.y) *smoothstep(1.,0.,snoise(p.xz+123.)));
    col*=1.-smoothstep(-.3,-1.2,p.y)*.7;
    return col*1.6;
}

vec3 shade(vec3 p, vec3 dir) {
    float y=tpos.y-h;
    float id2=id;
    vec3 n = normal3(p);
    vec3 col;
    float oc=1.;
    if (id<0.) col=terrainColors(tpos,n,y),oc=ao(p,n); else col=pampaColors();
    float amb=max(.5,dot(-dir,n))*.5*oc;
    amb=.4*oc;
    float sh=1.;
    if (ref<.5) sh=shadows(p,ldir);
    float dif=max(0.,dot(ldir,n))*.6;
    vec3 refl=reflect(ldir,n);
    float spe=pow(max(0.,dot(dir,refl)),50.);
    if (id2!=2.) spe*=.3;
    if (id2<0.) spe*=.4;

    return col*(amb+dif*sh)+spe*sh*vec3(1.1,1.,.9);
}

vec3 shadeRiver(vec3 p, vec3 dir) {
    float sh=1.;
    vec3 n = normal2(p);
    float tr=smoothstep(120.*3.,130.*3.,tpos.z);
    vec3 col=mix(vec3(0.14,0.12,0.1)*2.,vec3(0.1,0.1,0.105)*3.3,tr);
    col+=smoothstep(.05,.0,tpos.y-h)*.2;
    vec3 refl=reflect(ldir,n);
    float spe=pow(max(0.,dot(dir,refl)),30.)*.55;
    return col*(.7+sh*.3)+spe;
}

vec3 shadeVid(vec3 p, vec3 dir) {
    vec3 cab=vec3(.1);
    float t=step(.07,abs(pcab.z+.3-pcab.y*.5));
    t=min(t,step(.05,abs(pcab.z+1.4+pcab.y*.3)));
    vec3 col=cab;
    vec3 n = normal3(p);
    float amb=.4;
    float dif=max(0.,dot(ldir,n))*.5;
    vec3 refl=reflect(ldir,n);
    float spe=pow(max(0.,dot(dir,refl)),30.)*1.5;
    return col*(amb+dif)+spe*vec3(1.1,1.,.9);
}



vec3 march(vec3 from, vec3 dir, vec2 uv)
{
    ldir = normalize(vec3(1,1.,1.5));
    float d, td=0., td2=0., vid=0., g=0.,aro=g, chorro=1.;
    vec3 p=from, col=vec3(.0), colvid=col, shaderef=col, pp=p, pref=p, odir=dir;
    float h=hash(uv*1000.)*.1;
    for (int i=0; i<250; i++) {
        p+=d*dir;
        d=de(p)*(1.0-h);
        det=id<0.?.003:.0002;
        det*=1.0+pow(td,1.2)*.75;
        if (id==2.&&d<det) {
            if (vid<.5) colvid=shadeVid(p,dir);
            vid=1.;
            d=max(.01,abs(d));
        };
        if (id==-2.&&d<det&&ref<.5) {
            ref=1.;
            pref=p;
            vec3 n=normal2(p);
            dir=reflect(dir,n);
            p+=dir*.05;
            td2=td;
            d=det;
            //continue;
        };
        if (d<det || td>maxdist) break;
        pp=p-ppos;
        aro=max(abs(pp.y)-.005,abs(length(pp.xz)-.3));
        if (time>1.5 && time < 8.) d=min(d,aro);
#ifdef trail
        if (pp.z<-.1&&p.z>-14.){
            vec2 ctrail=contrail(p);
            float chorro=ctrail.x;
            float w=ctrail.y;
            d=min(d,chorro*(.5+w*17.));
            g=max(g,max(0.,w-chorro)/w*smoothstep(-14.,0.,pp.z)*(1.-ref*.5)
                *smoothstep(-.07,-.35,pp.z));
        }
#endif
        td+=d;
    }
    float id2=id;
    float clou=0.;
    vec3 sky=mix(vec3(.75,.77,.85)*1.0,vec3(0.6,.7,.85)*.9,clamp(p.y*.07+.2,-.4,1.))*.8;
    sky+=pow(max(0.,dot(dir,ldir)),70.)*vec3(1.4,1.,.6)*.5;
    vec3 colref=vec3(0.);
    if (d<det&&d!=aro) {
        p-=det*dir*2.;
        col=shade(p, dir);
    } else {
        td=maxdist;
        p=dir*maxdist;
        vec3 ps=dir*2.5;
        ps.y*=4.;
        ps.z+=time*.05;
        clou=fbm(ps);
        clou=smoothstep(.4,1.2,clou);
        clou*=smoothstep(0.,5.,p.y);
    }
    if (ref>.5) {
        shaderef=shadeRiver(pref,odir);
        colref=mix(shaderef,sky,td2/maxdist);
    }
    sky+=clou*step(td-.5,maxdist)*.3*vec3(1.,.85,.6)*(1.+ref*.7);
    col=mix(col,sky,pow(td/maxdist,1.5));
    col=mix(col,colvid,vid*.5);
    if (ref>.5) col=colref*.5+col*.5;
    col=mix(col,col.ggg,smoothstep(1.,0.,t));
    vec3 back = vec3(length(smoothstep(.8,1.,fract(uv*20.)))*.2);
    float b=max(-1.,1.5-time);
    float c=max(-1.,3.7-time);
    float li=smoothstep(.01,.0,abs(uv.x+.5-b))*step(abs(uv.y+.025),.425*min(1.,time*2.));
    back+=li*step(.5,fract(sqrt(time*13332.654)));
    back+=hash(uv*1234.+time)*.2;
    if (time>2.5) {
        li=smoothstep(.01,.0,abs(uv.y+.5-c))*step(abs(uv.x+.025),.425*min(1.,time*2.));
        back+=li*step(.5,fract(sqrt(time*13332.654)));
    }
    if (id2<0.||uv.x<b-.5||(time>2.5&&uv.y<c-.5)) col=mix(col,back,smoothstep(1.,0.,t));
    if (fract(time*10.)*step(abs(time-8.5),.5)>.5) col=back;
    col=mix(col,back,step(time,9.5)*step(aro,d)*step(.5,fract(time*2.+1.*atan(pp.x,pp.z))));
    g*=smoothstep(0.5,1.,t);
    col=mix(col,vec3(.85),g*.5);
    return col;
}

mat3 lookat(vec3 dir,vec3 up) {
	dir=normalize(dir);vec3 rt=normalize(cross(dir,normalize(up)));
    return mat3(rt,cross(rt,dir),dir);
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    hashseed=floor(time*.83/200.);
    vec2 uv = gl_FragCoord.xy/resolution.xy-.5;
    uv.x*=resolution.x/resolution.y;
    float fov=1.1;
    fragColor=vec4(0.);
#ifdef letterboxfullscreen
    if (resolution.x>1500.&&abs(uv.y)>.37) return;
    if (resolution.x>1500.) fov=.8;
#endif
    speed=.25;
    t=time*speed-2.;
    float start=smoothstep(0.,.5,t);
    float t1=t*start;
    float t2=time*speed+.13-2.;
    t2*=start;
    float z=-.65-interhash(11.,t1)*1.8;
    float xz=1.-interhash(22.,t1*.5)*5.;
    float yz=.3-interhash(33.,t1*.5)*1.1;
    ppos.x=(interhash(0.,t1)-.5)*4.5;
    ppos2.x=(interhash(0.,t2)-.5)*4.5;
    baja=smoothstep(125.,130.,time)*1.65;
    ppos.y=.8+(interhash(-10.,t1*.75)-.5)*1.5-baja;
    ppos2.y=.8+(interhash(-10.,t2*.75)-.5)*1.5-baja;
    rotyz=rot((ppos.y-ppos2.y)*.5);
    rotxy=rot((ppos.x-ppos2.x)*.8);
    rotxz=rot((ppos.x-ppos2.x)*.3);
    vec3 from=vec3(0.,0.3,z);
    vec2 m=mouse.xy/resolution.xy;
    bool mouseon=false;
#ifndef onlymouse
    if (mouse.x<1.) {
        from.yz*=rot(yz);
        from.xz*=rot(xz);
    } else {
        from.yz*=rot(.5-(1.-m.y)*1.5);
        from.xz*=rot(-m.x*6.);
        mouseon=true;
    }
#endif
#ifdef onlymouse
    from.yz*=rot(.5-(1.-m.y)*1.5);
    from.xz*=rot(-m.x*6.);
    mouseon=true;
#endif
    from+=ppos;
    if (!mouseon && mod(time,30.)>24.) from=vec3(.7,0.,mod(-time*4.,30.)-15.),fov*=1.3;
    vec3 g=mix(vec3(0.5,1.,0.),vec3(1.),smoothstep(0.,1.,t));
    if (t<0.) {
        ppos=vec3(0.);
        ppos2=ppos;
        from=vec3(0.,.6,-0.1);
        if (time>2.8) from=vec3(.5,.3,0.), from.xz*=rot(-time*.3);
    }
    vec3 dir=normalize(vec3(uv,fov));
    float s=sin(time*.3);
    dir.xz*=rot(s*s*s*.25*start);
    dir=lookat(ppos-from,vec3(0.,1.,0.))*dir;
    vec3 col=march(from,dir,uv*max(1.,2.-time*2.))*g;
    col=pow(col,vec3(1.2))*1.15;
    col*=smoothstep(200.,198.,mod(time,200.));
    col*=smoothstep(0.,.5,mod(time,200.));
    fragColor = vec4(col,1);
}
