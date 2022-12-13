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
/*    */ public class RescaleFilter
/*    */   extends TransferFilter
/*    */ {
/* 27 */   private float scale = 1.0F;
/*    */ 
/*    */ 
/*    */   
/*    */   public RescaleFilter() {}
/*    */ 
/*    */   
/*    */   public RescaleFilter(float scale) {
/* 35 */     this.scale = scale;
/*    */   }
/*    */ 
/*    */   
/*    */   protected float transferFunction(float v) {
/* 40 */     return v * this.scale;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setScale(float scale) {
/* 52 */     this.scale = scale;
/* 53 */     this.initialized = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getScale() {
/* 63 */     return this.scale;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return "Colors/Rescale...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RescaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */