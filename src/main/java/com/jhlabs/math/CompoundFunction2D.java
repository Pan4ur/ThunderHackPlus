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
/*    */ public abstract class CompoundFunction2D
/*    */   implements Function2D
/*    */ {
/*    */   protected Function2D basis;
/*    */   
/*    */   public CompoundFunction2D(Function2D basis) {
/* 25 */     this.basis = basis;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setBasis(Function2D basis) {
/* 30 */     this.basis = basis;
/*    */   }
/*    */ 
/*    */   
/*    */   public Function2D getBasis() {
/* 35 */     return this.basis;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\CompoundFunction2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */