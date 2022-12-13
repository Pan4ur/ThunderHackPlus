/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
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
/*    */ public class HighPassFilter
/*    */   extends GaussianFilter
/*    */ {
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 34 */     int width = src.getWidth();
/* 35 */     int height = src.getHeight();
/*    */     
/* 37 */     if (dst == null)
/*    */     {
/* 39 */       dst = createCompatibleDestImage(src, null);
/*    */     }
/*    */     
/* 42 */     int[] inPixels = new int[width * height];
/* 43 */     int[] outPixels = new int[width * height];
/* 44 */     src.getRGB(0, 0, width, height, inPixels, 0, width);
/*    */     
/* 46 */     if (this.radius > 0.0F) {
/*    */       
/* 48 */       convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, (this.alpha && this.premultiplyAlpha), false, CLAMP_EDGES);
/* 49 */       convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false, (this.alpha && this.premultiplyAlpha), CLAMP_EDGES);
/*    */     } 
/*    */     
/* 52 */     src.getRGB(0, 0, width, height, outPixels, 0, width);
/* 53 */     int index = 0;
/*    */     
/* 55 */     for (int y = 0; y < height; y++) {
/*    */       
/* 57 */       for (int x = 0; x < width; x++) {
/*    */         
/* 59 */         int rgb1 = outPixels[index];
/* 60 */         int r1 = rgb1 >> 16 & 0xFF;
/* 61 */         int g1 = rgb1 >> 8 & 0xFF;
/* 62 */         int b1 = rgb1 & 0xFF;
/* 63 */         int rgb2 = inPixels[index];
/* 64 */         int r2 = rgb2 >> 16 & 0xFF;
/* 65 */         int g2 = rgb2 >> 8 & 0xFF;
/* 66 */         int b2 = rgb2 & 0xFF;
/* 67 */         r1 = (r1 + 255 - r2) / 2;
/* 68 */         g1 = (g1 + 255 - g2) / 2;
/* 69 */         b1 = (b1 + 255 - b2) / 2;
/* 70 */         inPixels[index] = rgb1 & 0xFF000000 | r1 << 16 | g1 << 8 | b1;
/* 71 */         index++;
/*    */       } 
/*    */     } 
/*    */     
/* 75 */     dst.setRGB(0, 0, width, height, inPixels, 0, width);
/* 76 */     return dst;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return "Blur/High Pass...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\HighPassFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */