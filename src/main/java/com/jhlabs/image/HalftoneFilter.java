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
/*     */ public class HalftoneFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  27 */   private float softness = 0.1F;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean invert;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean monochrome;
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage mask;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSoftness(float softness) {
/*  45 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/*  55 */     return this.softness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMask(BufferedImage mask) {
/*  65 */     this.mask = mask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getMask() {
/*  75 */     return this.mask;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInvert(boolean invert) {
/*  80 */     this.invert = invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getInvert() {
/*  85 */     return this.invert;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMonochrome(boolean monochrome) {
/*  95 */     this.monochrome = monochrome;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getMonochrome() {
/* 105 */     return this.monochrome;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 110 */     int width = src.getWidth();
/* 111 */     int height = src.getHeight();
/*     */     
/* 113 */     if (dst == null)
/*     */     {
/* 115 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 118 */     if (this.mask == null)
/*     */     {
/* 120 */       return dst;
/*     */     }
/*     */     
/* 123 */     int maskWidth = this.mask.getWidth();
/* 124 */     int maskHeight = this.mask.getHeight();
/* 125 */     float s = 255.0F * this.softness;
/* 126 */     int[] inPixels = new int[width];
/* 127 */     int[] maskPixels = new int[maskWidth];
/*     */     
/* 129 */     for (int y = 0; y < height; y++) {
/*     */       
/* 131 */       getRGB(src, 0, y, width, 1, inPixels);
/* 132 */       getRGB(this.mask, 0, y % maskHeight, maskWidth, 1, maskPixels);
/*     */       
/* 134 */       for (int x = 0; x < width; x++) {
/*     */         
/* 136 */         int maskRGB = maskPixels[x % maskWidth];
/* 137 */         int inRGB = inPixels[x];
/*     */         
/* 139 */         if (this.invert)
/*     */         {
/* 141 */           maskRGB ^= 0xFFFFFF;
/*     */         }
/*     */         
/* 144 */         if (this.monochrome) {
/*     */           
/* 146 */           int v = PixelUtils.brightness(maskRGB);
/* 147 */           int iv = PixelUtils.brightness(inRGB);
/* 148 */           float f = 1.0F - ImageMath.smoothStep(iv - s, iv + s, v);
/* 149 */           int a = (int)(255.0F * f);
/* 150 */           inPixels[x] = inRGB & 0xFF000000 | a << 16 | a << 8 | a;
/*     */         }
/*     */         else {
/*     */           
/* 154 */           int ir = inRGB >> 16 & 0xFF;
/* 155 */           int ig = inRGB >> 8 & 0xFF;
/* 156 */           int ib = inRGB & 0xFF;
/* 157 */           int mr = maskRGB >> 16 & 0xFF;
/* 158 */           int mg = maskRGB >> 8 & 0xFF;
/* 159 */           int mb = maskRGB & 0xFF;
/* 160 */           int r = (int)(255.0F * (1.0F - ImageMath.smoothStep(ir - s, ir + s, mr)));
/* 161 */           int g = (int)(255.0F * (1.0F - ImageMath.smoothStep(ig - s, ig + s, mg)));
/* 162 */           int b = (int)(255.0F * (1.0F - ImageMath.smoothStep(ib - s, ib + s, mb)));
/* 163 */           inPixels[x] = inRGB & 0xFF000000 | r << 16 | g << 8 | b;
/*     */         } 
/*     */       } 
/*     */       
/* 167 */       setRGB(dst, 0, y, width, 1, inPixels);
/*     */     } 
/*     */     
/* 170 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 175 */     return "Stylize/Halftone...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\HalftoneFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */