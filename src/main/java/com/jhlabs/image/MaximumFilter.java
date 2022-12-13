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
/*    */ public class MaximumFilter
/*    */   extends WholeImageFilter
/*    */ {
/*    */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 33 */     int index = 0;
/* 34 */     int[] outPixels = new int[width * height];
/*    */     
/* 36 */     for (int y = 0; y < height; y++) {
/*    */       
/* 38 */       for (int x = 0; x < width; x++) {
/*    */         
/* 40 */         int pixel = -16777216;
/*    */         
/* 42 */         for (int dy = -1; dy <= 1; dy++) {
/*    */           
/* 44 */           int iy = y + dy;
/*    */ 
/*    */           
/* 47 */           if (0 <= iy && iy < height) {
/*    */             
/* 49 */             int ioffset = iy * width;
/*    */             
/* 51 */             for (int dx = -1; dx <= 1; dx++) {
/*    */               
/* 53 */               int ix = x + dx;
/*    */               
/* 55 */               if (0 <= ix && ix < width)
/*    */               {
/* 57 */                 pixel = PixelUtils.combinePixels(pixel, inPixels[ioffset + ix], 3);
/*    */               }
/*    */             } 
/*    */           } 
/*    */         } 
/*    */         
/* 63 */         outPixels[index++] = pixel;
/*    */       } 
/*    */     } 
/*    */     
/* 67 */     return outPixels;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 72 */     return "Blur/Maximum";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MaximumFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */