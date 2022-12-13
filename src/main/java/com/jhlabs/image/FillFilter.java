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
/*    */ public class FillFilter
/*    */   extends PointFilter
/*    */ {
/*    */   private int fillColor;
/*    */   
/*    */   public FillFilter() {
/* 35 */     this(-16777216);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FillFilter(int color) {
/* 44 */     this.fillColor = color;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setFillColor(int fillColor) {
/* 54 */     this.fillColor = fillColor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getFillColor() {
/* 64 */     return this.fillColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 69 */     return this.fillColor;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FillFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */