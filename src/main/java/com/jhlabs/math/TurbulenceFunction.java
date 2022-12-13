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
/*    */ public class TurbulenceFunction
/*    */   extends CompoundFunction2D
/*    */ {
/*    */   private float octaves;
/*    */   
/*    */   public TurbulenceFunction(Function2D basis, float octaves) {
/* 25 */     super(basis);
/* 26 */     this.octaves = octaves;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setOctaves(float octaves) {
/* 31 */     this.octaves = octaves;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getOctaves() {
/* 36 */     return this.octaves;
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float x, float y) {
/* 41 */     float t = 0.0F;
/*    */     float f;
/* 43 */     for (f = 1.0F; f <= this.octaves; f *= 2.0F)
/*    */     {
/* 45 */       t += Math.abs(this.basis.evaluate(f * x, f * y)) / f;
/*    */     }
/*    */     
/* 48 */     return t;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\TurbulenceFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */