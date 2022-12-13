/*    */ package com.jhlabs.math;
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
/*    */ public class VLNoise
/*    */   implements Function2D
/*    */ {
/* 21 */   private float distortion = 10.0F;
/*    */ 
/*    */   
/*    */   public void setDistortion(float distortion) {
/* 25 */     this.distortion = distortion;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getDistortion() {
/* 30 */     return this.distortion;
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float x, float y) {
/* 35 */     float ox = Noise.noise2(x + 0.5F, y) * this.distortion;
/* 36 */     float oy = Noise.noise2(x, y + 0.5F) * this.distortion;
/* 37 */     return Noise.noise2(x + ox, y + oy);
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\VLNoise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */