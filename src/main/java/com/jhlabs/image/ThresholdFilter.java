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
/*     */ 
/*     */ 
/*     */ public class ThresholdFilter
/*     */   extends PointFilter
/*     */ {
/*     */   private int lowerThreshold;
/*     */   private int upperThreshold;
/*  28 */   private int white = 16777215;
/*  29 */   private int black = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThresholdFilter() {
/*  36 */     this(127);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThresholdFilter(int t) {
/*  45 */     setLowerThreshold(t);
/*  46 */     setUpperThreshold(t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLowerThreshold(int lowerThreshold) {
/*  56 */     this.lowerThreshold = lowerThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLowerThreshold() {
/*  66 */     return this.lowerThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUpperThreshold(int upperThreshold) {
/*  76 */     this.upperThreshold = upperThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUpperThreshold() {
/*  86 */     return this.upperThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWhite(int white) {
/*  96 */     this.white = white;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWhite() {
/* 106 */     return this.white;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlack(int black) {
/* 116 */     this.black = black;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBlack() {
/* 126 */     return this.black;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 131 */     int v = PixelUtils.brightness(rgb);
/* 132 */     float f = ImageMath.smoothStep(this.lowerThreshold, this.upperThreshold, v);
/* 133 */     return rgb & 0xFF000000 | ImageMath.mixColors(f, this.black, this.white) & 0xFFFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 138 */     return "Stylize/Threshold...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ThresholdFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */