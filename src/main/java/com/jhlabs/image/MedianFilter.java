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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MedianFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private int median(int[] array) {
/*     */     int i;
/*  35 */     for (i = 0; i < 4; i++) {
/*     */       
/*  37 */       int k = 0;
/*  38 */       int maxIndex = 0;
/*     */       
/*  40 */       for (int j = 0; j < 9; j++) {
/*     */         
/*  42 */         if (array[j] > k) {
/*     */           
/*  44 */           k = array[j];
/*  45 */           maxIndex = j;
/*     */         } 
/*     */       } 
/*     */       
/*  49 */       array[maxIndex] = 0;
/*     */     } 
/*     */     
/*  52 */     int max = 0;
/*     */     
/*  54 */     for (i = 0; i < 9; i++) {
/*     */       
/*  56 */       if (array[i] > max)
/*     */       {
/*  58 */         max = array[i];
/*     */       }
/*     */     } 
/*     */     
/*  62 */     return max;
/*     */   }
/*     */ 
/*     */   
/*     */   private int rgbMedian(int[] r, int[] g, int[] b) {
/*  67 */     int index = 0, min = Integer.MAX_VALUE;
/*     */     
/*  69 */     for (int i = 0; i < 9; i++) {
/*     */       
/*  71 */       int sum = 0;
/*     */       
/*  73 */       for (int j = 0; j < 9; j++) {
/*     */         
/*  75 */         sum += Math.abs(r[i] - r[j]);
/*  76 */         sum += Math.abs(g[i] - g[j]);
/*  77 */         sum += Math.abs(b[i] - b[j]);
/*     */       } 
/*     */       
/*  80 */       if (sum < min) {
/*     */         
/*  82 */         min = sum;
/*  83 */         index = i;
/*     */       } 
/*     */     } 
/*     */     
/*  87 */     return index;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  92 */     int index = 0;
/*  93 */     int[] argb = new int[9];
/*  94 */     int[] r = new int[9];
/*  95 */     int[] g = new int[9];
/*  96 */     int[] b = new int[9];
/*  97 */     int[] outPixels = new int[width * height];
/*     */     
/*  99 */     for (int y = 0; y < height; y++) {
/*     */       
/* 101 */       for (int x = 0; x < width; x++) {
/*     */         
/* 103 */         int k = 0;
/*     */         
/* 105 */         for (int dy = -1; dy <= 1; dy++) {
/*     */           
/* 107 */           int iy = y + dy;
/*     */           
/* 109 */           if (0 <= iy && iy < height) {
/*     */             
/* 111 */             int ioffset = iy * width;
/*     */             
/* 113 */             for (int dx = -1; dx <= 1; dx++) {
/*     */               
/* 115 */               int ix = x + dx;
/*     */               
/* 117 */               if (0 <= ix && ix < width) {
/*     */                 
/* 119 */                 int rgb = inPixels[ioffset + ix];
/* 120 */                 argb[k] = rgb;
/* 121 */                 r[k] = rgb >> 16 & 0xFF;
/* 122 */                 g[k] = rgb >> 8 & 0xFF;
/* 123 */                 b[k] = rgb & 0xFF;
/* 124 */                 k++;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 130 */         while (k < 9) {
/*     */           
/* 132 */           argb[k] = -16777216;
/* 133 */           b[k] = 0; g[k] = 0; r[k] = 0;
/* 134 */           k++;
/*     */         } 
/*     */         
/* 137 */         outPixels[index++] = argb[rgbMedian(r, g, b)];
/*     */       } 
/*     */     } 
/*     */     
/* 141 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 146 */     return "Blur/Median";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MedianFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */