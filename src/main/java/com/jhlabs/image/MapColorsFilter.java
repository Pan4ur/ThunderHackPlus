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
/*    */ public class MapColorsFilter
/*    */   extends PointFilter
/*    */ {
/*    */   private int oldColor;
/*    */   private int newColor;
/*    */   
/*    */   public MapColorsFilter() {
/* 35 */     this(-1, -16777216);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MapColorsFilter(int oldColor, int newColor) {
/* 45 */     this.canFilterIndexColorModel = true;
/* 46 */     this.oldColor = oldColor;
/* 47 */     this.newColor = newColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 52 */     if (rgb == this.oldColor)
/*    */     {
/* 54 */       return this.newColor;
/*    */     }
/*    */     
/* 57 */     return rgb;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MapColorsFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */