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
/*     */ public class ErodeAlphaFilter
/*     */   extends PointFilter
/*     */ {
/*     */   private float threshold;
/*  24 */   private float softness = 0.0F;
/*  25 */   protected float radius = 5.0F;
/*     */   
/*     */   private float lowerThreshold;
/*     */   private float upperThreshold;
/*     */   
/*     */   public ErodeAlphaFilter() {
/*  31 */     this(3.0F, 0.75F, 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public ErodeAlphaFilter(float radius, float threshold, float softness) {
/*  36 */     this.radius = radius;
/*  37 */     this.threshold = threshold;
/*  38 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/*  43 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRadius() {
/*  48 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setThreshold(float threshold) {
/*  53 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getThreshold() {
/*  58 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSoftness(float softness) {
/*  63 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/*  68 */     return this.softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  73 */     dst = (new GaussianFilter((int)this.radius)).filter(src, null);
/*  74 */     this.lowerThreshold = 255.0F * (this.threshold - this.softness * 0.5F);
/*  75 */     this.upperThreshold = 255.0F * (this.threshold + this.softness * 0.5F);
/*  76 */     return super.filter(dst, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*  81 */     int a = rgb >> 24 & 0xFF;
/*  82 */     int r = rgb >> 16 & 0xFF;
/*  83 */     int g = rgb >> 8 & 0xFF;
/*  84 */     int b = rgb & 0xFF;
/*     */     
/*  86 */     if (a == 255)
/*     */     {
/*  88 */       return -1;
/*     */     }
/*     */     
/*  91 */     float f = ImageMath.smoothStep(this.lowerThreshold, this.upperThreshold, a);
/*  92 */     a = (int)(f * 255.0F);
/*     */     
/*  94 */     if (a < 0) {
/*     */       
/*  96 */       a = 0;
/*     */     }
/*  98 */     else if (a > 255) {
/*     */       
/* 100 */       a = 255;
/*     */     } 
/*     */     
/* 103 */     return a << 24 | 0xFFFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 108 */     return "Alpha/Erode...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ErodeAlphaFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */