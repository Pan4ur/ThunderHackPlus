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
/*    */ public class SolarizeFilter
/*    */   extends TransferFilter
/*    */ {
/*    */   protected float transferFunction(float v) {
/* 28 */     return (v > 0.5F) ? (2.0F * (v - 0.5F)) : (2.0F * (0.5F - v));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 33 */     return "Colors/Solarize";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SolarizeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */