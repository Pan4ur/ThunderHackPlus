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
/*    */ public class InvertFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 33 */     int a = rgb & 0xFF000000;
/* 34 */     return a | (rgb ^ 0xFFFFFFFF) & 0xFFFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 39 */     return "Colors/Invert";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\InvertFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */