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
/*     */ public class GlowFilter
/*     */   extends GaussianFilter
/*     */ {
/*  27 */   private float amount = 0.5F;
/*     */ 
/*     */   
/*     */   public GlowFilter() {
/*  31 */     this.radius = 2.0F;
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
/*  43 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  53 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  58 */     int width = src.getWidth();
/*  59 */     int height = src.getHeight();
/*     */     
/*  61 */     if (dst == null)
/*     */     {
/*  63 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  66 */     int[] inPixels = new int[width * height];
/*  67 */     int[] outPixels = new int[width * height];
/*  68 */     src.getRGB(0, 0, width, height, inPixels, 0, width);
/*     */     
/*  70 */     if (this.radius > 0.0F) {
/*     */       
/*  72 */       convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, (this.alpha && this.premultiplyAlpha), false, CLAMP_EDGES);
/*  73 */       convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false, (this.alpha && this.premultiplyAlpha), CLAMP_EDGES);
/*     */     } 
/*     */     
/*  76 */     src.getRGB(0, 0, width, height, outPixels, 0, width);
/*  77 */     float a = 4.0F * this.amount;
/*  78 */     int index = 0;
/*     */     
/*  80 */     for (int y = 0; y < height; y++) {
/*     */       
/*  82 */       for (int x = 0; x < width; x++) {
/*     */         
/*  84 */         int rgb1 = outPixels[index];
/*  85 */         int r1 = rgb1 >> 16 & 0xFF;
/*  86 */         int g1 = rgb1 >> 8 & 0xFF;
/*  87 */         int b1 = rgb1 & 0xFF;
/*  88 */         int rgb2 = inPixels[index];
/*  89 */         int r2 = rgb2 >> 16 & 0xFF;
/*  90 */         int g2 = rgb2 >> 8 & 0xFF;
/*  91 */         int b2 = rgb2 & 0xFF;
/*  92 */         r1 = PixelUtils.clamp((int)(r1 + a * r2));
/*  93 */         g1 = PixelUtils.clamp((int)(g1 + a * g2));
/*  94 */         b1 = PixelUtils.clamp((int)(b1 + a * b2));
/*  95 */         inPixels[index] = rgb1 & 0xFF000000 | r1 << 16 | g1 << 8 | b1;
/*  96 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 100 */     dst.setRGB(0, 0, width, height, inPixels, 0, width);
/* 101 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     return "Blur/Glow...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GlowFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */