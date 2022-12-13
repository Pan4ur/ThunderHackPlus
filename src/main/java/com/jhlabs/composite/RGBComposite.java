/*     */ package com.jhlabs.composite;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.CompositeContext;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RGBComposite
/*     */   implements Composite
/*     */ {
/*     */   protected float extraAlpha;
/*     */   
/*     */   public RGBComposite() {
/*  28 */     this(1.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public RGBComposite(float alpha) {
/*  33 */     if (alpha < 0.0F || alpha > 1.0F)
/*     */     {
/*  35 */       throw new IllegalArgumentException("RGBComposite: alpha must be between 0 and 1");
/*     */     }
/*     */     
/*  38 */     this.extraAlpha = alpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAlpha() {
/*  43 */     return this.extraAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  48 */     return Float.floatToIntBits(this.extraAlpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  53 */     if (!(o instanceof RGBComposite))
/*     */     {
/*  55 */       return false;
/*     */     }
/*     */     
/*  58 */     RGBComposite c = (RGBComposite)o;
/*     */     
/*  60 */     if (this.extraAlpha != c.extraAlpha)
/*     */     {
/*  62 */       return false;
/*     */     }
/*     */     
/*  65 */     return true;
/*     */   }
/*     */   
/*     */   public static abstract class RGBCompositeContext
/*     */     implements CompositeContext
/*     */   {
/*     */     private float alpha;
/*     */     private ColorModel srcColorModel;
/*     */     private ColorModel dstColorModel;
/*     */     
/*     */     public RGBCompositeContext(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
/*  76 */       this.alpha = alpha;
/*  77 */       this.srcColorModel = srcColorModel;
/*  78 */       this.dstColorModel = dstColorModel;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void dispose() {}
/*     */ 
/*     */ 
/*     */     
/*     */     static int multiply255(int a, int b) {
/*  88 */       int t = a * b + 128;
/*  89 */       return (t >> 8) + t >> 8;
/*     */     }
/*     */ 
/*     */     
/*     */     static int clamp(int a) {
/*  94 */       return (a < 0) ? 0 : ((a > 255) ? 255 : a);
/*     */     }
/*     */ 
/*     */     
/*     */     public abstract void composeRGB(int[] param1ArrayOfint1, int[] param1ArrayOfint2, float param1Float);
/*     */     
/*     */     public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
/* 101 */       float alpha = this.alpha;
/* 102 */       int[] srcPix = null;
/* 103 */       int[] dstPix = null;
/* 104 */       int x = dstOut.getMinX();
/* 105 */       int w = dstOut.getWidth();
/* 106 */       int y0 = dstOut.getMinY();
/* 107 */       int y1 = y0 + dstOut.getHeight();
/*     */       
/* 109 */       for (int y = y0; y < y1; y++) {
/*     */         
/* 111 */         srcPix = src.getPixels(x, y, w, 1, srcPix);
/* 112 */         dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
/* 113 */         composeRGB(srcPix, dstPix, alpha);
/* 114 */         dstOut.setPixels(x, y, w, 1, dstPix);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\RGBComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */