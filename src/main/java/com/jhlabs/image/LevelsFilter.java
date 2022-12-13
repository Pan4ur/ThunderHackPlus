/*     */ package com.jhlabs.image;
/*     */ 
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
/*     */ public class LevelsFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private int[][] lut;
/*  28 */   private float lowLevel = 0.0F;
/*  29 */   private float highLevel = 1.0F;
/*  30 */   private float lowOutputLevel = 0.0F;
/*  31 */   private float highOutputLevel = 1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLowLevel(float lowLevel) {
/*  39 */     this.lowLevel = lowLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLowLevel() {
/*  44 */     return this.lowLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHighLevel(float highLevel) {
/*  49 */     this.highLevel = highLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHighLevel() {
/*  54 */     return this.highLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLowOutputLevel(float lowOutputLevel) {
/*  59 */     this.lowOutputLevel = lowOutputLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLowOutputLevel() {
/*  64 */     return this.lowOutputLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHighOutputLevel(float highOutputLevel) {
/*  69 */     this.highOutputLevel = highOutputLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHighOutputLevel() {
/*  74 */     return this.highOutputLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  79 */     Histogram histogram = new Histogram(inPixels, width, height, 0, width);
/*     */ 
/*     */     
/*  82 */     if (histogram.getNumSamples() > 0) {
/*     */       
/*  84 */       float scale = 255.0F / histogram.getNumSamples();
/*  85 */       this.lut = new int[3][256];
/*  86 */       float low = this.lowLevel * 255.0F;
/*  87 */       float high = this.highLevel * 255.0F;
/*     */       
/*  89 */       if (low == high)
/*     */       {
/*  91 */         high++;
/*     */       }
/*     */       
/*  94 */       for (int j = 0; j < 3; j++)
/*     */       {
/*  96 */         for (int k = 0; k < 256; k++)
/*     */         {
/*  98 */           this.lut[j][k] = PixelUtils.clamp((int)(255.0F * (this.lowOutputLevel + (this.highOutputLevel - this.lowOutputLevel) * (k - low) / (high - low))));
/*     */         }
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 104 */       this.lut = (int[][])null;
/*     */     } 
/*     */     
/* 107 */     int i = 0;
/*     */     
/* 109 */     for (int y = 0; y < height; y++) {
/* 110 */       for (int x = 0; x < width; x++) {
/*     */         
/* 112 */         inPixels[i] = filterRGB(x, y, inPixels[i]);
/* 113 */         i++;
/*     */       } 
/*     */     } 
/* 116 */     this.lut = (int[][])null;
/* 117 */     return inPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 122 */     if (this.lut != null) {
/*     */       
/* 124 */       int a = rgb & 0xFF000000;
/* 125 */       int r = this.lut[0][rgb >> 16 & 0xFF];
/* 126 */       int g = this.lut[1][rgb >> 8 & 0xFF];
/* 127 */       int b = this.lut[2][rgb & 0xFF];
/* 128 */       return a | r << 16 | g << 8 | b;
/*     */     } 
/*     */     
/* 131 */     return rgb;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 136 */     return "Colors/Levels...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LevelsFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */