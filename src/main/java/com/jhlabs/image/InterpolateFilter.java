/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class InterpolateFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private BufferedImage destination;
/*     */   private float interpolation;
/*     */   
/*     */   public void setDestination(BufferedImage destination) {
/*  43 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getDestination() {
/*  53 */     return this.destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterpolation(float interpolation) {
/*  63 */     this.interpolation = interpolation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getInterpolation() {
/*  73 */     return this.interpolation;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  78 */     int width = src.getWidth();
/*  79 */     int height = src.getHeight();
/*  80 */     int type = src.getType();
/*  81 */     WritableRaster srcRaster = src.getRaster();
/*     */     
/*  83 */     if (dst == null)
/*     */     {
/*  85 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  88 */     WritableRaster dstRaster = dst.getRaster();
/*     */     
/*  90 */     if (this.destination != null) {
/*     */       
/*  92 */       width = Math.min(width, this.destination.getWidth());
/*  93 */       height = Math.min(height, this.destination.getWidth());
/*  94 */       int[] pixels1 = null;
/*  95 */       int[] pixels2 = null;
/*     */       
/*  97 */       for (int y = 0; y < height; y++) {
/*     */         
/*  99 */         pixels1 = getRGB(src, 0, y, width, 1, pixels1);
/* 100 */         pixels2 = getRGB(this.destination, 0, y, width, 1, pixels2);
/*     */         
/* 102 */         for (int x = 0; x < width; x++) {
/*     */           
/* 104 */           int rgb1 = pixels1[x];
/* 105 */           int rgb2 = pixels2[x];
/* 106 */           int a1 = rgb1 >> 24 & 0xFF;
/* 107 */           int r1 = rgb1 >> 16 & 0xFF;
/* 108 */           int g1 = rgb1 >> 8 & 0xFF;
/* 109 */           int b1 = rgb1 & 0xFF;
/* 110 */           int a2 = rgb2 >> 24 & 0xFF;
/* 111 */           int r2 = rgb2 >> 16 & 0xFF;
/* 112 */           int g2 = rgb2 >> 8 & 0xFF;
/* 113 */           int b2 = rgb2 & 0xFF;
/* 114 */           r1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, r1, r2));
/* 115 */           g1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, g1, g2));
/* 116 */           b1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, b1, b2));
/* 117 */           pixels1[x] = a1 << 24 | r1 << 16 | g1 << 8 | b1;
/*     */         } 
/*     */         
/* 120 */         setRGB(dst, 0, y, width, 1, pixels1);
/*     */       } 
/*     */     } 
/*     */     
/* 124 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 129 */     return "Effects/Interpolate...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\InterpolateFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */