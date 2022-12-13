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
/*    */ public class Flush3DFilter
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
/* 40 */         int pixel = inPixels[y * width + x];
/*    */         
/* 42 */         if (pixel != -16777216 && y > 0 && x > 0) {
/*    */           
/* 44 */           int count = 0;
/*    */           
/* 46 */           if (inPixels[y * width + x - 1] == -16777216)
/*    */           {
/* 48 */             count++;
/*    */           }
/*    */           
/* 51 */           if (inPixels[(y - 1) * width + x] == -16777216)
/*    */           {
/* 53 */             count++;
/*    */           }
/*    */           
/* 56 */           if (inPixels[(y - 1) * width + x - 1] == -16777216)
/*    */           {
/* 58 */             count++;
/*    */           }
/*    */           
/* 61 */           if (count >= 2)
/*    */           {
/* 63 */             pixel = -1;
/*    */           }
/*    */         } 
/*    */         
/* 67 */         outPixels[index++] = pixel;
/*    */       } 
/*    */     } 
/*    */     
/* 71 */     return outPixels;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "Stylize/Flush 3D...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\Flush3DFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */