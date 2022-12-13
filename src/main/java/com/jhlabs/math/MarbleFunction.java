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
/*    */ public class MarbleFunction
/*    */   extends CompoundFunction2D
/*    */ {
/*    */   public MarbleFunction() {
/* 23 */     super(new TurbulenceFunction(new Noise(), 6.0F));
/*    */   }
/*    */ 
/*    */   
/*    */   public MarbleFunction(Function2D basis) {
/* 28 */     super(basis);
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float x, float y) {
/* 33 */     return (float)Math.pow(0.5D * (Math.sin(8.0D * this.basis.evaluate(x, y)) + 1.0D), 0.77D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\MarbleFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */