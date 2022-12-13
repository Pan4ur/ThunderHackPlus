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
/*     */ public class EmbossFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private static final float pixelScale = 255.9F;
/*  29 */   private float azimuth = 2.3561945F; private float elevation = 0.5235988F;
/*     */   private boolean emboss = false;
/*  31 */   private float width45 = 3.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAzimuth(float azimuth) {
/*  39 */     this.azimuth = azimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAzimuth() {
/*  44 */     return this.azimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setElevation(float elevation) {
/*  49 */     this.elevation = elevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getElevation() {
/*  54 */     return this.elevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBumpHeight(float bumpHeight) {
/*  59 */     this.width45 = 3.0F * bumpHeight;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBumpHeight() {
/*  64 */     return this.width45 / 3.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEmboss(boolean emboss) {
/*  69 */     this.emboss = emboss;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getEmboss() {
/*  74 */     return this.emboss;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/*  79 */     int index = 0;
/*  80 */     int[] outPixels = new int[width * height];
/*     */ 
/*     */     
/*  83 */     int bumpMapWidth = width;
/*  84 */     int bumpMapHeight = height;
/*  85 */     int[] bumpPixels = new int[bumpMapWidth * bumpMapHeight];
/*     */     
/*  87 */     for (int i = 0; i < inPixels.length; i++)
/*     */     {
/*  89 */       bumpPixels[i] = PixelUtils.brightness(inPixels[i]);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  94 */     int Lx = (int)(Math.cos(this.azimuth) * Math.cos(this.elevation) * 255.89999389648438D);
/*  95 */     int Ly = (int)(Math.sin(this.azimuth) * Math.cos(this.elevation) * 255.89999389648438D);
/*  96 */     int Lz = (int)(Math.sin(this.elevation) * 255.89999389648438D);
/*  97 */     int Nz = (int)(1530.0F / this.width45);
/*  98 */     int Nz2 = Nz * Nz;
/*  99 */     int NzLz = Nz * Lz;
/* 100 */     int background = Lz;
/* 101 */     int bumpIndex = 0;
/*     */     
/* 103 */     for (int y = 0; y < height; y++, bumpIndex += bumpMapWidth) {
/*     */       
/* 105 */       int s1 = bumpIndex;
/* 106 */       int s2 = s1 + bumpMapWidth;
/* 107 */       int s3 = s2 + bumpMapWidth;
/*     */       
/* 109 */       for (int x = 0; x < width; x++, s1++, s2++, s3++) {
/*     */         int shade;
/* 111 */         if (y != 0 && y < height - 2 && x != 0 && x < width - 2) {
/*     */           
/* 113 */           int Nx = bumpPixels[s1 - 1] + bumpPixels[s2 - 1] + bumpPixels[s3 - 1] - bumpPixels[s1 + 1] - bumpPixels[s2 + 1] - bumpPixels[s3 + 1];
/* 114 */           int Ny = bumpPixels[s3 - 1] + bumpPixels[s3] + bumpPixels[s3 + 1] - bumpPixels[s1 - 1] - bumpPixels[s1] - bumpPixels[s1 + 1];
/*     */           
/* 116 */           if (Nx == 0 && Ny == 0) {
/*     */             
/* 118 */             shade = background;
/*     */           } else {
/* 120 */             int NdotL; if ((NdotL = Nx * Lx + Ny * Ly + NzLz) < 0) {
/*     */               
/* 122 */               shade = 0;
/*     */             }
/*     */             else {
/*     */               
/* 126 */               shade = (int)(NdotL / Math.sqrt((Nx * Nx + Ny * Ny + Nz2)));
/*     */             } 
/*     */           } 
/*     */         } else {
/*     */           
/* 131 */           shade = background;
/*     */         } 
/*     */         
/* 134 */         if (this.emboss) {
/*     */           
/* 136 */           int rgb = inPixels[index];
/* 137 */           int a = rgb & 0xFF000000;
/* 138 */           int r = rgb >> 16 & 0xFF;
/* 139 */           int g = rgb >> 8 & 0xFF;
/* 140 */           int b = rgb & 0xFF;
/* 141 */           r = r * shade >> 8;
/* 142 */           g = g * shade >> 8;
/* 143 */           b = b * shade >> 8;
/* 144 */           outPixels[index++] = a | r << 16 | g << 8 | b;
/*     */         }
/*     */         else {
/*     */           
/* 148 */           outPixels[index++] = 0xFF000000 | shade << 16 | shade << 8 | shade;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 153 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 158 */     return "Stylize/Emboss...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\EmbossFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */