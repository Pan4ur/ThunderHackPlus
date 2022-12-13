/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
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
/*     */ public class ErodeFilter
/*     */   extends BinaryFilter
/*     */ {
/*  27 */   private int threshold = 2;
/*     */ 
/*     */   
/*     */   public ErodeFilter() {
/*  31 */     this.newColor = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreshold(int threshold) {
/*  41 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreshold() {
/*  51 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  56 */     int[] outPixels = new int[width * height];
/*     */     
/*  58 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/*  60 */       int index = 0;
/*     */       
/*  62 */       if (i > 0) {
/*     */         
/*  64 */         int[] t = inPixels;
/*  65 */         inPixels = outPixels;
/*  66 */         outPixels = t;
/*     */       } 
/*     */       
/*  69 */       for (int y = 0; y < height; y++) {
/*     */         
/*  71 */         for (int x = 0; x < width; x++) {
/*     */           
/*  73 */           int pixel = inPixels[y * width + x];
/*     */           
/*  75 */           if (this.blackFunction.isBlack(pixel)) {
/*     */             
/*  77 */             int neighbours = 0;
/*     */             
/*  79 */             for (int dy = -1; dy <= 1; dy++) {
/*     */               
/*  81 */               int iy = y + dy;
/*     */ 
/*     */               
/*  84 */               if (0 <= iy && iy < height) {
/*     */                 
/*  86 */                 int ioffset = iy * width;
/*     */                 
/*  88 */                 for (int dx = -1; dx <= 1; dx++) {
/*     */                   
/*  90 */                   int ix = x + dx;
/*     */                   
/*  92 */                   if ((dy != 0 || dx != 0) && 0 <= ix && ix < width) {
/*     */                     
/*  94 */                     int rgb = inPixels[ioffset + ix];
/*     */                     
/*  96 */                     if (!this.blackFunction.isBlack(rgb))
/*     */                     {
/*  98 */                       neighbours++;
/*     */                     }
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */             
/* 105 */             if (neighbours >= this.threshold)
/*     */             {
/* 107 */               if (this.colormap != null) {
/*     */                 
/* 109 */                 pixel = this.colormap.getColor(i / this.iterations);
/*     */               }
/*     */               else {
/*     */                 
/* 113 */                 pixel = this.newColor;
/*     */               } 
/*     */             }
/*     */           } 
/*     */           
/* 118 */           outPixels[index++] = pixel;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 123 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return "Binary/Erode...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ErodeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */