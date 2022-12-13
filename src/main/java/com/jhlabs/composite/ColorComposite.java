/*    */ package com.jhlabs.composite;
/*    */ 
/*    */ import java.awt.Color;
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
/*    */ public final class ColorComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public ColorComposite(float alpha) {
/* 26 */     super(alpha);
/*    */   }
/*    */ 
/*    */   
/*    */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 31 */     return new Context(this.extraAlpha, srcColorModel, dstColorModel);
/*    */   }
/*    */   
/*    */   static class Context
/*    */     extends RGBComposite.RGBCompositeContext {
/* 36 */     private float[] sHSB = new float[3];
/* 37 */     private float[] dHSB = new float[3];
/*    */ 
/*    */     
/*    */     public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
/* 41 */       super(alpha, srcColorModel, dstColorModel);
/*    */     }
/*    */ 
/*    */     
/*    */     public void composeRGB(int[] src, int[] dst, float alpha) {
/* 46 */       int w = src.length;
/*    */       
/* 48 */       for (int i = 0; i < w; i += 4) {
/*    */         
/* 50 */         int sr = src[i];
/* 51 */         int dir = dst[i];
/* 52 */         int sg = src[i + 1];
/* 53 */         int dig = dst[i + 1];
/* 54 */         int sb = src[i + 2];
/* 55 */         int dib = dst[i + 2];
/* 56 */         int sa = src[i + 3];
/* 57 */         int dia = dst[i + 3];
/*    */         
/* 59 */         Color.RGBtoHSB(sr, sg, sb, this.sHSB);
/* 60 */         Color.RGBtoHSB(dir, dig, dib, this.dHSB);
/* 61 */         this.dHSB[0] = this.sHSB[0];
/* 62 */         this.dHSB[1] = this.sHSB[1];
/* 63 */         int doRGB = Color.HSBtoRGB(this.dHSB[0], this.dHSB[1], this.dHSB[2]);
/* 64 */         int dor = (doRGB & 0xFF0000) >> 16;
/* 65 */         int dog = (doRGB & 0xFF00) >> 8;
/* 66 */         int dob = doRGB & 0xFF;
/* 67 */         float a = alpha * sa / 255.0F;
/* 68 */         float ac = 1.0F - a;
/* 69 */         dst[i] = (int)(a * dor + ac * dir);
/* 70 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 71 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 72 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\ColorComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */