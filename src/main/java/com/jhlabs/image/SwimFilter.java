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
/*     */ 
/*     */ public class SwimFilter
/*     */   extends TransformFilter
/*     */ {
/*  28 */   private float scale = 32.0F;
/*  29 */   private float stretch = 1.0F;
/*  30 */   private float angle = 0.0F;
/*  31 */   private float amount = 1.0F;
/*  32 */   private float turbulence = 1.0F;
/*  33 */   private float time = 0.0F;
/*  34 */   private float m00 = 1.0F;
/*  35 */   private float m01 = 0.0F;
/*  36 */   private float m10 = 0.0F;
/*  37 */   private float m11 = 1.0F;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setScale(float scale) {
/*  74 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/*  84 */     return this.scale;
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
/*  96 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 106 */     return this.stretch;
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
/* 117 */     this.angle = angle;
/* 118 */     float cos = (float)Math.cos(angle);
/* 119 */     float sin = (float)Math.sin(angle);
/* 120 */     this.m00 = cos;
/* 121 */     this.m01 = sin;
/* 122 */     this.m10 = -sin;
/* 123 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 133 */     return this.angle;
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
/* 145 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/* 155 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTime(float time) {
/* 166 */     this.time = time;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTime() {
/* 176 */     return this.time;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 181 */     float nx = this.m00 * x + this.m01 * y;
/* 182 */     float ny = this.m10 * x + this.m11 * y;
/* 183 */     nx /= this.scale;
/* 184 */     ny /= this.scale * this.stretch;
/*     */     
/* 186 */     if (this.turbulence == 1.0F) {
/*     */       
/* 188 */       out[0] = x + this.amount * Noise.noise3(nx + 0.5F, ny, this.time);
/* 189 */       out[1] = y + this.amount * Noise.noise3(nx, ny + 0.5F, this.time);
/*     */     }
/*     */     else {
/*     */       
/* 193 */       out[0] = x + this.amount * Noise.turbulence3(nx + 0.5F, ny, this.turbulence, this.time);
/* 194 */       out[1] = y + this.amount * Noise.turbulence3(nx, ny + 0.5F, this.turbulence, this.time);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 200 */     return "Distort/Swim...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SwimFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */