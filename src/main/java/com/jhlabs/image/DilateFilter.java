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
/*     */ public class DilateFilter
/*     */   extends BinaryFilter
/*     */ {
/*  27 */   private int threshold = 2;
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
/*     */   public void setThreshold(int threshold) {
/*  40 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreshold() {
/*  50 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  55 */     int[] outPixels = new int[width * height];
/*     */     
/*  57 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/*  59 */       int index = 0;
/*     */       
/*  61 */       if (i > 0) {
/*     */         
/*  63 */         int[] t = inPixels;
/*  64 */         inPixels = outPixels;
/*  65 */         outPixels = t;
/*     */       } 
/*     */       
/*  68 */       for (int y = 0; y < height; y++) {
/*     */         
/*  70 */         for (int x = 0; x < width; x++) {
/*     */           
/*  72 */           int pixel = inPixels[y * width + x];
/*     */           
/*  74 */           if (!this.blackFunction.isBlack(pixel)) {
/*     */             
/*  76 */             int neighbours = 0;
/*     */             
/*  78 */             for (int dy = -1; dy <= 1; dy++) {
/*     */               
/*  80 */               int iy = y + dy;
/*     */ 
/*     */               
/*  83 */               if (0 <= iy && iy < height) {
/*     */                 
/*  85 */                 int ioffset = iy * width;
/*     */                 
/*  87 */                 for (int dx = -1; dx <= 1; dx++) {
/*     */                   
/*  89 */                   int ix = x + dx;
/*     */                   
/*  91 */                   if ((dy != 0 || dx != 0) && 0 <= ix && ix < width) {
/*     */                     
/*  93 */                     int rgb = inPixels[ioffset + ix];
/*     */                     
/*  95 */                     if (this.blackFunction.isBlack(rgb))
/*     */                     {
/*  97 */                       neighbours++;
/*     */                     }
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */             
/* 104 */             if (neighbours >= this.threshold)
/*     */             {
/* 106 */               if (this.colormap != null) {
/*     */                 
/* 108 */                 pixel = this.colormap.getColor(i / this.iterations);
/*     */               }
/*     */               else {
/*     */                 
/* 112 */                 pixel = this.newColor;
/*     */               } 
/*     */             }
/*     */           } 
/*     */           
/* 117 */           outPixels[index++] = pixel;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 122 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 127 */     return "Binary/Dilate...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DilateFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */