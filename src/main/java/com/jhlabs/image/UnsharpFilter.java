/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class UnsharpFilter
/*     */   extends GaussianFilter
/*     */ {
/*  27 */   private float amount = 0.5F;
/*  28 */   private int threshold = 1;
/*     */ 
/*     */   
/*     */   public UnsharpFilter() {
/*  32 */     this.radius = 2.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreshold(int threshold) {
/*  42 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreshold() {
/*  52 */     return this.threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAmount(float amount) {
/*  64 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  74 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  79 */     int width = src.getWidth();
/*  80 */     int height = src.getHeight();
/*     */     
/*  82 */     if (dst == null)
/*     */     {
/*  84 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  87 */     int[] inPixels = new int[width * height];
/*  88 */     int[] outPixels = new int[width * height];
/*  89 */     src.getRGB(0, 0, width, height, inPixels, 0, width);
/*     */     
/*  91 */     if (this.radius > 0.0F) {
/*     */       
/*  93 */       convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, (this.alpha && this.premultiplyAlpha), false, CLAMP_EDGES);
/*  94 */       convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false, (this.alpha && this.premultiplyAlpha), CLAMP_EDGES);
/*     */     } 
/*     */     
/*  97 */     src.getRGB(0, 0, width, height, outPixels, 0, width);
/*  98 */     float a = 4.0F * this.amount;
/*  99 */     int index = 0;
/*     */     
/* 101 */     for (int y = 0; y < height; y++) {
/*     */       
/* 103 */       for (int x = 0; x < width; x++) {
/*     */         
/* 105 */         int rgb1 = outPixels[index];
/* 106 */         int r1 = rgb1 >> 16 & 0xFF;
/* 107 */         int g1 = rgb1 >> 8 & 0xFF;
/* 108 */         int b1 = rgb1 & 0xFF;
/* 109 */         int rgb2 = inPixels[index];
/* 110 */         int r2 = rgb2 >> 16 & 0xFF;
/* 111 */         int g2 = rgb2 >> 8 & 0xFF;
/* 112 */         int b2 = rgb2 & 0xFF;
/*     */         
/* 114 */         if (Math.abs(r1 - r2) >= this.threshold)
/*     */         {
/* 116 */           r1 = PixelUtils.clamp((int)((a + 1.0F) * (r1 - r2) + r2));
/*     */         }
/*     */         
/* 119 */         if (Math.abs(g1 - g2) >= this.threshold)
/*     */         {
/* 121 */           g1 = PixelUtils.clamp((int)((a + 1.0F) * (g1 - g2) + g2));
/*     */         }
/*     */         
/* 124 */         if (Math.abs(b1 - b2) >= this.threshold)
/*     */         {
/* 126 */           b1 = PixelUtils.clamp((int)((a + 1.0F) * (b1 - b2) + b2));
/*     */         }
/*     */         
/* 129 */         inPixels[index] = rgb1 & 0xFF000000 | r1 << 16 | g1 << 8 | b1;
/* 130 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 134 */     dst.setRGB(0, 0, width, height, inPixels, 0, width);
/* 135 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return "Blur/Unsharp Mask...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\UnsharpFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */