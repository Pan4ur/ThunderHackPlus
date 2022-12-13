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
/*     */ public class SkeletonFilter
/*     */   extends BinaryFilter
/*     */ {
/*  29 */   private static final byte[] skeletonTable = new byte[] { 0, 0, 0, 1, 0, 0, 1, 3, 0, 0, 3, 1, 1, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 3, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 3, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 1, 3, 1, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 1, 3, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0 };
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
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  56 */     int[] outPixels = new int[width * height];
/*  57 */     int count = 0;
/*  58 */     int black = -16777216;
/*  59 */     int white = -1;
/*     */     
/*  61 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/*  63 */       count = 0;
/*     */       
/*  65 */       for (int pass = 0; pass < 2; pass++) {
/*     */         
/*  67 */         for (int y = 1; y < height - 1; y++) {
/*     */           
/*  69 */           int offset = y * width + 1;
/*     */           
/*  71 */           for (int x = 1; x < width - 1; x++) {
/*     */             
/*  73 */             int pixel = inPixels[offset];
/*     */             
/*  75 */             if (pixel == black) {
/*     */               
/*  77 */               int tableIndex = 0;
/*     */               
/*  79 */               if (inPixels[offset - width - 1] == black)
/*     */               {
/*  81 */                 tableIndex |= 0x1;
/*     */               }
/*     */               
/*  84 */               if (inPixels[offset - width] == black)
/*     */               {
/*  86 */                 tableIndex |= 0x2;
/*     */               }
/*     */               
/*  89 */               if (inPixels[offset - width + 1] == black)
/*     */               {
/*  91 */                 tableIndex |= 0x4;
/*     */               }
/*     */               
/*  94 */               if (inPixels[offset + 1] == black)
/*     */               {
/*  96 */                 tableIndex |= 0x8;
/*     */               }
/*     */               
/*  99 */               if (inPixels[offset + width + 1] == black)
/*     */               {
/* 101 */                 tableIndex |= 0x10;
/*     */               }
/*     */               
/* 104 */               if (inPixels[offset + width] == black)
/*     */               {
/* 106 */                 tableIndex |= 0x20;
/*     */               }
/*     */               
/* 109 */               if (inPixels[offset + width - 1] == black)
/*     */               {
/* 111 */                 tableIndex |= 0x40;
/*     */               }
/*     */               
/* 114 */               if (inPixels[offset - 1] == black)
/*     */               {
/* 116 */                 tableIndex |= 0x80;
/*     */               }
/*     */               
/* 119 */               int code = skeletonTable[tableIndex];
/*     */               
/* 121 */               if (pass == 1) {
/*     */                 
/* 123 */                 if (code == 2 || code == 3)
/*     */                 {
/* 125 */                   if (this.colormap != null) {
/*     */                     
/* 127 */                     pixel = this.colormap.getColor(i / this.iterations);
/*     */                   }
/*     */                   else {
/*     */                     
/* 131 */                     pixel = this.newColor;
/*     */                   } 
/*     */                   
/* 134 */                   count++;
/*     */                 
/*     */                 }
/*     */               
/*     */               }
/* 139 */               else if (code == 1 || code == 3) {
/*     */                 
/* 141 */                 if (this.colormap != null) {
/*     */                   
/* 143 */                   pixel = this.colormap.getColor(i / this.iterations);
/*     */                 }
/*     */                 else {
/*     */                   
/* 147 */                   pixel = this.newColor;
/*     */                 } 
/*     */                 
/* 150 */                 count++;
/*     */               } 
/*     */             } 
/*     */ 
/*     */             
/* 155 */             outPixels[offset++] = pixel;
/*     */           } 
/*     */         } 
/*     */         
/* 159 */         if (pass == 0) {
/*     */           
/* 161 */           inPixels = outPixels;
/* 162 */           outPixels = new int[width * height];
/*     */         } 
/*     */       } 
/*     */       
/* 166 */       if (count == 0) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 172 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 177 */     return "Binary/Skeletonize...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SkeletonFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */