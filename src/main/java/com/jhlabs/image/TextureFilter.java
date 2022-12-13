/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Function2D;
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
/*     */ public class TextureFilter
/*     */   extends PointFilter
/*     */ {
/*  24 */   private float scale = 32.0F;
/*  25 */   private float stretch = 1.0F;
/*  26 */   private float angle = 0.0F;
/*  27 */   public float amount = 1.0F;
/*  28 */   public float turbulence = 1.0F;
/*  29 */   public float gain = 0.5F;
/*  30 */   public float bias = 0.5F;
/*     */   public int operation;
/*  32 */   private float m00 = 1.0F;
/*  33 */   private float m01 = 0.0F;
/*  34 */   private float m10 = 0.0F;
/*  35 */   private float m11 = 1.0F;
/*  36 */   private Colormap colormap = new Gradient();
/*  37 */   private Function2D function = (Function2D)new Noise();
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
/*     */   public void setAmount(float amount) {
/*  52 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  62 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFunction(Function2D function) {
/*  67 */     this.function = function;
/*     */   }
/*     */ 
/*     */   
/*     */   public Function2D getFunction() {
/*  72 */     return this.function;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOperation(int operation) {
/*  77 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOperation() {
/*  82 */     return this.operation;
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
/*  94 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 104 */     return this.scale;
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
/* 116 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 126 */     return this.stretch;
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
/* 137 */     this.angle = angle;
/* 138 */     float cos = (float)Math.cos(angle);
/* 139 */     float sin = (float)Math.sin(angle);
/* 140 */     this.m00 = cos;
/* 141 */     this.m01 = sin;
/* 142 */     this.m10 = -sin;
/* 143 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 153 */     return this.angle;
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
/* 165 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/* 175 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 185 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 195 */     return this.colormap;
/*     */   }
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*     */     int v;
/* 200 */     float nx = this.m00 * x + this.m01 * y;
/* 201 */     float ny = this.m10 * x + this.m11 * y;
/* 202 */     nx /= this.scale;
/* 203 */     ny /= this.scale * this.stretch;
/* 204 */     float f = (this.turbulence == 1.0D) ? Noise.noise2(nx, ny) : Noise.turbulence2(nx, ny, this.turbulence);
/* 205 */     f = f * 0.5F + 0.5F;
/* 206 */     f = ImageMath.gain(f, this.gain);
/* 207 */     f = ImageMath.bias(f, this.bias);
/* 208 */     f *= this.amount;
/* 209 */     int a = rgb & 0xFF000000;
/*     */ 
/*     */     
/* 212 */     if (this.colormap != null) {
/*     */       
/* 214 */       v = this.colormap.getColor(f);
/*     */     }
/*     */     else {
/*     */       
/* 218 */       v = PixelUtils.clamp((int)(f * 255.0F));
/* 219 */       int r = v << 16;
/* 220 */       int g = v << 8;
/* 221 */       int b = v;
/* 222 */       v = a | r | g | b;
/*     */     } 
/*     */     
/* 225 */     if (this.operation != 0)
/*     */     {
/* 227 */       v = PixelUtils.combinePixels(rgb, v, this.operation);
/*     */     }
/*     */     
/* 230 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 235 */     return "Texture/Noise...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TextureFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */