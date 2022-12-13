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
/*    */ 
/*    */ public class RidgedFBM
/*    */   implements Function2D
/*    */ {
/*    */   public float evaluate(float x, float y) {
/* 23 */     return 1.0F - Math.abs(Noise.noise2(x, y));
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\RidgedFBM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */