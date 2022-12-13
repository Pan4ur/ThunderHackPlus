/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Color;
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
/*     */ public class HSBAdjustFilter
/*     */   extends PointFilter
/*     */ {
/*     */   public float hFactor;
/*     */   public float sFactor;
/*     */   public float bFactor;
/*  25 */   private float[] hsb = new float[3];
/*     */ 
/*     */   
/*     */   public HSBAdjustFilter() {
/*  29 */     this(0.0F, 0.0F, 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public HSBAdjustFilter(float r, float g, float b) {
/*  34 */     this.hFactor = r;
/*  35 */     this.sFactor = g;
/*  36 */     this.bFactor = b;
/*  37 */     this.canFilterIndexColorModel = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHFactor(float hFactor) {
/*  42 */     this.hFactor = hFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHFactor() {
/*  47 */     return this.hFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSFactor(float sFactor) {
/*  52 */     this.sFactor = sFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSFactor() {
/*  57 */     return this.sFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBFactor(float bFactor) {
/*  62 */     this.bFactor = bFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBFactor() {
/*  67 */     return this.bFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*  72 */     int a = rgb & 0xFF000000;
/*  73 */     int r = rgb >> 16 & 0xFF;
/*  74 */     int g = rgb >> 8 & 0xFF;
/*  75 */     int b = rgb & 0xFF;
/*  76 */     Color.RGBtoHSB(r, g, b, this.hsb);
/*  77 */     this.hsb[0] = this.hsb[0] + this.hFactor;
/*     */     
/*  79 */     while (this.hsb[0] < 0.0F)
/*     */     {
/*  81 */       this.hsb[0] = (float)(this.hsb[0] + 6.283185307179586D);
/*     */     }
/*     */     
/*  84 */     this.hsb[1] = this.hsb[1] + this.sFactor;
/*     */     
/*  86 */     if (this.hsb[1] < 0.0F) {
/*     */       
/*  88 */       this.hsb[1] = 0.0F;
/*     */     }
/*  90 */     else if (this.hsb[1] > 1.0D) {
/*     */       
/*  92 */       this.hsb[1] = 1.0F;
/*     */     } 
/*     */     
/*  95 */     this.hsb[2] = this.hsb[2] + this.bFactor;
/*     */     
/*  97 */     if (this.hsb[2] < 0.0F) {
/*     */       
/*  99 */       this.hsb[2] = 0.0F;
/*     */     }
/* 101 */     else if (this.hsb[2] > 1.0D) {
/*     */       
/* 103 */       this.hsb[2] = 1.0F;
/*     */     } 
/*     */     
/* 106 */     rgb = Color.HSBtoRGB(this.hsb[0], this.hsb[1], this.hsb[2]);
/* 107 */     return a | rgb & 0xFFFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 112 */     return "Colors/Adjust HSB...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\HSBAdjustFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */