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
/*    */ public class SharpenFilter
/*    */   extends ConvolveFilter
/*    */ {
/* 26 */   private static float[] sharpenMatrix = new float[] { 0.0F, -0.2F, 0.0F, -0.2F, 1.8F, -0.2F, 0.0F, -0.2F, 0.0F };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SharpenFilter() {
/* 35 */     super(sharpenMatrix);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 40 */     return "Blur/Sharpen";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SharpenFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */