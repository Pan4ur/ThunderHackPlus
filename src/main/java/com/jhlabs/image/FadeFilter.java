/*     */ package com.jhlabs.image;
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
/*     */ public class FadeFilter
/*     */   extends PointFilter
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*  26 */   private float angle = 0.0F;
/*  27 */   private float fadeStart = 1.0F;
/*  28 */   private float fadeWidth = 10.0F;
/*     */   private int sides;
/*     */   private boolean invert;
/*  31 */   private float m00 = 1.0F;
/*  32 */   private float m01 = 0.0F;
/*  33 */   private float m10 = 0.0F;
/*  34 */   private float m11 = 1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  44 */     this.angle = angle;
/*  45 */     float cos = (float)Math.cos(angle);
/*  46 */     float sin = (float)Math.sin(angle);
/*  47 */     this.m00 = cos;
/*  48 */     this.m01 = sin;
/*  49 */     this.m10 = -sin;
/*  50 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  60 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSides(int sides) {
/*  65 */     this.sides = sides;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSides() {
/*  70 */     return this.sides;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFadeStart(float fadeStart) {
/*  75 */     this.fadeStart = fadeStart;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFadeStart() {
/*  80 */     return this.fadeStart;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFadeWidth(float fadeWidth) {
/*  85 */     this.fadeWidth = fadeWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFadeWidth() {
/*  90 */     return this.fadeWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInvert(boolean invert) {
/*  95 */     this.invert = invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getInvert() {
/* 100 */     return this.invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDimensions(int width, int height) {
/* 105 */     this.width = width;
/* 106 */     this.height = height;
/* 107 */     super.setDimensions(width, height);
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 112 */     float nx = this.m00 * x + this.m01 * y;
/* 113 */     float ny = this.m10 * x + this.m11 * y;
/*     */     
/* 115 */     if (this.sides == 2) {
/*     */       
/* 117 */       nx = (float)Math.sqrt((nx * nx + ny * ny));
/*     */     }
/* 119 */     else if (this.sides == 3) {
/*     */       
/* 121 */       nx = ImageMath.mod(nx, 16.0F);
/*     */     }
/* 123 */     else if (this.sides == 4) {
/*     */       
/* 125 */       nx = symmetry(nx, 16.0F);
/*     */     } 
/*     */     
/* 128 */     int alpha = (int)(ImageMath.smoothStep(this.fadeStart, this.fadeStart + this.fadeWidth, nx) * 255.0F);
/*     */     
/* 130 */     if (this.invert)
/*     */     {
/* 132 */       alpha = 255 - alpha;
/*     */     }
/*     */     
/* 135 */     return alpha << 24 | rgb & 0xFFFFFF;
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
/*     */   public float symmetry(float x, float b) {
/* 147 */     x = ImageMath.mod(x, 2.0F * b);
/*     */     
/* 149 */     if (x > b)
/*     */     {
/* 151 */       return 2.0F * b - x;
/*     */     }
/*     */     
/* 154 */     return x;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 170 */     return "Fade...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FadeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */