/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Noise;
/*     */ import java.awt.Rectangle;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RippleFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   public static final int SINE = 0;
/*     */   public static final int SAWTOOTH = 1;
/*     */   public static final int TRIANGLE = 2;
/*     */   public static final int NOISE = 3;
/*  59 */   private float xAmplitude = 5.0F;
/*  60 */   private float yAmplitude = 0.0F;
/*  61 */   private float xWavelength = this.yWavelength = 16.0F;
/*     */ 
/*     */   
/*     */   private float yWavelength;
/*     */ 
/*     */   
/*     */   private int waveType;
/*     */ 
/*     */   
/*     */   public void setXAmplitude(float xAmplitude) {
/*  71 */     this.xAmplitude = xAmplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getXAmplitude() {
/*  81 */     return this.xAmplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setXWavelength(float xWavelength) {
/*  91 */     this.xWavelength = xWavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getXWavelength() {
/* 101 */     return this.xWavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setYAmplitude(float yAmplitude) {
/* 111 */     this.yAmplitude = yAmplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getYAmplitude() {
/* 121 */     return this.yAmplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setYWavelength(float yWavelength) {
/* 131 */     this.yWavelength = yWavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getYWavelength() {
/* 141 */     return this.yWavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWaveType(int waveType) {
/* 151 */     this.waveType = waveType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWaveType() {
/* 161 */     return this.waveType;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle r) {
/* 166 */     if (this.edgeAction == 0) {
/*     */       
/* 168 */       r.x -= (int)this.xAmplitude;
/* 169 */       r.width += (int)(2.0F * this.xAmplitude);
/* 170 */       r.y -= (int)this.yAmplitude;
/* 171 */       r.height += (int)(2.0F * this.yAmplitude);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 177 */     float fx, fy, nx = y / this.xWavelength;
/* 178 */     float ny = x / this.yWavelength;
/*     */ 
/*     */     
/* 181 */     switch (this.waveType) {
/*     */ 
/*     */       
/*     */       default:
/* 185 */         fx = (float)Math.sin(nx);
/* 186 */         fy = (float)Math.sin(ny);
/*     */         break;
/*     */       
/*     */       case 1:
/* 190 */         fx = ImageMath.mod(nx, 1.0F);
/* 191 */         fy = ImageMath.mod(ny, 1.0F);
/*     */         break;
/*     */       
/*     */       case 2:
/* 195 */         fx = ImageMath.triangle(nx);
/* 196 */         fy = ImageMath.triangle(ny);
/*     */         break;
/*     */       
/*     */       case 3:
/* 200 */         fx = Noise.noise1(nx);
/* 201 */         fy = Noise.noise1(ny);
/*     */         break;
/*     */     } 
/*     */     
/* 205 */     out[0] = x + this.xAmplitude * fx;
/* 206 */     out[1] = y + this.yAmplitude * fy;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 211 */     return "Distort/Ripple...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RippleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */