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
/*    */ public class GainFilter
/*    */   extends TransferFilter
/*    */ {
/* 27 */   private float gain = 0.5F;
/* 28 */   private float bias = 0.5F;
/*    */ 
/*    */   
/*    */   protected float transferFunction(float f) {
/* 32 */     f = ImageMath.gain(f, this.gain);
/* 33 */     f = ImageMath.bias(f, this.bias);
/* 34 */     return f;
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
/*    */   public void setGain(float gain) {
/* 46 */     this.gain = gain;
/* 47 */     this.initialized = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getGain() {
/* 57 */     return this.gain;
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
/*    */   public void setBias(float bias) {
/* 69 */     this.bias = bias;
/* 70 */     this.initialized = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getBias() {
/* 80 */     return this.bias;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return "Colors/Gain...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GainFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */