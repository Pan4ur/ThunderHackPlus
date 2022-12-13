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
/*    */ public class OpacityFilter
/*    */   extends PointFilter
/*    */ {
/*    */   private int opacity;
/*    */   private int opacity24;
/*    */   
/*    */   public OpacityFilter() {
/* 35 */     this(136);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OpacityFilter(int opacity) {
/* 44 */     setOpacity(opacity);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setOpacity(int opacity) {
/* 54 */     this.opacity = opacity;
/* 55 */     this.opacity24 = opacity << 24;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getOpacity() {
/* 65 */     return this.opacity;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 70 */     if ((rgb & 0xFF000000) != 0)
/*    */     {
/* 72 */       return rgb & 0xFFFFFF | this.opacity24;
/*    */     }
/*    */     
/* 75 */     return rgb;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     return "Colors/Transparency...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\OpacityFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */