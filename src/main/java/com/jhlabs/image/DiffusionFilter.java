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
/*     */ public class DiffusionFilter
/*     */   extends WholeImageFilter
/*     */ {
/*  27 */   private static final int[] diffusionMatrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] matrix;
/*     */ 
/*     */ 
/*     */   
/*  35 */   private int sum = 16;
/*     */   private boolean serpentine = true;
/*     */   private boolean colorDither = true;
/*  38 */   private int levels = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DiffusionFilter() {
/*  45 */     setMatrix(diffusionMatrix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSerpentine(boolean serpentine) {
/*  55 */     this.serpentine = serpentine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSerpentine() {
/*  65 */     return this.serpentine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColorDither(boolean colorDither) {
/*  75 */     this.colorDither = colorDither;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getColorDither() {
/*  85 */     return this.colorDither;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMatrix(int[] matrix) {
/*  95 */     this.matrix = matrix;
/*  96 */     this.sum = 0;
/*     */     
/*  98 */     for (int i = 0; i < matrix.length; i++)
/*     */     {
/* 100 */       this.sum += matrix[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getMatrix() {
/* 111 */     return this.matrix;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevels(int levels) {
/* 121 */     this.levels = levels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLevels() {
/* 131 */     return this.levels;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 136 */     int[] outPixels = new int[width * height];
/* 137 */     int index = 0;
/* 138 */     int[] map = new int[this.levels];
/*     */     
/* 140 */     for (int i = 0; i < this.levels; i++) {
/*     */       
/* 142 */       int v = 255 * i / (this.levels - 1);
/* 143 */       map[i] = v;
/*     */     } 
/*     */     
/* 146 */     int[] div = new int[256];
/*     */     
/* 148 */     for (int j = 0; j < 256; j++)
/*     */     {
/* 150 */       div[j] = this.levels * j / 256;
/*     */     }
/*     */     
/* 153 */     for (int y = 0; y < height; y++) {
/*     */       int direction;
/* 155 */       boolean reverse = (this.serpentine && (y & 0x1) == 1);
/*     */ 
/*     */       
/* 158 */       if (reverse) {
/*     */         
/* 160 */         index = y * width + width - 1;
/* 161 */         direction = -1;
/*     */       }
/*     */       else {
/*     */         
/* 165 */         index = y * width;
/* 166 */         direction = 1;
/*     */       } 
/*     */       
/* 169 */       for (int x = 0; x < width; x++) {
/*     */         
/* 171 */         int rgb1 = inPixels[index];
/* 172 */         int r1 = rgb1 >> 16 & 0xFF;
/* 173 */         int g1 = rgb1 >> 8 & 0xFF;
/* 174 */         int b1 = rgb1 & 0xFF;
/*     */         
/* 176 */         if (!this.colorDither)
/*     */         {
/* 178 */           r1 = g1 = b1 = (r1 + g1 + b1) / 3;
/*     */         }
/*     */         
/* 181 */         int r2 = map[div[r1]];
/* 182 */         int g2 = map[div[g1]];
/* 183 */         int b2 = map[div[b1]];
/* 184 */         outPixels[index] = rgb1 & 0xFF000000 | r2 << 16 | g2 << 8 | b2;
/* 185 */         int er = r1 - r2;
/* 186 */         int eg = g1 - g2;
/* 187 */         int eb = b1 - b2;
/*     */         
/* 189 */         for (int k = -1; k <= 1; k++) {
/*     */           
/* 191 */           int iy = k + y;
/*     */           
/* 193 */           if (0 <= iy && iy < height)
/*     */           {
/* 195 */             for (int m = -1; m <= 1; m++) {
/*     */               
/* 197 */               int jx = m + x;
/*     */               
/* 199 */               if (0 <= jx && jx < width) {
/*     */                 int w;
/*     */ 
/*     */                 
/* 203 */                 if (reverse) {
/*     */                   
/* 205 */                   w = this.matrix[(k + 1) * 3 - m + 1];
/*     */                 }
/*     */                 else {
/*     */                   
/* 209 */                   w = this.matrix[(k + 1) * 3 + m + 1];
/*     */                 } 
/*     */                 
/* 212 */                 if (w != 0) {
/*     */                   
/* 214 */                   int n = reverse ? (index - m) : (index + m);
/* 215 */                   rgb1 = inPixels[n];
/* 216 */                   r1 = rgb1 >> 16 & 0xFF;
/* 217 */                   g1 = rgb1 >> 8 & 0xFF;
/* 218 */                   b1 = rgb1 & 0xFF;
/* 219 */                   r1 += er * w / this.sum;
/* 220 */                   g1 += eg * w / this.sum;
/* 221 */                   b1 += eb * w / this.sum;
/* 222 */                   inPixels[n] = inPixels[n] & 0xFF000000 | PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1);
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 229 */         index += direction;
/*     */       } 
/*     */     } 
/*     */     
/* 233 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 238 */     return "Colors/Diffusion Dither...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DiffusionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */