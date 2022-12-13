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
/*    */ public class ExposureFilter
/*    */   extends TransferFilter
/*    */ {
/* 27 */   private float exposure = 1.0F;
/*    */ 
/*    */   
/*    */   protected float transferFunction(float f) {
/* 31 */     return 1.0F - (float)Math.exp((-f * this.exposure));
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
/*    */   public void setExposure(float exposure) {
/* 43 */     this.exposure = exposure;
/* 44 */     this.initialized = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getExposure() {
/* 54 */     return this.exposure;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "Colors/Exposure...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ExposureFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */