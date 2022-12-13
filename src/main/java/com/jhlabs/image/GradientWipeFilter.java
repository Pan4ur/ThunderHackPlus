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
/*     */ public class GradientWipeFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  27 */   private float density = 0.0F;
/*  28 */   private float softness = 0.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean invert;
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage mask;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDensity(float density) {
/*  42 */     this.density = density;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDensity() {
/*  47 */     return this.density;
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
/*     */   public void setSoftness(float softness) {
/*  59 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/*  69 */     return this.softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMask(BufferedImage mask) {
/*  74 */     this.mask = mask;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage getMask() {
/*  79 */     return this.mask;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInvert(boolean invert) {
/*  84 */     this.invert = invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getInvert() {
/*  89 */     return this.invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  94 */     int width = src.getWidth();
/*  95 */     int height = src.getHeight();
/*     */     
/*  97 */     if (dst == null)
/*     */     {
/*  99 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 102 */     if (this.mask == null)
/*     */     {
/* 104 */       return dst;
/*     */     }
/*     */     
/* 107 */     int maskWidth = this.mask.getWidth();
/* 108 */     int maskHeight = this.mask.getHeight();
/* 109 */     float d = this.density * (1.0F + this.softness);
/* 110 */     float lower = 255.0F * (d - this.softness);
/* 111 */     float upper = 255.0F * d;
/* 112 */     int[] inPixels = new int[width];
/* 113 */     int[] maskPixels = new int[maskWidth];
/*     */     
/* 115 */     for (int y = 0; y < height; y++) {
/*     */       
/* 117 */       getRGB(src, 0, y, width, 1, inPixels);
/* 118 */       getRGB(this.mask, 0, y % maskHeight, maskWidth, 1, maskPixels);
/*     */       
/* 120 */       for (int x = 0; x < width; x++) {
/*     */         
/* 122 */         int maskRGB = maskPixels[x % maskWidth];
/* 123 */         int inRGB = inPixels[x];
/* 124 */         int v = PixelUtils.brightness(maskRGB);
/* 125 */         float f = ImageMath.smoothStep(lower, upper, v);
/* 126 */         int a = (int)(255.0F * f);
/*     */         
/* 128 */         if (this.invert)
/*     */         {
/* 130 */           a = 255 - a;
/*     */         }
/*     */         
/* 133 */         inPixels[x] = a << 24 | inRGB & 0xFFFFFF;
/*     */       } 
/*     */       
/* 136 */       setRGB(dst, 0, y, width, 1, inPixels);
/*     */     } 
/*     */     
/* 139 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 144 */     return "Transitions/Gradient Wipe...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GradientWipeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */