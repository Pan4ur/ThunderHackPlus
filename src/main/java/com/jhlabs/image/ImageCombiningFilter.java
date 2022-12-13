/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.image.ImageProducer;
/*    */ import java.awt.image.MemoryImageSource;
/*    */ import java.awt.image.PixelGrabber;
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
/*    */ public class ImageCombiningFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb1, int rgb2) {
/* 26 */     int a1 = rgb1 >> 24 & 0xFF;
/* 27 */     int r1 = rgb1 >> 16 & 0xFF;
/* 28 */     int g1 = rgb1 >> 8 & 0xFF;
/* 29 */     int b1 = rgb1 & 0xFF;
/* 30 */     int a2 = rgb2 >> 24 & 0xFF;
/* 31 */     int r2 = rgb2 >> 16 & 0xFF;
/* 32 */     int g2 = rgb2 >> 8 & 0xFF;
/* 33 */     int b2 = rgb2 & 0xFF;
/* 34 */     int r = PixelUtils.clamp(r1 + r2);
/* 35 */     int g = PixelUtils.clamp(r1 + r2);
/* 36 */     int b = PixelUtils.clamp(r1 + r2);
/* 37 */     return a1 << 24 | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public ImageProducer filter(Image image1, Image image2, int x, int y, int w, int h) {
/* 42 */     int[] pixels1 = new int[w * h];
/* 43 */     int[] pixels2 = new int[w * h];
/* 44 */     int[] pixels3 = new int[w * h];
/* 45 */     PixelGrabber pg1 = new PixelGrabber(image1, x, y, w, h, pixels1, 0, w);
/* 46 */     PixelGrabber pg2 = new PixelGrabber(image2, x, y, w, h, pixels2, 0, w);
/*    */ 
/*    */     
/*    */     try {
/* 50 */       pg1.grabPixels();
/* 51 */       pg2.grabPixels();
/*    */     }
/* 53 */     catch (InterruptedException e) {
/*    */       
/* 55 */       System.err.println("interrupted waiting for pixels!");
/* 56 */       return null;
/*    */     } 
/*    */     
/* 59 */     if ((pg1.status() & 0x80) != 0) {
/*    */       
/* 61 */       System.err.println("image fetch aborted or errored");
/* 62 */       return null;
/*    */     } 
/*    */     
/* 65 */     if ((pg2.status() & 0x80) != 0) {
/*    */       
/* 67 */       System.err.println("image fetch aborted or errored");
/* 68 */       return null;
/*    */     } 
/*    */     
/* 71 */     for (int j = 0; j < h; j++) {
/*    */       
/* 73 */       for (int i = 0; i < w; i++) {
/*    */         
/* 75 */         int k = j * w + i;
/* 76 */         pixels3[k] = filterRGB(x + i, y + j, pixels1[k], pixels2[k]);
/*    */       } 
/*    */     } 
/*    */     
/* 80 */     return new MemoryImageSource(w, h, pixels3, 0, w);
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ImageCombiningFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */