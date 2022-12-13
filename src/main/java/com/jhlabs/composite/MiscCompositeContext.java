/*     */ package com.jhlabs.composite;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.CompositeContext;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.Raster;
/*     */ import java.awt.image.WritableRaster;
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
/*     */ public class MiscCompositeContext
/*     */   implements CompositeContext
/*     */ {
/*     */   private int rule;
/*     */   private float alpha;
/*     */   private ColorModel srcColorModel;
/*     */   private ColorModel dstColorModel;
/*     */   private ColorSpace srcColorSpace;
/*     */   private ColorSpace dstColorSpace;
/*     */   private boolean srcNeedsConverting;
/*     */   private boolean dstNeedsConverting;
/*     */   
/*     */   public MiscCompositeContext(int rule, float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
/*  42 */     this.rule = rule;
/*  43 */     this.alpha = alpha;
/*  44 */     this.srcColorModel = srcColorModel;
/*  45 */     this.dstColorModel = dstColorModel;
/*  46 */     this.srcColorSpace = srcColorModel.getColorSpace();
/*  47 */     this.dstColorSpace = dstColorModel.getColorSpace();
/*  48 */     ColorModel srgbCM = ColorModel.getRGBdefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int multiply255(int a, int b) {
/*  60 */     int t = a * b + 128;
/*  61 */     return (t >> 8) + t >> 8;
/*     */   }
/*     */ 
/*     */   
/*     */   static int clamp(int a) {
/*  66 */     return (a < 0) ? 0 : ((a > 255) ? 255 : a);
/*     */   }
/*     */ 
/*     */   
/*     */   public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
/*  71 */     float a = 0.0F, ac = 0.0F;
/*  72 */     float alpha = this.alpha;
/*     */     
/*  74 */     float[] sHsv = null, diHsv = null, doHsv = null;
/*     */     
/*  76 */     switch (this.rule) {
/*     */       
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/*     */       case 15:
/*  82 */         sHsv = new float[3];
/*  83 */         diHsv = new float[3];
/*  84 */         doHsv = new float[3];
/*     */         break;
/*     */     } 
/*     */     
/*  88 */     int[] srcPix = null;
/*  89 */     int[] dstPix = null;
/*  90 */     int x = dstOut.getMinX();
/*  91 */     int w = dstOut.getWidth();
/*  92 */     int y0 = dstOut.getMinY();
/*  93 */     int y1 = y0 + dstOut.getHeight();
/*     */     
/*  95 */     for (int y = y0; y < y1; y++) {
/*     */       
/*  97 */       srcPix = src.getPixels(x, y, w, 1, srcPix);
/*  98 */       dstPix = dstIn.getPixels(x, y, w, 1, dstPix);
/*  99 */       int i = 0;
/* 100 */       int end = w * 4;
/*     */       
/* 102 */       while (i < end) {
/*     */         
/* 104 */         int t, dor, dog, dob, doRGB, d, sr = srcPix[i];
/* 105 */         int dir = dstPix[i];
/* 106 */         int sg = srcPix[i + 1];
/* 107 */         int dig = dstPix[i + 1];
/* 108 */         int sb = srcPix[i + 2];
/* 109 */         int dib = dstPix[i + 2];
/* 110 */         int sa = srcPix[i + 3];
/* 111 */         int dia = dstPix[i + 3];
/*     */ 
/*     */         
/* 114 */         switch (this.rule) {
/*     */ 
/*     */           
/*     */           default:
/* 118 */             dor = dir + sr;
/*     */             
/* 120 */             if (dor > 255)
/*     */             {
/* 122 */               dor = 255;
/*     */             }
/*     */             
/* 125 */             dog = dig + sg;
/*     */             
/* 127 */             if (dog > 255)
/*     */             {
/* 129 */               dog = 255;
/*     */             }
/*     */             
/* 132 */             dob = dib + sb;
/*     */             
/* 134 */             if (dob > 255)
/*     */             {
/* 136 */               dob = 255;
/*     */             }
/*     */             break;
/*     */ 
/*     */           
/*     */           case 2:
/* 142 */             dor = dir - sr;
/*     */             
/* 144 */             if (dor < 0)
/*     */             {
/* 146 */               dor = 0;
/*     */             }
/*     */             
/* 149 */             dog = dig - sg;
/*     */             
/* 151 */             if (dog < 0)
/*     */             {
/* 153 */               dog = 0;
/*     */             }
/*     */             
/* 156 */             dob = dib - sb;
/*     */             
/* 158 */             if (dob < 0)
/*     */             {
/* 160 */               dob = 0;
/*     */             }
/*     */             break;
/*     */ 
/*     */           
/*     */           case 3:
/* 166 */             dor = dir - sr;
/*     */             
/* 168 */             if (dor < 0)
/*     */             {
/* 170 */               dor = -dor;
/*     */             }
/*     */             
/* 173 */             dog = dig - sg;
/*     */             
/* 175 */             if (dog < 0)
/*     */             {
/* 177 */               dog = -dog;
/*     */             }
/*     */             
/* 180 */             dob = dib - sb;
/*     */             
/* 182 */             if (dob < 0)
/*     */             {
/* 184 */               dob = -dob;
/*     */             }
/*     */             break;
/*     */ 
/*     */           
/*     */           case 4:
/* 190 */             t = dir * sr + 128;
/* 191 */             dor = (t >> 8) + t >> 8;
/* 192 */             t = dig * sg + 128;
/* 193 */             dog = (t >> 8) + t >> 8;
/* 194 */             t = dib * sb + 128;
/* 195 */             dob = (t >> 8) + t >> 8;
/*     */             break;
/*     */           
/*     */           case 8:
/* 199 */             t = (255 - dir) * (255 - sr) + 128;
/* 200 */             dor = 255 - ((t >> 8) + t >> 8);
/* 201 */             t = (255 - dig) * (255 - sg) + 128;
/* 202 */             dog = 255 - ((t >> 8) + t >> 8);
/* 203 */             t = (255 - dib) * (255 - sb) + 128;
/* 204 */             dob = 255 - ((t >> 8) + t >> 8);
/*     */             break;
/*     */           
/*     */           case 16:
/* 208 */             if (dir < 128) {
/*     */               
/* 210 */               t = dir * sr + 128;
/* 211 */               dor = 2 * ((t >> 8) + t >> 8);
/*     */             }
/*     */             else {
/*     */               
/* 215 */               t = (255 - dir) * (255 - sr) + 128;
/* 216 */               dor = 2 * (255 - ((t >> 8) + t >> 8));
/*     */             } 
/*     */             
/* 219 */             if (dig < 128) {
/*     */               
/* 221 */               t = dig * sg + 128;
/* 222 */               dog = 2 * ((t >> 8) + t >> 8);
/*     */             }
/*     */             else {
/*     */               
/* 226 */               t = (255 - dig) * (255 - sg) + 128;
/* 227 */               dog = 2 * (255 - ((t >> 8) + t >> 8));
/*     */             } 
/*     */             
/* 230 */             if (dib < 128) {
/*     */               
/* 232 */               t = dib * sb + 128;
/* 233 */               dob = 2 * ((t >> 8) + t >> 8);
/*     */               
/*     */               break;
/*     */             } 
/* 237 */             t = (255 - dib) * (255 - sb) + 128;
/* 238 */             dob = 2 * (255 - ((t >> 8) + t >> 8));
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 5:
/* 244 */             dor = (dir < sr) ? dir : sr;
/* 245 */             dog = (dig < sg) ? dig : sg;
/* 246 */             dob = (dib < sb) ? dib : sb;
/*     */             break;
/*     */           
/*     */           case 9:
/* 250 */             dor = (dir > sr) ? dir : sr;
/* 251 */             dog = (dig > sg) ? dig : sg;
/* 252 */             dob = (dib > sb) ? dib : sb;
/*     */             break;
/*     */           
/*     */           case 22:
/* 256 */             dor = (dir + sr) / 2;
/* 257 */             dog = (dig + sg) / 2;
/* 258 */             dob = (dib + sb) / 2;
/*     */             break;
/*     */           
/*     */           case 12:
/*     */           case 13:
/*     */           case 14:
/*     */           case 15:
/* 265 */             Color.RGBtoHSB(sr, sg, sb, sHsv);
/* 266 */             Color.RGBtoHSB(dir, dig, dib, diHsv);
/*     */             
/* 268 */             switch (this.rule) {
/*     */               
/*     */               case 12:
/* 271 */                 doHsv[0] = sHsv[0];
/* 272 */                 doHsv[1] = diHsv[1];
/* 273 */                 doHsv[2] = diHsv[2];
/*     */                 break;
/*     */               
/*     */               case 13:
/* 277 */                 doHsv[0] = diHsv[0];
/* 278 */                 doHsv[1] = sHsv[1];
/* 279 */                 doHsv[2] = diHsv[2];
/*     */                 break;
/*     */               
/*     */               case 14:
/* 283 */                 doHsv[0] = diHsv[0];
/* 284 */                 doHsv[1] = diHsv[1];
/* 285 */                 doHsv[2] = sHsv[2];
/*     */                 break;
/*     */               
/*     */               case 15:
/* 289 */                 doHsv[0] = sHsv[0];
/* 290 */                 doHsv[1] = sHsv[1];
/* 291 */                 doHsv[2] = diHsv[2];
/*     */                 break;
/*     */             } 
/*     */             
/* 295 */             doRGB = Color.HSBtoRGB(doHsv[0], doHsv[1], doHsv[2]);
/* 296 */             dor = (doRGB & 0xFF0000) >> 16;
/* 297 */             dog = (doRGB & 0xFF00) >> 8;
/* 298 */             dob = doRGB & 0xFF;
/*     */             break;
/*     */           
/*     */           case 6:
/* 302 */             if (dir != 255) {
/*     */               
/* 304 */               dor = clamp(255 - (255 - sr << 8) / (dir + 1));
/*     */             }
/*     */             else {
/*     */               
/* 308 */               dor = sr;
/*     */             } 
/*     */             
/* 311 */             if (dig != 255) {
/*     */               
/* 313 */               dog = clamp(255 - (255 - sg << 8) / (dig + 1));
/*     */             }
/*     */             else {
/*     */               
/* 317 */               dog = sg;
/*     */             } 
/*     */             
/* 320 */             if (dib != 255) {
/*     */               
/* 322 */               dob = clamp(255 - (255 - sb << 8) / (dib + 1));
/*     */               
/*     */               break;
/*     */             } 
/* 326 */             dob = sb;
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 7:
/* 332 */             if (sr != 0) {
/*     */               
/* 334 */               dor = Math.max(255 - (255 - dir << 8) / sr, 0);
/*     */             }
/*     */             else {
/*     */               
/* 338 */               dor = sr;
/*     */             } 
/*     */             
/* 341 */             if (sg != 0) {
/*     */               
/* 343 */               dog = Math.max(255 - (255 - dig << 8) / sg, 0);
/*     */             }
/*     */             else {
/*     */               
/* 347 */               dog = sg;
/*     */             } 
/*     */             
/* 350 */             if (sb != 0) {
/*     */               
/* 352 */               dob = Math.max(255 - (255 - dib << 8) / sb, 0);
/*     */               
/*     */               break;
/*     */             } 
/* 356 */             dob = sb;
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 10:
/* 362 */             dor = clamp((sr << 8) / (256 - dir));
/* 363 */             dog = clamp((sg << 8) / (256 - dig));
/* 364 */             dob = clamp((sb << 8) / (256 - dib));
/*     */             break;
/*     */           
/*     */           case 11:
/* 368 */             if (sr != 255) {
/*     */               
/* 370 */               dor = Math.min((dir << 8) / (255 - sr), 255);
/*     */             }
/*     */             else {
/*     */               
/* 374 */               dor = sr;
/*     */             } 
/*     */             
/* 377 */             if (sg != 255) {
/*     */               
/* 379 */               dog = Math.min((dig << 8) / (255 - sg), 255);
/*     */             }
/*     */             else {
/*     */               
/* 383 */               dog = sg;
/*     */             } 
/*     */             
/* 386 */             if (sb != 255) {
/*     */               
/* 388 */               dob = Math.min((dib << 8) / (255 - sb), 255);
/*     */               
/*     */               break;
/*     */             } 
/* 392 */             dob = sb;
/*     */             break;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case 17:
/* 399 */             d = multiply255(sr, dir);
/* 400 */             dor = d + multiply255(dir, 255 - multiply255(255 - dir, 255 - sr) - d);
/* 401 */             d = multiply255(sg, dig);
/* 402 */             dog = d + multiply255(dig, 255 - multiply255(255 - dig, 255 - sg) - d);
/* 403 */             d = multiply255(sb, dib);
/* 404 */             dob = d + multiply255(dib, 255 - multiply255(255 - dib, 255 - sb) - d);
/*     */             break;
/*     */           
/*     */           case 18:
/* 408 */             if (sr > 127) {
/*     */               
/* 410 */               dor = 255 - 2 * multiply255(255 - sr, 255 - dir);
/*     */             }
/*     */             else {
/*     */               
/* 414 */               dor = 2 * multiply255(sr, dir);
/*     */             } 
/*     */             
/* 417 */             if (sg > 127) {
/*     */               
/* 419 */               dog = 255 - 2 * multiply255(255 - sg, 255 - dig);
/*     */             }
/*     */             else {
/*     */               
/* 423 */               dog = 2 * multiply255(sg, dig);
/*     */             } 
/*     */             
/* 426 */             if (sb > 127) {
/*     */               
/* 428 */               dob = 255 - 2 * multiply255(255 - sb, 255 - dib);
/*     */               
/*     */               break;
/*     */             } 
/* 432 */             dob = 2 * multiply255(sb, dib);
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 19:
/* 438 */             dor = (sr > 127) ? Math.max(sr, dir) : Math.min(sr, dir);
/* 439 */             dog = (sg > 127) ? Math.max(sg, dig) : Math.min(sg, dig);
/* 440 */             dob = (sb > 127) ? Math.max(sb, dib) : Math.min(sb, dib);
/*     */             break;
/*     */           
/*     */           case 20:
/* 444 */             dor = dir + multiply255(sr, 255 - dir - dir);
/* 445 */             dog = dig + multiply255(sg, 255 - dig - dig);
/* 446 */             dob = dib + multiply255(sb, 255 - dib - dib);
/*     */             break;
/*     */           
/*     */           case 21:
/* 450 */             dor = 255 - Math.abs(255 - sr - dir);
/* 451 */             dog = 255 - Math.abs(255 - sg - dig);
/* 452 */             dob = 255 - Math.abs(255 - sb - dib);
/*     */             break;
/*     */         } 
/*     */         
/* 456 */         a = alpha * sa / 255.0F;
/* 457 */         ac = 1.0F - a;
/* 458 */         dstPix[i] = (int)(a * dor + ac * dir);
/* 459 */         dstPix[i + 1] = (int)(a * dog + ac * dig);
/* 460 */         dstPix[i + 2] = (int)(a * dob + ac * dib);
/* 461 */         dstPix[i + 3] = (int)(sa * alpha + dia * ac);
/* 462 */         i += 4;
/*     */       } 
/*     */       
/* 465 */       dstOut.setPixels(x, y, w, 1, dstPix);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\MiscCompositeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */