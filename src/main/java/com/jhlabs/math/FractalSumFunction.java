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
/*    */ public class FractalSumFunction
/*    */   extends CompoundFunction2D
/*    */ {
/* 21 */   private float octaves = 1.0F;
/*    */ 
/*    */   
/*    */   public FractalSumFunction(Function2D basis) {
/* 25 */     super(basis);
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float x, float y) {
/* 30 */     float t = 0.0F;
/*    */     float f;
/* 32 */     for (f = 1.0F; f <= this.octaves; f *= 2.0F)
/*    */     {
/* 34 */       t += this.basis.evaluate(f * x, f * y) / f;
/*    */     }
/*    */     
/* 37 */     return t;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\FractalSumFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */