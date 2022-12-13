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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JavaLnFFilter
/*    */   extends PointFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 33 */     if ((x & 0x1) == (y & 0x1))
/*    */     {
/* 35 */       return rgb;
/*    */     }
/*    */     
/* 38 */     return ImageMath.mixColors(0.25F, -6710887, rgb);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 43 */     return "Stylize/Java L&F Stipple";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\JavaLnFFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */