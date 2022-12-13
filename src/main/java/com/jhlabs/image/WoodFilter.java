/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Noise;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WoodFilter
/*     */   extends PointFilter
/*     */ {
/*  27 */   private float scale = 200.0F;
/*  28 */   private float stretch = 10.0F;
/*  29 */   private float angle = 1.5707964F;
/*  30 */   private float rings = 0.5F;
/*  31 */   private float turbulence = 0.0F;
/*  32 */   private float fibres = 0.5F;
/*  33 */   private float gain = 0.8F;
/*  34 */   private float m00 = 1.0F;
/*  35 */   private float m01 = 0.0F;
/*  36 */   private float m10 = 0.0F;
/*  37 */   private float m11 = 1.0F;
/*  38 */   private Colormap colormap = new LinearColormap(-1719148, -6784175);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRings(float rings) {
/*  56 */     this.rings = rings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRings() {
/*  66 */     return this.rings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setScale(float scale) {
/*  78 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/*  88 */     return this.scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStretch(float stretch) {
/* 100 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 110 */     return this.stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/* 121 */     this.angle = angle;
/* 122 */     float cos = (float)Math.cos(angle);
/* 123 */     float sin = (float)Math.sin(angle);
/* 124 */     this.m00 = cos;
/* 125 */     this.m01 = sin;
/* 126 */     this.m10 = -sin;
/* 127 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 137 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTurbulence(float turbulence) {
/* 149 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/* 159 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFibres(float fibres) {
/* 171 */     this.fibres = fibres;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getFibres() {
/* 181 */     return this.fibres;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGain(float gain) {
/* 193 */     this.gain = gain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getGain() {
/* 203 */     return this.gain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 213 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 223 */     return this.colormap;
/*     */   }
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*     */     int v;
/* 228 */     float nx = this.m00 * x + this.m01 * y;
/* 229 */     float ny = this.m10 * x + this.m11 * y;
/* 230 */     nx /= this.scale;
/* 231 */     ny /= this.scale * this.stretch;
/* 232 */     float f = Noise.noise2(nx, ny);
/* 233 */     f += 0.1F * this.turbulence * Noise.noise2(nx * 0.05F, ny * 20.0F);
/* 234 */     f = f * 0.5F + 0.5F;
/* 235 */     f *= this.rings * 50.0F;
/* 236 */     f -= (int)f;
/* 237 */     f *= 1.0F - ImageMath.smoothStep(this.gain, 1.0F, f);
/* 238 */     f += this.fibres * Noise.noise2(nx * this.scale, ny * 50.0F);
/* 239 */     int a = rgb & 0xFF000000;
/*     */ 
/*     */     
/* 242 */     if (this.colormap != null) {
/*     */       
/* 244 */       v = this.colormap.getColor(f);
/*     */     }
/*     */     else {
/*     */       
/* 248 */       v = PixelUtils.clamp((int)(f * 255.0F));
/* 249 */       int r = v << 16;
/* 250 */       int g = v << 8;
/* 251 */       int b = v;
/* 252 */       v = a | r | g | b;
/*     */     } 
/*     */     
/* 255 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 260 */     return "Texture/Wood...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WoodFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */