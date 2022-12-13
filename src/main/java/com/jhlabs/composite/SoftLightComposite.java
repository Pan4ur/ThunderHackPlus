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
/*    */ public final class SoftLightComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public SoftLightComposite(float alpha) {
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
/*    */         
/* 57 */         int d = multiply255(sr, dir);
/* 58 */         int dor = d + multiply255(dir, 255 - multiply255(255 - dir, 255 - sr) - d);
/* 59 */         d = multiply255(sg, dig);
/* 60 */         int dog = d + multiply255(dig, 255 - multiply255(255 - dig, 255 - sg) - d);
/* 61 */         d = multiply255(sb, dib);
/* 62 */         int dob = d + multiply255(dib, 255 - multiply255(255 - dib, 255 - sb) - d);
/* 63 */         float a = alpha * sa / 255.0F;
/* 64 */         float ac = 1.0F - a;
/* 65 */         dst[i] = (int)(a * dor + ac * dir);
/* 66 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 67 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 68 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\SoftLightComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */