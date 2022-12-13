/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Random;
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
/*     */ public class PixelUtils
/*     */ {
/*     */   public static final int REPLACE = 0;
/*     */   public static final int NORMAL = 1;
/*     */   public static final int MIN = 2;
/*     */   public static final int MAX = 3;
/*     */   public static final int ADD = 4;
/*     */   public static final int SUBTRACT = 5;
/*     */   public static final int DIFFERENCE = 6;
/*     */   public static final int MULTIPLY = 7;
/*     */   public static final int HUE = 8;
/*     */   public static final int SATURATION = 9;
/*     */   public static final int VALUE = 10;
/*     */   public static final int COLOR = 11;
/*     */   public static final int SCREEN = 12;
/*     */   public static final int AVERAGE = 13;
/*     */   public static final int OVERLAY = 14;
/*     */   public static final int CLEAR = 15;
/*     */   public static final int EXCHANGE = 16;
/*     */   public static final int DISSOLVE = 17;
/*     */   public static final int DST_IN = 18;
/*     */   public static final int ALPHA = 19;
/*     */   public static final int ALPHA_TO_GRAY = 20;
/*  50 */   private static Random randomGenerator = new Random();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int clamp(int c) {
/*  57 */     if (c < 0)
/*     */     {
/*  59 */       return 0;
/*     */     }
/*     */     
/*  62 */     if (c > 255)
/*     */     {
/*  64 */       return 255;
/*     */     }
/*     */     
/*  67 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int interpolate(int v1, int v2, float f) {
/*  72 */     return clamp((int)(v1 + f * (v2 - v1)));
/*     */   }
/*     */ 
/*     */   
/*     */   public static int brightness(int rgb) {
/*  77 */     int r = rgb >> 16 & 0xFF;
/*  78 */     int g = rgb >> 8 & 0xFF;
/*  79 */     int b = rgb & 0xFF;
/*  80 */     return (r + g + b) / 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
/*  85 */     int r1 = rgb1 >> 16 & 0xFF;
/*  86 */     int g1 = rgb1 >> 8 & 0xFF;
/*  87 */     int b1 = rgb1 & 0xFF;
/*  88 */     int r2 = rgb2 >> 16 & 0xFF;
/*  89 */     int g2 = rgb2 >> 8 & 0xFF;
/*  90 */     int b2 = rgb2 & 0xFF;
/*  91 */     return (Math.abs(r1 - r2) <= tolerance && Math.abs(g1 - g2) <= tolerance && Math.abs(b1 - b2) <= tolerance);
/*     */   }
/*     */   
/*  94 */   private static final float[] hsb1 = new float[3];
/*  95 */   private static final float[] hsb2 = new float[3];
/*     */ 
/*     */ 
/*     */   
/*     */   public static int combinePixels(int rgb1, int rgb2, int op) {
/* 100 */     return combinePixels(rgb1, rgb2, op, 255);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha, int channelMask) {
/* 105 */     return rgb2 & (channelMask ^ 0xFFFFFFFF) | combinePixels(rgb1 & channelMask, rgb2, op, extraAlpha);
/*     */   }
/*     */   
/*     */   public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha) {
/*     */     int m, s, na;
/* 110 */     if (op == 0)
/*     */     {
/* 112 */       return rgb1;
/*     */     }
/*     */     
/* 115 */     int a1 = rgb1 >> 24 & 0xFF;
/* 116 */     int r1 = rgb1 >> 16 & 0xFF;
/* 117 */     int g1 = rgb1 >> 8 & 0xFF;
/* 118 */     int b1 = rgb1 & 0xFF;
/* 119 */     int a2 = rgb2 >> 24 & 0xFF;
/* 120 */     int r2 = rgb2 >> 16 & 0xFF;
/* 121 */     int g2 = rgb2 >> 8 & 0xFF;
/* 122 */     int b2 = rgb2 & 0xFF;
/*     */     
/* 124 */     switch (op) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 2:
/* 130 */         r1 = Math.min(r1, r2);
/* 131 */         g1 = Math.min(g1, g2);
/* 132 */         b1 = Math.min(b1, b2);
/*     */         break;
/*     */       
/*     */       case 3:
/* 136 */         r1 = Math.max(r1, r2);
/* 137 */         g1 = Math.max(g1, g2);
/* 138 */         b1 = Math.max(b1, b2);
/*     */         break;
/*     */       
/*     */       case 4:
/* 142 */         r1 = clamp(r1 + r2);
/* 143 */         g1 = clamp(g1 + g2);
/* 144 */         b1 = clamp(b1 + b2);
/*     */         break;
/*     */       
/*     */       case 5:
/* 148 */         r1 = clamp(r2 - r1);
/* 149 */         g1 = clamp(g2 - g1);
/* 150 */         b1 = clamp(b2 - b1);
/*     */         break;
/*     */       
/*     */       case 6:
/* 154 */         r1 = clamp(Math.abs(r1 - r2));
/* 155 */         g1 = clamp(Math.abs(g1 - g2));
/* 156 */         b1 = clamp(Math.abs(b1 - b2));
/*     */         break;
/*     */       
/*     */       case 7:
/* 160 */         r1 = clamp(r1 * r2 / 255);
/* 161 */         g1 = clamp(g1 * g2 / 255);
/* 162 */         b1 = clamp(b1 * b2 / 255);
/*     */         break;
/*     */       
/*     */       case 17:
/* 166 */         if ((randomGenerator.nextInt() & 0xFF) <= a1) {
/*     */           
/* 168 */           r1 = r2;
/* 169 */           g1 = g2;
/* 170 */           b1 = b2;
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case 13:
/* 176 */         r1 = (r1 + r2) / 2;
/* 177 */         g1 = (g1 + g2) / 2;
/* 178 */         b1 = (b1 + b2) / 2;
/*     */         break;
/*     */       
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/* 185 */         Color.RGBtoHSB(r1, g1, b1, hsb1);
/* 186 */         Color.RGBtoHSB(r2, g2, b2, hsb2);
/*     */         
/* 188 */         switch (op) {
/*     */           
/*     */           case 8:
/* 191 */             hsb2[0] = hsb1[0];
/*     */             break;
/*     */           
/*     */           case 9:
/* 195 */             hsb2[1] = hsb1[1];
/*     */             break;
/*     */           
/*     */           case 10:
/* 199 */             hsb2[2] = hsb1[2];
/*     */             break;
/*     */           
/*     */           case 11:
/* 203 */             hsb2[0] = hsb1[0];
/* 204 */             hsb2[1] = hsb1[1];
/*     */             break;
/*     */         } 
/*     */         
/* 208 */         rgb1 = Color.HSBtoRGB(hsb2[0], hsb2[1], hsb2[2]);
/* 209 */         r1 = rgb1 >> 16 & 0xFF;
/* 210 */         g1 = rgb1 >> 8 & 0xFF;
/* 211 */         b1 = rgb1 & 0xFF;
/*     */         break;
/*     */       
/*     */       case 12:
/* 215 */         r1 = 255 - (255 - r1) * (255 - r2) / 255;
/* 216 */         g1 = 255 - (255 - g1) * (255 - g2) / 255;
/* 217 */         b1 = 255 - (255 - b1) * (255 - b2) / 255;
/*     */         break;
/*     */ 
/*     */       
/*     */       case 14:
/* 222 */         s = 255 - (255 - r1) * (255 - r2) / 255;
/* 223 */         m = r1 * r2 / 255;
/* 224 */         r1 = (s * r1 + m * (255 - r1)) / 255;
/* 225 */         s = 255 - (255 - g1) * (255 - g2) / 255;
/* 226 */         m = g1 * g2 / 255;
/* 227 */         g1 = (s * g1 + m * (255 - g1)) / 255;
/* 228 */         s = 255 - (255 - b1) * (255 - b2) / 255;
/* 229 */         m = b1 * b2 / 255;
/* 230 */         b1 = (s * b1 + m * (255 - b1)) / 255;
/*     */         break;
/*     */       
/*     */       case 15:
/* 234 */         r1 = g1 = b1 = 255;
/*     */         break;
/*     */       
/*     */       case 18:
/* 238 */         r1 = clamp(r2 * a1 / 255);
/* 239 */         g1 = clamp(g2 * a1 / 255);
/* 240 */         b1 = clamp(b2 * a1 / 255);
/* 241 */         a1 = clamp(a2 * a1 / 255);
/* 242 */         return a1 << 24 | r1 << 16 | g1 << 8 | b1;
/*     */       
/*     */       case 19:
/* 245 */         a1 = a1 * a2 / 255;
/* 246 */         return a1 << 24 | r2 << 16 | g2 << 8 | b2;
/*     */       
/*     */       case 20:
/* 249 */         na = 255 - a1;
/* 250 */         return a1 << 24 | na << 16 | na << 8 | na;
/*     */     } 
/*     */     
/* 253 */     if (extraAlpha != 255 || a1 != 255) {
/*     */       
/* 255 */       a1 = a1 * extraAlpha / 255;
/* 256 */       int a3 = (255 - a1) * a2 / 255;
/* 257 */       r1 = clamp((r1 * a1 + r2 * a3) / 255);
/* 258 */       g1 = clamp((g1 * a1 + g2 * a3) / 255);
/* 259 */       b1 = clamp((b1 * a1 + b2 * a3) / 255);
/* 260 */       a1 = clamp(a1 + a3);
/*     */     } 
/*     */     
/* 263 */     return a1 << 24 | r1 << 16 | g1 << 8 | b1;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PixelUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */