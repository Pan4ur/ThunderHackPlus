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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SaturationFilter
/*    */   extends PointFilter
/*    */ {
/* 28 */   public float amount = 1.0F;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SaturationFilter() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SaturationFilter(float amount) {
/* 43 */     this.amount = amount;
/* 44 */     this.canFilterIndexColorModel = true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAmount(float amount) {
/* 54 */     this.amount = amount;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getAmount() {
/* 63 */     return this.amount;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 68 */     if (this.amount != 1.0F) {
/*    */       
/* 70 */       int a = rgb & 0xFF000000;
/* 71 */       int r = rgb >> 16 & 0xFF;
/* 72 */       int g = rgb >> 8 & 0xFF;
/* 73 */       int b = rgb & 0xFF;
/* 74 */       int v = (r + g + b) / 3;
/* 75 */       r = PixelUtils.clamp((int)(v + this.amount * (r - v)));
/* 76 */       g = PixelUtils.clamp((int)(v + this.amount * (g - v)));
/* 77 */       b = PixelUtils.clamp((int)(v + this.amount * (b - v)));
/* 78 */       return a | r << 16 | g << 8 | b;
/*    */     } 
/*    */     
/* 81 */     return rgb;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     return "Colors/Saturation...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SaturationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */