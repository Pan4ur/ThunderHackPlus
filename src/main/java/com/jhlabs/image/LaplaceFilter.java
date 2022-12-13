/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class LaplaceFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private void brightness(int[] row) {
/*  31 */     for (int i = 0; i < row.length; i++) {
/*     */       
/*  33 */       int rgb = row[i];
/*  34 */       int r = rgb >> 16 & 0xFF;
/*  35 */       int g = rgb >> 8 & 0xFF;
/*  36 */       int b = rgb & 0xFF;
/*  37 */       row[i] = (r + g + b) / 3;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  43 */     int width = src.getWidth();
/*  44 */     int height = src.getHeight();
/*     */     
/*  46 */     if (dst == null)
/*     */     {
/*  48 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  51 */     int[] row1 = null;
/*  52 */     int[] row2 = null;
/*  53 */     int[] row3 = null;
/*  54 */     int[] pixels = new int[width];
/*  55 */     row1 = getRGB(src, 0, 0, width, 1, row1);
/*  56 */     row2 = getRGB(src, 0, 0, width, 1, row2);
/*  57 */     brightness(row1);
/*  58 */     brightness(row2);
/*     */     int y;
/*  60 */     for (y = 0; y < height; y++) {
/*     */       
/*  62 */       if (y < height - 1) {
/*     */         
/*  64 */         row3 = getRGB(src, 0, y + 1, width, 1, row3);
/*  65 */         brightness(row3);
/*     */       } 
/*     */       
/*  68 */       pixels[width - 1] = -16777216; pixels[0] = -16777216;
/*     */       
/*  70 */       for (int x = 1; x < width - 1; x++) {
/*     */         
/*  72 */         int l1 = row2[x - 1];
/*  73 */         int l2 = row1[x];
/*  74 */         int l3 = row3[x];
/*  75 */         int l4 = row2[x + 1];
/*  76 */         int l = row2[x];
/*  77 */         int max = Math.max(Math.max(l1, l2), Math.max(l3, l4));
/*  78 */         int min = Math.min(Math.min(l1, l2), Math.min(l3, l4));
/*  79 */         int gradient = (int)(0.5F * Math.max(max - l, l - min));
/*  80 */         int r = (row1[x - 1] + row1[x] + row1[x + 1] + row2[x - 1] - 8 * row2[x] + row2[x + 1] + row3[x - 1] + row3[x] + row3[x + 1] > 0) ? gradient : (128 + gradient);
/*     */ 
/*     */ 
/*     */         
/*  84 */         pixels[x] = r;
/*     */       } 
/*     */       
/*  87 */       setRGB(dst, 0, y, width, 1, pixels);
/*  88 */       int[] t = row1;
/*  89 */       row1 = row2;
/*  90 */       row2 = row3;
/*  91 */       row3 = t;
/*     */     } 
/*     */     
/*  94 */     row1 = getRGB(dst, 0, 0, width, 1, row1);
/*  95 */     row2 = getRGB(dst, 0, 0, width, 1, row2);
/*     */     
/*  97 */     for (y = 0; y < height; y++) {
/*     */       
/*  99 */       if (y < height - 1)
/*     */       {
/* 101 */         row3 = getRGB(dst, 0, y + 1, width, 1, row3);
/*     */       }
/*     */       
/* 104 */       pixels[width - 1] = -16777216; pixels[0] = -16777216;
/*     */       
/* 106 */       for (int x = 1; x < width - 1; x++) {
/*     */         
/* 108 */         int r = row2[x];
/* 109 */         r = (r <= 128 && (row1[x - 1] > 128 || row1[x] > 128 || row1[x + 1] > 128 || row2[x - 1] > 128 || row2[x + 1] > 128 || row3[x - 1] > 128 || row3[x] > 128 || row3[x + 1] > 128)) ? ((r >= 128) ? (r - 128) : r) : 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 119 */         pixels[x] = 0xFF000000 | r << 16 | r << 8 | r;
/*     */       } 
/*     */       
/* 122 */       setRGB(dst, 0, y, width, 1, pixels);
/* 123 */       int[] t = row1;
/* 124 */       row1 = row2;
/* 125 */       row2 = row3;
/* 126 */       row3 = t;
/*     */     } 
/*     */     
/* 129 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 134 */     return "Edges/Laplace...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LaplaceFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */