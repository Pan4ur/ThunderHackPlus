/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.Kernel;
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
/*     */ public class SmartBlurFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  28 */   private int hRadius = 5;
/*  29 */   private int vRadius = 5;
/*  30 */   private int threshold = 10;
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  34 */     int width = src.getWidth();
/*  35 */     int height = src.getHeight();
/*     */     
/*  37 */     if (dst == null)
/*     */     {
/*  39 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  42 */     int[] inPixels = new int[width * height];
/*  43 */     int[] outPixels = new int[width * height];
/*  44 */     getRGB(src, 0, 0, width, height, inPixels);
/*  45 */     Kernel kernel = GaussianFilter.makeKernel(this.hRadius);
/*  46 */     thresholdBlur(kernel, inPixels, outPixels, width, height, true);
/*  47 */     thresholdBlur(kernel, outPixels, inPixels, height, width, true);
/*  48 */     setRGB(dst, 0, 0, width, height, inPixels);
/*  49 */     return dst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void thresholdBlur(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha) {
/*  57 */     int index = 0;
/*  58 */     float[] matrix = kernel.getKernelData(null);
/*  59 */     int cols = kernel.getWidth();
/*  60 */     int cols2 = cols / 2;
/*     */     
/*  62 */     for (int y = 0; y < height; y++) {
/*     */       
/*  64 */       int ioffset = y * width;
/*  65 */       int outIndex = y;
/*     */       
/*  67 */       for (int x = 0; x < width; x++) {
/*     */         
/*  69 */         float r = 0.0F, g = 0.0F, b = 0.0F, a = 0.0F;
/*  70 */         int moffset = cols2;
/*  71 */         int rgb1 = inPixels[ioffset + x];
/*  72 */         int a1 = rgb1 >> 24 & 0xFF;
/*  73 */         int r1 = rgb1 >> 16 & 0xFF;
/*  74 */         int g1 = rgb1 >> 8 & 0xFF;
/*  75 */         int b1 = rgb1 & 0xFF;
/*  76 */         float af = 0.0F, rf = 0.0F, gf = 0.0F, bf = 0.0F;
/*     */         
/*  78 */         for (int col = -cols2; col <= cols2; col++) {
/*     */           
/*  80 */           float f = matrix[moffset + col];
/*     */           
/*  82 */           if (f != 0.0F) {
/*     */             
/*  84 */             int ix = x + col;
/*     */             
/*  86 */             if (0 > ix || ix >= width)
/*     */             {
/*  88 */               ix = x;
/*     */             }
/*     */             
/*  91 */             int rgb2 = inPixels[ioffset + ix];
/*  92 */             int a2 = rgb2 >> 24 & 0xFF;
/*  93 */             int r2 = rgb2 >> 16 & 0xFF;
/*  94 */             int g2 = rgb2 >> 8 & 0xFF;
/*  95 */             int b2 = rgb2 & 0xFF;
/*     */             
/*  97 */             int d = a1 - a2;
/*     */             
/*  99 */             if (d >= -this.threshold && d <= this.threshold) {
/*     */               
/* 101 */               a += f * a2;
/* 102 */               af += f;
/*     */             } 
/*     */             
/* 105 */             d = r1 - r2;
/*     */             
/* 107 */             if (d >= -this.threshold && d <= this.threshold) {
/*     */               
/* 109 */               r += f * r2;
/* 110 */               rf += f;
/*     */             } 
/*     */             
/* 113 */             d = g1 - g2;
/*     */             
/* 115 */             if (d >= -this.threshold && d <= this.threshold) {
/*     */               
/* 117 */               g += f * g2;
/* 118 */               gf += f;
/*     */             } 
/*     */             
/* 121 */             d = b1 - b2;
/*     */             
/* 123 */             if (d >= -this.threshold && d <= this.threshold) {
/*     */               
/* 125 */               b += f * b2;
/* 126 */               bf += f;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 131 */         a = (af == 0.0F) ? a1 : (a / af);
/* 132 */         r = (rf == 0.0F) ? r1 : (r / rf);
/* 133 */         g = (gf == 0.0F) ? g1 : (g / gf);
/* 134 */         b = (bf == 0.0F) ? b1 : (b / bf);
/* 135 */         int ia = alpha ? PixelUtils.clamp((int)(a + 0.5D)) : 255;
/* 136 */         int ir = PixelUtils.clamp((int)(r + 0.5D));
/* 137 */         int ig = PixelUtils.clamp((int)(g + 0.5D));
/* 138 */         int ib = PixelUtils.clamp((int)(b + 0.5D));
/* 139 */         outPixels[outIndex] = ia << 24 | ir << 16 | ig << 8 | ib;
/* 140 */         outIndex += height;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHRadius(int hRadius) {
/* 153 */     this.hRadius = hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHRadius() {
/* 163 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVRadius(int vRadius) {
/* 174 */     this.vRadius = vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVRadius() {
/* 184 */     return this.vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(int radius) {
/* 195 */     this.hRadius = this.vRadius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRadius() {
/* 205 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreshold(int threshold) {
/* 215 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreshold() {
/* 225 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 230 */     return "Blur/Smart Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SmartBlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */