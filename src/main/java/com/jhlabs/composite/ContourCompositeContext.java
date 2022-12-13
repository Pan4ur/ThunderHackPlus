/*     */ package com.jhlabs.composite;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ContourCompositeContext
/*     */   implements CompositeContext
/*     */ {
/*     */   private int offset;
/*     */   
/*     */   public ContourCompositeContext(int offset, ColorModel srcColorModel, ColorModel dstColorModel) {
/*  63 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {}
/*     */ 
/*     */   
/*     */   public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
/*  72 */     int x = src.getMinX();
/*  73 */     int y = src.getMinY();
/*  74 */     int w = src.getWidth();
/*  75 */     int h = src.getHeight();
/*  76 */     int[] srcPix = null;
/*  77 */     int[] srcPix2 = null;
/*  78 */     int[] dstInPix = null;
/*  79 */     int[] dstOutPix = new int[w * 4];
/*     */     
/*  81 */     for (int i = 0; i < h; i++) {
/*     */       
/*  83 */       srcPix = src.getPixels(x, y, w, 1, srcPix);
/*  84 */       dstInPix = dstIn.getPixels(x, y, w, 1, dstInPix);
/*  85 */       int lastAlpha = 0;
/*  86 */       int k = 0;
/*     */       
/*  88 */       for (int j = 0; j < w; j++) {
/*     */         
/*  90 */         int alpha = srcPix[k + 3];
/*  91 */         int alphaAbove = (i != 0) ? srcPix2[k + 3] : alpha;
/*     */         
/*  93 */         if ((i != 0 && j != 0 && ((alpha ^ lastAlpha) & 0x80) != 0) || ((alpha ^ alphaAbove) & 0x80) != 0) {
/*     */           
/*  95 */           if ((this.offset + i + j) % 10 > 4) {
/*     */             
/*  97 */             dstOutPix[k] = 0;
/*  98 */             dstOutPix[k + 1] = 0;
/*  99 */             dstOutPix[k + 2] = 0;
/*     */           }
/*     */           else {
/*     */             
/* 103 */             dstOutPix[k] = 255;
/* 104 */             dstOutPix[k + 1] = 255;
/* 105 */             dstOutPix[k + 2] = 127;
/*     */           } 
/*     */           
/* 108 */           dstOutPix[k + 3] = 255;
/*     */         }
/*     */         else {
/*     */           
/* 112 */           dstOutPix[k] = dstInPix[k];
/* 113 */           dstOutPix[k + 1] = dstInPix[k + 1];
/* 114 */           dstOutPix[k + 2] = dstInPix[k + 2];
/*     */           
/* 116 */           dstOutPix[k] = 255;
/* 117 */           dstOutPix[k + 1] = 0;
/* 118 */           dstOutPix[k + 2] = 0;
/* 119 */           dstOutPix[k + 3] = 0;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 124 */         lastAlpha = alpha;
/* 125 */         k += 4;
/*     */       } 
/*     */       
/* 128 */       dstOut.setPixels(x, y, w, 1, dstOutPix);
/* 129 */       int[] t = srcPix;
/* 130 */       srcPix = srcPix2;
/* 131 */       srcPix2 = t;
/* 132 */       y++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\ContourCompositeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */