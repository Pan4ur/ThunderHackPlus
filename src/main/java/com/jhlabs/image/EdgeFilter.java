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
/*     */ public class EdgeFilter
/*     */   extends WholeImageFilter
/*     */ {
/*  27 */   public static final float R2 = (float)Math.sqrt(2.0D);
/*     */   
/*  29 */   public static final float[] ROBERTS_V = new float[] { 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  35 */   public static final float[] ROBERTS_H = new float[] { -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   public static final float[] PREWITT_V = new float[] { -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   public static final float[] PREWITT_H = new float[] { -1.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   public static final float[] SOBEL_V = new float[] { -1.0F, 0.0F, 1.0F, -2.0F, 0.0F, 2.0F, -1.0F, 0.0F, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static float[] SOBEL_H = new float[] { -1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final float[] FREI_CHEN_V = new float[] { -1.0F, 0.0F, 1.0F, -R2, 0.0F, R2, -1.0F, 0.0F, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static float[] FREI_CHEN_H = new float[] { -1.0F, -R2, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, R2, 1.0F };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  78 */   protected float[] vEdgeMatrix = SOBEL_V;
/*  79 */   protected float[] hEdgeMatrix = SOBEL_H;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVEdgeMatrix(float[] vEdgeMatrix) {
/*  87 */     this.vEdgeMatrix = vEdgeMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float[] getVEdgeMatrix() {
/*  92 */     return this.vEdgeMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHEdgeMatrix(float[] hEdgeMatrix) {
/*  97 */     this.hEdgeMatrix = hEdgeMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float[] getHEdgeMatrix() {
/* 102 */     return this.hEdgeMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 107 */     int index = 0;
/* 108 */     int[] outPixels = new int[width * height];
/*     */     
/* 110 */     for (int y = 0; y < height; y++) {
/*     */       
/* 112 */       for (int x = 0; x < width; x++) {
/*     */         
/* 114 */         int r = 0, g = 0, b = 0;
/* 115 */         int rh = 0, gh = 0, bh = 0;
/* 116 */         int rv = 0, gv = 0, bv = 0;
/* 117 */         int a = inPixels[y * width + x] & 0xFF000000;
/*     */         
/* 119 */         for (int row = -1; row <= 1; row++) {
/*     */           
/* 121 */           int ioffset, iy = y + row;
/*     */ 
/*     */           
/* 124 */           if (0 <= iy && iy < height) {
/*     */             
/* 126 */             ioffset = iy * width;
/*     */           }
/*     */           else {
/*     */             
/* 130 */             ioffset = y * width;
/*     */           } 
/*     */           
/* 133 */           int moffset = 3 * (row + 1) + 1;
/*     */           
/* 135 */           for (int col = -1; col <= 1; col++) {
/*     */             
/* 137 */             int ix = x + col;
/*     */             
/* 139 */             if (0 > ix || ix >= width)
/*     */             {
/* 141 */               ix = x;
/*     */             }
/*     */             
/* 144 */             int rgb = inPixels[ioffset + ix];
/* 145 */             float h = this.hEdgeMatrix[moffset + col];
/* 146 */             float v = this.vEdgeMatrix[moffset + col];
/* 147 */             r = (rgb & 0xFF0000) >> 16;
/* 148 */             g = (rgb & 0xFF00) >> 8;
/* 149 */             b = rgb & 0xFF;
/* 150 */             rh += (int)(h * r);
/* 151 */             gh += (int)(h * g);
/* 152 */             bh += (int)(h * b);
/* 153 */             rv += (int)(v * r);
/* 154 */             gv += (int)(v * g);
/* 155 */             bv += (int)(v * b);
/*     */           } 
/*     */         } 
/*     */         
/* 159 */         r = (int)(Math.sqrt((rh * rh + rv * rv)) / 1.8D);
/* 160 */         g = (int)(Math.sqrt((gh * gh + gv * gv)) / 1.8D);
/* 161 */         b = (int)(Math.sqrt((bh * bh + bv * bv)) / 1.8D);
/* 162 */         r = PixelUtils.clamp(r);
/* 163 */         g = PixelUtils.clamp(g);
/* 164 */         b = PixelUtils.clamp(b);
/* 165 */         outPixels[index++] = a | r << 16 | g << 8 | b;
/*     */       } 
/*     */     } 
/*     */     
/* 169 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 174 */     return "Edges/Detect Edges";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\EdgeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */