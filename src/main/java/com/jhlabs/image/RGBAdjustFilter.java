/*    */ package com.jhlabs.image;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RGBAdjustFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public float rFactor;
/*    */   public float gFactor;
/*    */   public float bFactor;
/*    */   
/*    */   public RGBAdjustFilter() {
/* 28 */     this(0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public RGBAdjustFilter(float r, float g, float b) {
/* 33 */     this.rFactor = 1.0F + r;
/* 34 */     this.gFactor = 1.0F + g;
/* 35 */     this.bFactor = 1.0F + b;
/* 36 */     this.canFilterIndexColorModel = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setRFactor(float rFactor) {
/* 41 */     this.rFactor = 1.0F + rFactor;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getRFactor() {
/* 46 */     return this.rFactor - 1.0F;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setGFactor(float gFactor) {
/* 51 */     this.gFactor = 1.0F + gFactor;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getGFactor() {
/* 56 */     return this.gFactor - 1.0F;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setBFactor(float bFactor) {
/* 61 */     this.bFactor = 1.0F + bFactor;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getBFactor() {
/* 66 */     return this.bFactor - 1.0F;
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] getLUT() {
/* 71 */     int[] lut = new int[256];
/*    */     
/* 73 */     for (int i = 0; i < 256; i++)
/*    */     {
/* 75 */       lut[i] = filterRGB(0, 0, i << 24 | i << 16 | i << 8 | i);
/*    */     }
/*    */     
/* 78 */     return lut;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 83 */     int a = rgb & 0xFF000000;
/* 84 */     int r = rgb >> 16 & 0xFF;
/* 85 */     int g = rgb >> 8 & 0xFF;
/* 86 */     int b = rgb & 0xFF;
/* 87 */     r = PixelUtils.clamp((int)(r * this.rFactor));
/* 88 */     g = PixelUtils.clamp((int)(g * this.gFactor));
/* 89 */     b = PixelUtils.clamp((int)(b * this.bFactor));
/* 90 */     return a | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 95 */     return "Colors/Adjust RGB...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RGBAdjustFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */