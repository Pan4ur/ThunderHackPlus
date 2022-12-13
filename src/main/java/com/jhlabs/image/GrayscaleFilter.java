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
/*    */ public class GrayscaleFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 34 */     int a = rgb & 0xFF000000;
/* 35 */     int r = rgb >> 16 & 0xFF;
/* 36 */     int g = rgb >> 8 & 0xFF;
/* 37 */     int b = rgb & 0xFF;
/*    */     
/* 39 */     rgb = r * 77 + g * 151 + b * 28 >> 8;
/* 40 */     return a | rgb << 16 | rgb << 8 | rgb;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     return "Colors/Grayscale";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GrayscaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */