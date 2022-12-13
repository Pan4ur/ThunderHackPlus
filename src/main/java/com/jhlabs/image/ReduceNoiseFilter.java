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
/*     */ public class ReduceNoiseFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private int smooth(int[] v) {
/*  34 */     int minindex = 0, maxindex = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
/*     */     
/*  36 */     for (int i = 0; i < 9; i++) {
/*     */       
/*  38 */       if (i != 4) {
/*     */         
/*  40 */         if (v[i] < min) {
/*     */           
/*  42 */           min = v[i];
/*  43 */           minindex = i;
/*     */         } 
/*     */         
/*  46 */         if (v[i] > max) {
/*     */           
/*  48 */           max = v[i];
/*  49 */           maxindex = i;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  54 */     if (v[4] < min)
/*     */     {
/*  56 */       return v[minindex];
/*     */     }
/*     */     
/*  59 */     if (v[4] > max)
/*     */     {
/*  61 */       return v[maxindex];
/*     */     }
/*     */     
/*  64 */     return v[4];
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  69 */     int index = 0;
/*  70 */     int[] r = new int[9];
/*  71 */     int[] g = new int[9];
/*  72 */     int[] b = new int[9];
/*  73 */     int[] outPixels = new int[width * height];
/*     */     
/*  75 */     for (int y = 0; y < height; y++) {
/*     */       
/*  77 */       for (int x = 0; x < width; x++) {
/*     */         
/*  79 */         int k = 0;
/*  80 */         int irgb = inPixels[index];
/*  81 */         int ir = irgb >> 16 & 0xFF;
/*  82 */         int ig = irgb >> 8 & 0xFF;
/*  83 */         int ib = irgb & 0xFF;
/*     */         
/*  85 */         for (int dy = -1; dy <= 1; dy++) {
/*     */           
/*  87 */           int iy = y + dy;
/*     */           
/*  89 */           if (0 <= iy && iy < height) {
/*     */             
/*  91 */             int ioffset = iy * width;
/*     */             
/*  93 */             for (int dx = -1; dx <= 1; dx++)
/*     */             {
/*  95 */               int ix = x + dx;
/*     */               
/*  97 */               if (0 <= ix && ix < width) {
/*     */                 
/*  99 */                 int rgb = inPixels[ioffset + ix];
/* 100 */                 r[k] = rgb >> 16 & 0xFF;
/* 101 */                 g[k] = rgb >> 8 & 0xFF;
/* 102 */                 b[k] = rgb & 0xFF;
/*     */               }
/*     */               else {
/*     */                 
/* 106 */                 r[k] = ir;
/* 107 */                 g[k] = ig;
/* 108 */                 b[k] = ib;
/*     */               } 
/*     */               
/* 111 */               k++;
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 116 */             for (int dx = -1; dx <= 1; dx++) {
/*     */               
/* 118 */               r[k] = ir;
/* 119 */               g[k] = ig;
/* 120 */               b[k] = ib;
/* 121 */               k++;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 126 */         outPixels[index] = inPixels[index] & 0xFF000000 | smooth(r) << 16 | smooth(g) << 8 | smooth(b);
/* 127 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 131 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 136 */     return "Blur/Smooth";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ReduceNoiseFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */