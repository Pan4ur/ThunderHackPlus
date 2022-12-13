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
/*    */ public final class SubtractComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public SubtractComposite(float alpha) {
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
/* 56 */         int dor = dir - sr;
/*    */         
/* 58 */         if (dor < 0)
/*    */         {
/* 60 */           dor = 0;
/*    */         }
/*    */         
/* 63 */         int dog = dig - sg;
/*    */         
/* 65 */         if (dog < 0)
/*    */         {
/* 67 */           dog = 0;
/*    */         }
/*    */         
/* 70 */         int dob = dib - sb;
/*    */         
/* 72 */         if (dob < 0)
/*    */         {
/* 74 */           dob = 0;
/*    */         }
/*    */         
/* 77 */         float a = alpha * sa / 255.0F;
/* 78 */         float ac = 1.0F - a;
/* 79 */         dst[i] = (int)(a * dor + ac * dir);
/* 80 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 81 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 82 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\SubtractComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */