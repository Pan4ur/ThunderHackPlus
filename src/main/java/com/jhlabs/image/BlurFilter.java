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
/*    */ 
/*    */ 
/*    */ public class BlurFilter
/*    */   extends ConvolveFilter
/*    */ {
/* 29 */   protected static float[] blurMatrix = new float[] { 0.071428575F, 0.14285715F, 0.071428575F, 0.14285715F, 0.14285715F, 0.14285715F, 0.071428575F, 0.14285715F, 0.071428575F };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BlurFilter() {
/* 38 */     super(blurMatrix);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 43 */     return "Blur/Simple Blur";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\BlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */