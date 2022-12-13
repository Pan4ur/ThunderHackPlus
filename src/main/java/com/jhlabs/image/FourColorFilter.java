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
/*     */ public class FourColorFilter
/*     */   extends PointFilter
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private int colorNW;
/*     */   private int colorNE;
/*     */   private int colorSW;
/*     */   private int colorSE;
/*     */   private int rNW;
/*     */   private int gNW;
/*     */   private int bNW;
/*     */   private int rNE;
/*     */   private int gNE;
/*     */   private int bNE;
/*     */   private int rSW;
/*     */   private int gSW;
/*     */   private int bSW;
/*     */   private int rSE;
/*     */   private int gSE;
/*     */   private int bSE;
/*     */   
/*     */   public FourColorFilter() {
/*  41 */     setColorNW(-65536);
/*  42 */     setColorNE(-65281);
/*  43 */     setColorSW(-16776961);
/*  44 */     setColorSE(-16711681);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColorNW(int color) {
/*  49 */     this.colorNW = color;
/*  50 */     this.rNW = color >> 16 & 0xFF;
/*  51 */     this.gNW = color >> 8 & 0xFF;
/*  52 */     this.bNW = color & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColorNW() {
/*  57 */     return this.colorNW;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColorNE(int color) {
/*  62 */     this.colorNE = color;
/*  63 */     this.rNE = color >> 16 & 0xFF;
/*  64 */     this.gNE = color >> 8 & 0xFF;
/*  65 */     this.bNE = color & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColorNE() {
/*  70 */     return this.colorNE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColorSW(int color) {
/*  75 */     this.colorSW = color;
/*  76 */     this.rSW = color >> 16 & 0xFF;
/*  77 */     this.gSW = color >> 8 & 0xFF;
/*  78 */     this.bSW = color & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColorSW() {
/*  83 */     return this.colorSW;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColorSE(int color) {
/*  88 */     this.colorSE = color;
/*  89 */     this.rSE = color >> 16 & 0xFF;
/*  90 */     this.gSE = color >> 8 & 0xFF;
/*  91 */     this.bSE = color & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColorSE() {
/*  96 */     return this.colorSE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDimensions(int width, int height) {
/* 101 */     this.width = width;
/* 102 */     this.height = height;
/* 103 */     super.setDimensions(width, height);
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 108 */     float fx = x / this.width;
/* 109 */     float fy = y / this.height;
/*     */     
/* 111 */     float p = this.rNW + (this.rNE - this.rNW) * fx;
/* 112 */     float q = this.rSW + (this.rSE - this.rSW) * fx;
/* 113 */     int r = (int)(p + (q - p) * fy + 0.5F);
/* 114 */     p = this.gNW + (this.gNE - this.gNW) * fx;
/* 115 */     q = this.gSW + (this.gSE - this.gSW) * fx;
/* 116 */     int g = (int)(p + (q - p) * fy + 0.5F);
/* 117 */     p = this.bNW + (this.bNE - this.bNW) * fx;
/* 118 */     q = this.bSW + (this.bSE - this.bSW) * fx;
/* 119 */     int b = (int)(p + (q - p) * fy + 0.5F);
/* 120 */     return 0xFF000000 | r << 16 | g << 8 | b;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 125 */     return "Texture/Four Color Fill...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FourColorFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */