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
/*     */ public class OilFilter
/*     */   extends WholeImageFilter
/*     */ {
/*  27 */   private int range = 3;
/*  28 */   private int levels = 256;
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
/*     */   public void setRange(int range) {
/*  41 */     this.range = range;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRange() {
/*  51 */     return this.range;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevels(int levels) {
/*  61 */     this.levels = levels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLevels() {
/*  71 */     return this.levels;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  76 */     int index = 0;
/*  77 */     int[] rHistogram = new int[this.levels];
/*  78 */     int[] gHistogram = new int[this.levels];
/*  79 */     int[] bHistogram = new int[this.levels];
/*  80 */     int[] rTotal = new int[this.levels];
/*  81 */     int[] gTotal = new int[this.levels];
/*  82 */     int[] bTotal = new int[this.levels];
/*  83 */     int[] outPixels = new int[width * height];
/*     */     
/*  85 */     for (int y = 0; y < height; y++) {
/*     */       
/*  87 */       for (int x = 0; x < width; x++) {
/*     */         
/*  89 */         for (int i = 0; i < this.levels; i++) {
/*     */           
/*  91 */           bTotal[i] = 0; gTotal[i] = 0; rTotal[i] = 0; bHistogram[i] = 0; gHistogram[i] = 0; rHistogram[i] = 0;
/*     */         } 
/*     */         
/*  94 */         for (int row = -this.range; row <= this.range; row++) {
/*     */           
/*  96 */           int iy = y + row;
/*     */ 
/*     */           
/*  99 */           if (0 <= iy && iy < height) {
/*     */             
/* 101 */             int ioffset = iy * width;
/*     */             
/* 103 */             for (int col = -this.range; col <= this.range; col++) {
/*     */               
/* 105 */               int ix = x + col;
/*     */               
/* 107 */               if (0 <= ix && ix < width) {
/*     */                 
/* 109 */                 int rgb = inPixels[ioffset + ix];
/* 110 */                 int k = rgb >> 16 & 0xFF;
/* 111 */                 int m = rgb >> 8 & 0xFF;
/* 112 */                 int n = rgb & 0xFF;
/* 113 */                 int ri = k * this.levels / 256;
/* 114 */                 int gi = m * this.levels / 256;
/* 115 */                 int bi = n * this.levels / 256;
/* 116 */                 rTotal[ri] = rTotal[ri] + k;
/* 117 */                 gTotal[gi] = gTotal[gi] + m;
/* 118 */                 bTotal[bi] = bTotal[bi] + n;
/* 119 */                 rHistogram[ri] = rHistogram[ri] + 1;
/* 120 */                 gHistogram[gi] = gHistogram[gi] + 1;
/* 121 */                 bHistogram[bi] = bHistogram[bi] + 1;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 127 */         int r = 0, g = 0, b = 0;
/*     */         
/* 129 */         for (int j = 1; j < this.levels; j++) {
/*     */           
/* 131 */           if (rHistogram[j] > rHistogram[r])
/*     */           {
/* 133 */             r = j;
/*     */           }
/*     */           
/* 136 */           if (gHistogram[j] > gHistogram[g])
/*     */           {
/* 138 */             g = j;
/*     */           }
/*     */           
/* 141 */           if (bHistogram[j] > bHistogram[b])
/*     */           {
/* 143 */             b = j;
/*     */           }
/*     */         } 
/*     */         
/* 147 */         r = rTotal[r] / rHistogram[r];
/* 148 */         g = gTotal[g] / gHistogram[g];
/* 149 */         b = bTotal[b] / bHistogram[b];
/* 150 */         outPixels[index] = inPixels[index] & 0xFF000000 | r << 16 | g << 8 | b;
/* 151 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 155 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 160 */     return "Stylize/Oil...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\OilFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */