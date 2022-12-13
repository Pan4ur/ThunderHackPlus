/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.Rectangle;
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
/*    */ public class EqualizeFilter
/*    */   extends WholeImageFilter
/*    */ {
/*    */   private int[][] lut;
/*    */   
/*    */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 35 */     Histogram histogram = new Histogram(inPixels, width, height, 0, width);
/*    */ 
/*    */     
/* 38 */     if (histogram.getNumSamples() > 0) {
/*    */       
/* 40 */       float scale = 255.0F / histogram.getNumSamples();
/* 41 */       this.lut = new int[3][256];
/*    */       
/* 43 */       for (int j = 0; j < 3; j++)
/*    */       {
/* 45 */         this.lut[j][0] = histogram.getFrequency(j, 0);
/*    */         int k;
/* 47 */         for (k = 1; k < 256; k++)
/*    */         {
/* 49 */           this.lut[j][k] = this.lut[j][k - 1] + histogram.getFrequency(j, k);
/*    */         }
/*    */         
/* 52 */         for (k = 0; k < 256; k++)
/*    */         {
/* 54 */           this.lut[j][k] = Math.round(this.lut[j][k] * scale);
/*    */         }
/*    */       }
/*    */     
/*    */     } else {
/*    */       
/* 60 */       this.lut = (int[][])null;
/*    */     } 
/*    */     
/* 63 */     int i = 0;
/*    */     
/* 65 */     for (int y = 0; y < height; y++) {
/* 66 */       for (int x = 0; x < width; x++) {
/*    */         
/* 68 */         inPixels[i] = filterRGB(x, y, inPixels[i]);
/* 69 */         i++;
/*    */       } 
/*    */     } 
/* 72 */     this.lut = (int[][])null;
/* 73 */     return inPixels;
/*    */   }
/*    */ 
/*    */   
/*    */   private int filterRGB(int x, int y, int rgb) {
/* 78 */     if (this.lut != null) {
/*    */       
/* 80 */       int a = rgb & 0xFF000000;
/* 81 */       int r = this.lut[0][rgb >> 16 & 0xFF];
/* 82 */       int g = this.lut[1][rgb >> 8 & 0xFF];
/* 83 */       int b = this.lut[2][rgb & 0xFF];
/* 84 */       return a | r << 16 | g << 8 | b;
/*    */     } 
/*    */     
/* 87 */     return rgb;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 92 */     return "Colors/Equalize";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\EqualizeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */