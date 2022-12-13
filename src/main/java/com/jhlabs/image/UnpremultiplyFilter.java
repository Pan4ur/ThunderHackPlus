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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnpremultiplyFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 34 */     int a = rgb >> 24 & 0xFF;
/* 35 */     int r = rgb >> 16 & 0xFF;
/* 36 */     int g = rgb >> 8 & 0xFF;
/* 37 */     int b = rgb & 0xFF;
/*    */     
/* 39 */     if (a == 0 || a == 255)
/*    */     {
/* 41 */       return rgb;
/*    */     }
/*    */     
/* 44 */     float f = 255.0F / a;
/* 45 */     r = (int)(r * f);
/* 46 */     g = (int)(g * f);
/* 47 */     b = (int)(b * f);
/*    */     
/* 49 */     if (r > 255)
/*    */     {
/* 51 */       r = 255;
/*    */     }
/*    */     
/* 54 */     if (g > 255)
/*    */     {
/* 56 */       g = 255;
/*    */     }
/*    */     
/* 59 */     if (b > 255)
/*    */     {
/* 61 */       b = 255;
/*    */     }
/*    */     
/* 64 */     return a << 24 | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return "Alpha/Unpremultiply";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\UnpremultiplyFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */