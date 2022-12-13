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
/*    */ public final class OverlayComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public OverlayComposite(float alpha) {
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
/* 47 */         int dor, dog, dob, sr = src[i];
/* 48 */         int dir = dst[i];
/* 49 */         int sg = src[i + 1];
/* 50 */         int dig = dst[i + 1];
/* 51 */         int sb = src[i + 2];
/* 52 */         int dib = dst[i + 2];
/* 53 */         int sa = src[i + 3];
/* 54 */         int dia = dst[i + 3];
/*    */ 
/*    */ 
/*    */         
/* 58 */         if (dir < 128) {
/*    */           
/* 60 */           int t = dir * sr + 128;
/* 61 */           dor = 2 * ((t >> 8) + t >> 8);
/*    */         }
/*    */         else {
/*    */           
/* 65 */           int t = (255 - dir) * (255 - sr) + 128;
/* 66 */           dor = 2 * (255 - ((t >> 8) + t >> 8));
/*    */         } 
/*    */         
/* 69 */         if (dig < 128) {
/*    */           
/* 71 */           int j = dig * sg + 128;
/* 72 */           dog = 2 * ((j >> 8) + j >> 8);
/*    */         }
/*    */         else {
/*    */           
/* 76 */           int j = (255 - dig) * (255 - sg) + 128;
/* 77 */           dog = 2 * (255 - ((j >> 8) + j >> 8));
/*    */         } 
/*    */         
/* 80 */         if (dib < 128) {
/*    */           
/* 82 */           int j = dib * sb + 128;
/* 83 */           dob = 2 * ((j >> 8) + j >> 8);
/*    */         }
/*    */         else {
/*    */           
/* 87 */           int j = (255 - dib) * (255 - sb) + 128;
/* 88 */           dob = 2 * (255 - ((j >> 8) + j >> 8));
/*    */         } 
/*    */         
/* 91 */         float a = alpha * sa / 255.0F;
/* 92 */         float ac = 1.0F - a;
/* 93 */         dst[i] = (int)(a * dor + ac * dir);
/* 94 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 95 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 96 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\OverlayComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */