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
/*    */ public class PremultiplyFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 34 */     int a = rgb >> 24 & 0xFF;
/* 35 */     int r = rgb >> 16 & 0xFF;
/* 36 */     int g = rgb >> 8 & 0xFF;
/* 37 */     int b = rgb & 0xFF;
/* 38 */     float f = a * 0.003921569F;
/* 39 */     r = (int)(r * f);
/* 40 */     g = (int)(g * f);
/* 41 */     b = (int)(b * f);
/* 42 */     return a << 24 | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return "Alpha/Premultiply";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PremultiplyFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */