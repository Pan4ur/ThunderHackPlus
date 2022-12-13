/*     */ package com.jhlabs.image;
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
/*     */ public class Spectrum
/*     */ {
/*     */   private static int adjust(float color, float factor, float gamma) {
/*  26 */     if (color == 0.0D)
/*     */     {
/*  28 */       return 0;
/*     */     }
/*     */     
/*  31 */     return (int)Math.round(255.0D * Math.pow((color * factor), gamma));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int wavelengthToRGB(float wavelength) {
/*  41 */     float r, g, b, factor, gamma = 0.8F;
/*     */     
/*  43 */     int w = (int)wavelength;
/*     */     
/*  45 */     if (w < 380) {
/*     */       
/*  47 */       r = 0.0F;
/*  48 */       g = 0.0F;
/*  49 */       b = 0.0F;
/*     */     }
/*  51 */     else if (w < 440) {
/*     */       
/*  53 */       r = -(wavelength - 440.0F) / 60.0F;
/*  54 */       g = 0.0F;
/*  55 */       b = 1.0F;
/*     */     }
/*  57 */     else if (w < 490) {
/*     */       
/*  59 */       r = 0.0F;
/*  60 */       g = (wavelength - 440.0F) / 50.0F;
/*  61 */       b = 1.0F;
/*     */     }
/*  63 */     else if (w < 510) {
/*     */       
/*  65 */       r = 0.0F;
/*  66 */       g = 1.0F;
/*  67 */       b = -(wavelength - 510.0F) / 20.0F;
/*     */     }
/*  69 */     else if (w < 580) {
/*     */       
/*  71 */       r = (wavelength - 510.0F) / 70.0F;
/*  72 */       g = 1.0F;
/*  73 */       b = 0.0F;
/*     */     }
/*  75 */     else if (w < 645) {
/*     */       
/*  77 */       r = 1.0F;
/*  78 */       g = -(wavelength - 645.0F) / 65.0F;
/*  79 */       b = 0.0F;
/*     */     }
/*  81 */     else if (w <= 780) {
/*     */       
/*  83 */       r = 1.0F;
/*  84 */       g = 0.0F;
/*  85 */       b = 0.0F;
/*     */     }
/*     */     else {
/*     */       
/*  89 */       r = 0.0F;
/*  90 */       g = 0.0F;
/*  91 */       b = 0.0F;
/*     */     } 
/*     */ 
/*     */     
/*  95 */     if (380 <= w && w <= 419) {
/*     */       
/*  97 */       factor = 0.3F + 0.7F * (wavelength - 380.0F) / 40.0F;
/*     */     }
/*  99 */     else if (420 <= w && w <= 700) {
/*     */       
/* 101 */       factor = 1.0F;
/*     */     }
/* 103 */     else if (701 <= w && w <= 780) {
/*     */       
/* 105 */       factor = 0.3F + 0.7F * (780.0F - wavelength) / 80.0F;
/*     */     }
/*     */     else {
/*     */       
/* 109 */       factor = 0.0F;
/*     */     } 
/*     */     
/* 112 */     int ir = adjust(r, factor, gamma);
/* 113 */     int ig = adjust(g, factor, gamma);
/* 114 */     int ib = adjust(b, factor, gamma);
/* 115 */     return 0xFF000000 | ir << 16 | ig << 8 | ib;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\Spectrum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */