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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QuantizeFilter
/*     */   extends WholeImageFilter
/*     */ {
/*  37 */   protected static final int[] matrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   private int sum = 16;
/*     */   
/*     */   private boolean dither;
/*  46 */   private int numColors = 256;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean serpentine = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumColors(int numColors) {
/*  55 */     this.numColors = Math.min(Math.max(numColors, 8), 256);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumColors() {
/*  64 */     return this.numColors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDither(boolean dither) {
/*  73 */     this.dither = dither;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getDither() {
/*  82 */     return this.dither;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerpentine(boolean serpentine) {
/*  91 */     this.serpentine = serpentine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSerpentine() {
/* 100 */     return this.serpentine;
/*     */   }
/*     */ 
/*     */   
/*     */   public void quantize(int[] inPixels, int[] outPixels, int width, int height, int numColors, boolean dither, boolean serpentine) {
/* 105 */     int count = width * height;
/* 106 */     Quantizer quantizer = new OctTreeQuantizer();
/* 107 */     quantizer.setup(numColors);
/* 108 */     quantizer.addPixels(inPixels, 0, count);
/* 109 */     int[] table = quantizer.buildColorTable();
/*     */     
/* 111 */     if (!dither) {
/*     */       
/* 113 */       for (int i = 0; i < count; i++)
/*     */       {
/* 115 */         outPixels[i] = table[quantizer.getIndexForColor(inPixels[i])];
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 120 */       int index = 0;
/*     */       
/* 122 */       for (int y = 0; y < height; y++) {
/*     */         int direction;
/* 124 */         boolean reverse = (serpentine && (y & 0x1) == 1);
/*     */ 
/*     */         
/* 127 */         if (reverse) {
/*     */           
/* 129 */           index = y * width + width - 1;
/* 130 */           direction = -1;
/*     */         }
/*     */         else {
/*     */           
/* 134 */           index = y * width;
/* 135 */           direction = 1;
/*     */         } 
/*     */         
/* 138 */         for (int x = 0; x < width; x++) {
/*     */           
/* 140 */           int rgb1 = inPixels[index];
/* 141 */           int rgb2 = table[quantizer.getIndexForColor(rgb1)];
/* 142 */           outPixels[index] = rgb2;
/* 143 */           int r1 = rgb1 >> 16 & 0xFF;
/* 144 */           int g1 = rgb1 >> 8 & 0xFF;
/* 145 */           int b1 = rgb1 & 0xFF;
/* 146 */           int r2 = rgb2 >> 16 & 0xFF;
/* 147 */           int g2 = rgb2 >> 8 & 0xFF;
/* 148 */           int b2 = rgb2 & 0xFF;
/* 149 */           int er = r1 - r2;
/* 150 */           int eg = g1 - g2;
/* 151 */           int eb = b1 - b2;
/*     */           
/* 153 */           for (int i = -1; i <= 1; i++) {
/*     */             
/* 155 */             int iy = i + y;
/*     */             
/* 157 */             if (0 <= iy && iy < height)
/*     */             {
/* 159 */               for (int j = -1; j <= 1; j++) {
/*     */                 
/* 161 */                 int jx = j + x;
/*     */                 
/* 163 */                 if (0 <= jx && jx < width) {
/*     */                   int w;
/*     */ 
/*     */                   
/* 167 */                   if (reverse) {
/*     */                     
/* 169 */                     w = matrix[(i + 1) * 3 - j + 1];
/*     */                   }
/*     */                   else {
/*     */                     
/* 173 */                     w = matrix[(i + 1) * 3 + j + 1];
/*     */                   } 
/*     */                   
/* 176 */                   if (w != 0) {
/*     */                     
/* 178 */                     int k = reverse ? (index - j) : (index + j);
/* 179 */                     rgb1 = inPixels[k];
/* 180 */                     r1 = rgb1 >> 16 & 0xFF;
/* 181 */                     g1 = rgb1 >> 8 & 0xFF;
/* 182 */                     b1 = rgb1 & 0xFF;
/* 183 */                     r1 += er * w / this.sum;
/* 184 */                     g1 += eg * w / this.sum;
/* 185 */                     b1 += eb * w / this.sum;
/* 186 */                     inPixels[k] = PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1);
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             }
/*     */           } 
/*     */           
/* 193 */           index += direction;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 201 */     int[] outPixels = new int[width * height];
/* 202 */     quantize(inPixels, outPixels, width, height, this.numColors, this.dither, this.serpentine);
/* 203 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 208 */     return "Colors/Quantize...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\QuantizeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */