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
/*    */ public class CompositeFunction1D
/*    */   implements Function1D
/*    */ {
/*    */   private Function1D f1;
/*    */   private Function1D f2;
/*    */   
/*    */   public CompositeFunction1D(Function1D f1, Function1D f2) {
/* 25 */     this.f1 = f1;
/* 26 */     this.f2 = f2;
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float v) {
/* 31 */     return this.f1.evaluate(this.f2.evaluate(v));
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\CompositeFunction1D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */