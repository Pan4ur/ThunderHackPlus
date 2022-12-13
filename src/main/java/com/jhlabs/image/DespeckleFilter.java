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
/*     */ public class DespeckleFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private short pepperAndSalt(short c, short v1, short v2) {
/*  33 */     if (c < v1)
/*     */     {
/*  35 */       c = (short)(c + 1);
/*     */     }
/*     */     
/*  38 */     if (c < v2)
/*     */     {
/*  40 */       c = (short)(c + 1);
/*     */     }
/*     */     
/*  43 */     if (c > v1)
/*     */     {
/*  45 */       c = (short)(c - 1);
/*     */     }
/*     */     
/*  48 */     if (c > v2)
/*     */     {
/*  50 */       c = (short)(c - 1);
/*     */     }
/*     */     
/*  53 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  58 */     int index = 0;
/*  59 */     short[][] r = new short[3][width];
/*  60 */     short[][] g = new short[3][width];
/*  61 */     short[][] b = new short[3][width];
/*  62 */     int[] outPixels = new int[width * height];
/*     */     
/*  64 */     for (int x = 0; x < width; x++) {
/*     */       
/*  66 */       int rgb = inPixels[x];
/*  67 */       r[1][x] = (short)(rgb >> 16 & 0xFF);
/*  68 */       g[1][x] = (short)(rgb >> 8 & 0xFF);
/*  69 */       b[1][x] = (short)(rgb & 0xFF);
/*     */     } 
/*     */     
/*  72 */     for (int y = 0; y < height; y++) {
/*     */       
/*  74 */       boolean yIn = (y > 0 && y < height - 1);
/*  75 */       int nextRowIndex = index + width;
/*     */       
/*  77 */       if (y < height - 1)
/*     */       {
/*  79 */         for (int j = 0; j < width; j++) {
/*     */           
/*  81 */           int rgb = inPixels[nextRowIndex++];
/*  82 */           r[2][j] = (short)(rgb >> 16 & 0xFF);
/*  83 */           g[2][j] = (short)(rgb >> 8 & 0xFF);
/*  84 */           b[2][j] = (short)(rgb & 0xFF);
/*     */         } 
/*     */       }
/*     */       
/*  88 */       for (int i = 0; i < width; i++) {
/*     */         
/*  90 */         boolean xIn = (i > 0 && i < width - 1);
/*  91 */         short or = r[1][i];
/*  92 */         short og = g[1][i];
/*  93 */         short ob = b[1][i];
/*  94 */         int w = i - 1;
/*  95 */         int e = i + 1;
/*     */         
/*  97 */         if (yIn) {
/*     */           
/*  99 */           or = pepperAndSalt(or, r[0][i], r[2][i]);
/* 100 */           og = pepperAndSalt(og, g[0][i], g[2][i]);
/* 101 */           ob = pepperAndSalt(ob, b[0][i], b[2][i]);
/*     */         } 
/*     */         
/* 104 */         if (xIn) {
/*     */           
/* 106 */           or = pepperAndSalt(or, r[1][w], r[1][e]);
/* 107 */           og = pepperAndSalt(og, g[1][w], g[1][e]);
/* 108 */           ob = pepperAndSalt(ob, b[1][w], b[1][e]);
/*     */         } 
/*     */         
/* 111 */         if (yIn && xIn) {
/*     */           
/* 113 */           or = pepperAndSalt(or, r[0][w], r[2][e]);
/* 114 */           og = pepperAndSalt(og, g[0][w], g[2][e]);
/* 115 */           ob = pepperAndSalt(ob, b[0][w], b[2][e]);
/* 116 */           or = pepperAndSalt(or, r[2][w], r[0][e]);
/* 117 */           og = pepperAndSalt(og, g[2][w], g[0][e]);
/* 118 */           ob = pepperAndSalt(ob, b[2][w], b[0][e]);
/*     */         } 
/*     */         
/* 121 */         outPixels[index] = inPixels[index] & 0xFF000000 | or << 16 | og << 8 | ob;
/* 122 */         index++;
/*     */       } 
/*     */ 
/*     */       
/* 126 */       short[] t = r[0];
/* 127 */       r[0] = r[1];
/* 128 */       r[1] = r[2];
/* 129 */       r[2] = t;
/* 130 */       t = g[0];
/* 131 */       g[0] = g[1];
/* 132 */       g[1] = g[2];
/* 133 */       g[2] = t;
/* 134 */       t = b[0];
/* 135 */       b[0] = b[1];
/* 136 */       b[1] = b[2];
/* 137 */       b[2] = t;
/*     */     } 
/*     */     
/* 140 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 145 */     return "Blur/Despeckle...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DespeckleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */