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
/*    */ public class LifeFilter
/*    */   extends BinaryFilter
/*    */ {
/*    */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 33 */     int index = 0;
/* 34 */     int[] outPixels = new int[width * height];
/*    */     
/* 36 */     for (int y = 0; y < height; y++) {
/*    */       
/* 38 */       for (int x = 0; x < width; x++) {
/*    */         
/* 40 */         int r = 0, g = 0, b = 0;
/* 41 */         int pixel = inPixels[y * width + x];
/* 42 */         int a = pixel & 0xFF000000;
/* 43 */         int neighbours = 0;
/*    */         
/* 45 */         for (int row = -1; row <= 1; row++) {
/*    */           
/* 47 */           int iy = y + row;
/*    */ 
/*    */           
/* 50 */           if (0 <= iy && iy < height) {
/*    */             
/* 52 */             int ioffset = iy * width;
/*    */             
/* 54 */             for (int col = -1; col <= 1; col++) {
/*    */               
/* 56 */               int ix = x + col;
/*    */               
/* 58 */               if ((row != 0 || col != 0) && 0 <= ix && ix < width) {
/*    */                 
/* 60 */                 int rgb = inPixels[ioffset + ix];
/*    */                 
/* 62 */                 if (this.blackFunction.isBlack(rgb))
/*    */                 {
/* 64 */                   neighbours++;
/*    */                 }
/*    */               } 
/*    */             } 
/*    */           } 
/*    */         } 
/*    */         
/* 71 */         if (this.blackFunction.isBlack(pixel)) {
/*    */           
/* 73 */           outPixels[index++] = (neighbours == 2 || neighbours == 3) ? pixel : -1;
/*    */         }
/*    */         else {
/*    */           
/* 77 */           outPixels[index++] = (neighbours == 3) ? -16777216 : pixel;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 82 */     return outPixels;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 87 */     return "Binary/Life";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LifeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */