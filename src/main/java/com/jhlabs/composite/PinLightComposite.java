/*    */ package com.jhlabs.composite;
/*    */ 
/*    */ import java.awt.CompositeContext;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.image.ColorModel;
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
/*    */ public final class PinLightComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public PinLightComposite(float alpha) {
/* 26 */     super(alpha);
/*    */   }
/*    */ 
/*    */   
/*    */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 31 */     return new Context(this.extraAlpha, srcColorModel, dstColorModel);
/*    */   }
/*    */   
/*    */   static class Context
/*    */     extends RGBComposite.RGBCompositeContext
/*    */   {
/*    */     public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
/* 38 */       super(alpha, srcColorModel, dstColorModel);
/*    */     }
/*    */ 
/*    */     
/*    */     public void composeRGB(int[] src, int[] dst, float alpha) {
/* 43 */       int w = src.length;
/*    */       
/* 45 */       for (int i = 0; i < w; i += 4) {
/*    */         
/* 47 */         int sr = src[i];
/* 48 */         int dir = dst[i];
/* 49 */         int sg = src[i + 1];
/* 50 */         int dig = dst[i + 1];
/* 51 */         int sb = src[i + 2];
/* 52 */         int dib = dst[i + 2];
/* 53 */         int sa = src[i + 3];
/* 54 */         int dia = dst[i + 3];
/*    */         
/* 56 */         int dor = (sr > 127) ? Math.max(sr, dir) : Math.min(sr, dir);
/* 57 */         int dog = (sg > 127) ? Math.max(sg, dig) : Math.min(sg, dig);
/* 58 */         int dob = (sb > 127) ? Math.max(sb, dib) : Math.min(sb, dib);
/* 59 */         float a = alpha * sa / 255.0F;
/* 60 */         float ac = 1.0F - a;
/* 61 */         dst[i] = (int)(a * dor + ac * dir);
/* 62 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 63 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 64 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\PinLightComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */