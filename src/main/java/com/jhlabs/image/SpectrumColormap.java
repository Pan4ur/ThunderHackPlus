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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SpectrumColormap
/*    */   implements Colormap
/*    */ {
/*    */   public int getColor(float v) {
/* 38 */     return Spectrum.wavelengthToRGB(380.0F + 400.0F * ImageMath.clamp(v, 0.0F, 1.0F));
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SpectrumColormap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */