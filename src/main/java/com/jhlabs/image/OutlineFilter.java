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
/*    */ 
/*    */ public class OutlineFilter
/*    */   extends BinaryFilter
/*    */ {
/*    */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 34 */     int index = 0;
/* 35 */     int[] outPixels = new int[width * height];
/*    */     
/* 37 */     for (int y = 0; y < height; y++) {
/*    */       
/* 39 */       for (int x = 0; x < width; x++) {
/*    */         
/* 41 */         int pixel = inPixels[y * width + x];
/*    */         
/* 43 */         if (this.blackFunction.isBlack(pixel)) {
/*    */           
/* 45 */           int neighbours = 0;
/*    */           
/* 47 */           for (int dy = -1; dy <= 1; dy++) {
/*    */             
/* 49 */             int iy = y + dy;
/*    */ 
/*    */             
/* 52 */             if (0 <= iy && iy < height) {
/*    */               
/* 54 */               int ioffset = iy * width;
/*    */               
/* 56 */               for (int dx = -1; dx <= 1; dx++) {
/*    */                 
/* 58 */                 int ix = x + dx;
/*    */                 
/* 60 */                 if ((dy != 0 || dx != 0) && 0 <= ix && ix < width) {
/*    */                   
/* 62 */                   int rgb = inPixels[ioffset + ix];
/*    */                   
/* 64 */                   if (this.blackFunction.isBlack(rgb))
/*    */                   {
/* 66 */                     neighbours++;
/*    */                   }
/*    */                 }
/*    */                 else {
/*    */                   
/* 71 */                   neighbours++;
/*    */                 } 
/*    */               } 
/*    */             } 
/*    */           } 
/*    */           
/* 77 */           if (neighbours == 9)
/*    */           {
/* 79 */             pixel = this.newColor;
/*    */           }
/*    */         } 
/*    */         
/* 83 */         outPixels[index++] = pixel;
/*    */       } 
/*    */     } 
/*    */     
/* 87 */     return outPixels;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 92 */     return "Binary/Outline...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\OutlineFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */