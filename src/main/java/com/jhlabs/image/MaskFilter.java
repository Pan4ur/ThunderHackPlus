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
/*    */ public class MaskFilter
/*    */   extends PointFilter
/*    */ {
/*    */   private int mask;
/*    */   
/*    */   public MaskFilter() {
/* 32 */     this(-16711681);
/*    */   }
/*    */ 
/*    */   
/*    */   public MaskFilter(int mask) {
/* 37 */     this.canFilterIndexColorModel = true;
/* 38 */     setMask(mask);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMask(int mask) {
/* 43 */     this.mask = mask;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMask() {
/* 48 */     return this.mask;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 53 */     return rgb & this.mask;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     return "Mask";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MaskFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */