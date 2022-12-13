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
/*    */ 
/*    */ public class MathFunction1D
/*    */   implements Function1D
/*    */ {
/*    */   public static final int SIN = 1;
/*    */   public static final int COS = 2;
/*    */   public static final int TAN = 3;
/*    */   public static final int SQRT = 4;
/*    */   public static final int ASIN = -1;
/*    */   public static final int ACOS = -2;
/*    */   public static final int ATAN = -3;
/*    */   public static final int SQR = -4;
/*    */   private int operation;
/*    */   
/*    */   public MathFunction1D(int operation) {
/* 34 */     this.operation = operation;
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float v) {
/* 39 */     switch (this.operation) {
/*    */       
/*    */       case 1:
/* 42 */         return (float)Math.sin(v);
/*    */       
/*    */       case 2:
/* 45 */         return (float)Math.cos(v);
/*    */       
/*    */       case 3:
/* 48 */         return (float)Math.tan(v);
/*    */       
/*    */       case 4:
/* 51 */         return (float)Math.sqrt(v);
/*    */       
/*    */       case -1:
/* 54 */         return (float)Math.asin(v);
/*    */       
/*    */       case -2:
/* 57 */         return (float)Math.acos(v);
/*    */       
/*    */       case -3:
/* 60 */         return (float)Math.atan(v);
/*    */       
/*    */       case -4:
/* 63 */         return v * v;
/*    */     } 
/*    */     
/* 66 */     return v;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\MathFunction1D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */