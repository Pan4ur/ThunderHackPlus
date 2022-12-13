/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class StampFilter
/*     */   extends PointFilter
/*     */ {
/*     */   private float threshold;
/*  27 */   private float softness = 0.0F;
/*  28 */   private float radius = 5.0F;
/*     */   private float lowerThreshold3;
/*     */   private float upperThreshold3;
/*  31 */   private int white = -1;
/*  32 */   private int black = -16777216;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StampFilter() {
/*  39 */     this(0.5F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StampFilter(float threshold) {
/*  48 */     setThreshold(threshold);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/*  59 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/*  69 */     return this.radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreshold(float threshold) {
/*  79 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getThreshold() {
/*  89 */     return this.threshold;
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
/*     */   public void setSoftness(float softness) {
/* 101 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/* 111 */     return this.softness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWhite(int white) {
/* 121 */     this.white = white;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWhite() {
/* 131 */     return this.white;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlack(int black) {
/* 141 */     this.black = black;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBlack() {
/* 151 */     return this.black;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 156 */     dst = (new GaussianFilter((int)this.radius)).filter(src, null);
/* 157 */     this.lowerThreshold3 = 765.0F * (this.threshold - this.softness * 0.5F);
/* 158 */     this.upperThreshold3 = 765.0F * (this.threshold + this.softness * 0.5F);
/* 159 */     return super.filter(dst, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 164 */     int a = rgb & 0xFF000000;
/* 165 */     int r = rgb >> 16 & 0xFF;
/* 166 */     int g = rgb >> 8 & 0xFF;
/* 167 */     int b = rgb & 0xFF;
/* 168 */     int l = r + g + b;
/* 169 */     float f = ImageMath.smoothStep(this.lowerThreshold3, this.upperThreshold3, l);
/* 170 */     return ImageMath.mixColors(f, this.black, this.white);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 175 */     return "Stylize/Stamp...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\StampFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */