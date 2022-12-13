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
/*    */ public class GrayFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 34 */     int a = rgb & 0xFF000000;
/* 35 */     int r = rgb >> 16 & 0xFF;
/* 36 */     int g = rgb >> 8 & 0xFF;
/* 37 */     int b = rgb & 0xFF;
/* 38 */     r = (r + 255) / 2;
/* 39 */     g = (g + 255) / 2;
/* 40 */     b = (b + 255) / 2;
/* 41 */     return a | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return "Colors/Gray Out";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GrayFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */