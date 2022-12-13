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
/*     */ public class ShapeFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   public static final int LINEAR = 0;
/*     */   public static final int CIRCLE_UP = 1;
/*     */   public static final int CIRCLE_DOWN = 2;
/*     */   public static final int SMOOTH = 3;
/*  36 */   private float factor = 1.0F;
/*     */   
/*     */   protected Colormap colormap;
/*     */   private boolean useAlpha = true;
/*     */   private boolean invert = false;
/*     */   private boolean merge = false;
/*     */   private int type;
/*     */   private static final int one = 41;
/*  44 */   private static final int sqrt2 = (int)(41.0D * Math.sqrt(2.0D));
/*  45 */   private static final int sqrt5 = (int)(41.0D * Math.sqrt(5.0D));
/*     */ 
/*     */   
/*     */   public ShapeFilter() {
/*  49 */     this.colormap = new LinearColormap();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFactor(float factor) {
/*  54 */     this.factor = factor;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFactor() {
/*  59 */     return this.factor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/*  69 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/*  79 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseAlpha(boolean useAlpha) {
/*  84 */     this.useAlpha = useAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseAlpha() {
/*  89 */     return this.useAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setType(int type) {
/*  94 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  99 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInvert(boolean invert) {
/* 104 */     this.invert = invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getInvert() {
/* 109 */     return this.invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMerge(boolean merge) {
/* 114 */     this.merge = merge;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getMerge() {
/* 119 */     return this.merge;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 124 */     int[] map = new int[width * height];
/* 125 */     makeMap(inPixels, map, width, height);
/* 126 */     int max = distanceMap(map, width, height);
/* 127 */     applyMap(map, inPixels, width, height, max);
/* 128 */     return inPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public int distanceMap(int[] map, int width, int height) {
/* 133 */     int xmax = width - 3;
/* 134 */     int ymax = height - 3;
/* 135 */     int max = 0;
/*     */     
/*     */     int y;
/* 138 */     for (y = 0; y < height; y++) {
/*     */       
/* 140 */       for (int x = 0; x < width; x++) {
/*     */         
/* 142 */         int offset = x + y * width;
/*     */         
/* 144 */         if (map[offset] > 0) {
/*     */           int v;
/* 146 */           if (x < 2 || x > xmax || y < 2 || y > ymax) {
/*     */             
/* 148 */             v = setEdgeValue(x, y, map, width, offset, xmax, ymax);
/*     */           }
/*     */           else {
/*     */             
/* 152 */             v = setValue(map, width, offset);
/*     */           } 
/*     */           
/* 155 */           if (v > max)
/*     */           {
/* 157 */             max = v;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 163 */     for (y = height - 1; y >= 0; y--) {
/*     */       
/* 165 */       for (int x = width - 1; x >= 0; x--) {
/*     */         
/* 167 */         int offset = x + y * width;
/*     */         
/* 169 */         if (map[offset] > 0) {
/*     */           int v;
/* 171 */           if (x < 2 || x > xmax || y < 2 || y > ymax) {
/*     */             
/* 173 */             v = setEdgeValue(x, y, map, width, offset, xmax, ymax);
/*     */           }
/*     */           else {
/*     */             
/* 177 */             v = setValue(map, width, offset);
/*     */           } 
/*     */           
/* 180 */           if (v > max)
/*     */           {
/* 182 */             max = v;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 188 */     return max;
/*     */   }
/*     */ 
/*     */   
/*     */   private void makeMap(int[] pixels, int[] map, int width, int height) {
/* 193 */     for (int y = 0; y < height; y++) {
/*     */       
/* 195 */       for (int x = 0; x < width; x++) {
/*     */         
/* 197 */         int offset = x + y * width;
/* 198 */         int b = this.useAlpha ? (pixels[offset] >> 24 & 0xFF) : PixelUtils.brightness(pixels[offset]);
/*     */         
/* 200 */         map[offset] = b * 41 / 10;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void applyMap(int[] map, int[] pixels, int width, int height, int max) {
/* 207 */     if (max == 0)
/*     */     {
/* 209 */       max = 1;
/*     */     }
/*     */     
/* 212 */     for (int y = 0; y < height; y++) {
/*     */       
/* 214 */       for (int x = 0; x < width; x++) {
/*     */         
/* 216 */         int offset = x + y * width;
/* 217 */         int m = map[offset];
/* 218 */         float v = 0.0F;
/* 219 */         int sa = 0, sr = 0, sg = 0, sb = 0;
/*     */         
/* 221 */         if (m == 0) {
/*     */ 
/*     */           
/* 224 */           sa = sr = sg = sb = 0;
/* 225 */           sa = pixels[offset] >> 24 & 0xFF;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 230 */           v = ImageMath.clamp(this.factor * m / max, 0.0F, 1.0F);
/*     */           
/* 232 */           switch (this.type) {
/*     */             
/*     */             case 1:
/* 235 */               v = ImageMath.circleUp(v);
/*     */               break;
/*     */             
/*     */             case 2:
/* 239 */               v = ImageMath.circleDown(v);
/*     */               break;
/*     */             
/*     */             case 3:
/* 243 */               v = ImageMath.smoothStep(0.0F, 1.0F, v);
/*     */               break;
/*     */           } 
/*     */           
/* 247 */           if (this.colormap == null) {
/*     */             
/* 249 */             sr = sg = sb = (int)(v * 255.0F);
/*     */           }
/*     */           else {
/*     */             
/* 253 */             int c = this.colormap.getColor(v);
/* 254 */             sr = c >> 16 & 0xFF;
/* 255 */             sg = c >> 8 & 0xFF;
/* 256 */             sb = c & 0xFF;
/*     */           } 
/*     */           
/* 259 */           sa = this.useAlpha ? (pixels[offset] >> 24 & 0xFF) : PixelUtils.brightness(pixels[offset]);
/*     */ 
/*     */           
/* 262 */           if (this.invert) {
/*     */             
/* 264 */             sr = 255 - sr;
/* 265 */             sg = 255 - sg;
/* 266 */             sb = 255 - sb;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 271 */         if (this.merge) {
/*     */ 
/*     */           
/* 274 */           int transp = 255;
/* 275 */           int col = pixels[offset];
/* 276 */           int a = (col & 0xFF000000) >> 24;
/* 277 */           int r = (col & 0xFF0000) >> 16;
/* 278 */           int g = (col & 0xFF00) >> 8;
/* 279 */           int b = col & 0xFF;
/* 280 */           r = sr * r / transp;
/* 281 */           g = sg * g / transp;
/* 282 */           b = sb * b / transp;
/*     */ 
/*     */           
/* 285 */           if (r < 0)
/*     */           {
/* 287 */             r = 0;
/*     */           }
/*     */           
/* 290 */           if (r > 255)
/*     */           {
/* 292 */             r = 255;
/*     */           }
/*     */           
/* 295 */           if (g < 0)
/*     */           {
/* 297 */             g = 0;
/*     */           }
/*     */           
/* 300 */           if (g > 255)
/*     */           {
/* 302 */             g = 255;
/*     */           }
/*     */           
/* 305 */           if (b < 0)
/*     */           {
/* 307 */             b = 0;
/*     */           }
/*     */           
/* 310 */           if (b > 255)
/*     */           {
/* 312 */             b = 255;
/*     */           }
/*     */           
/* 315 */           pixels[offset] = a << 24 | r << 16 | g << 8 | b;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 320 */           pixels[offset] = sa << 24 | sr << 16 | sg << 8 | sb;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int setEdgeValue(int x, int y, int[] map, int width, int offset, int xmax, int ymax) {
/* 330 */     int r1 = offset - width - width - 2;
/* 331 */     int r2 = r1 + width;
/* 332 */     int r3 = r2 + width;
/* 333 */     int r4 = r3 + width;
/* 334 */     int r5 = r4 + width;
/*     */     
/* 336 */     if (y == 0 || x == 0 || y == ymax + 2 || x == xmax + 2) {
/*     */       
/* 338 */       map[offset] = 41; return 41;
/*     */     } 
/*     */     
/* 341 */     int v = map[r2 + 2] + 41;
/* 342 */     int min = v;
/* 343 */     v = map[r3 + 1] + 41;
/*     */     
/* 345 */     if (v < min)
/*     */     {
/* 347 */       min = v;
/*     */     }
/*     */     
/* 350 */     v = map[r3 + 3] + 41;
/*     */     
/* 352 */     if (v < min)
/*     */     {
/* 354 */       min = v;
/*     */     }
/*     */     
/* 357 */     v = map[r4 + 2] + 41;
/*     */     
/* 359 */     if (v < min)
/*     */     {
/* 361 */       min = v;
/*     */     }
/*     */     
/* 364 */     v = map[r2 + 1] + sqrt2;
/*     */     
/* 366 */     if (v < min)
/*     */     {
/* 368 */       min = v;
/*     */     }
/*     */     
/* 371 */     v = map[r2 + 3] + sqrt2;
/*     */     
/* 373 */     if (v < min)
/*     */     {
/* 375 */       min = v;
/*     */     }
/*     */     
/* 378 */     v = map[r4 + 1] + sqrt2;
/*     */     
/* 380 */     if (v < min)
/*     */     {
/* 382 */       min = v;
/*     */     }
/*     */     
/* 385 */     v = map[r4 + 3] + sqrt2;
/*     */     
/* 387 */     if (v < min)
/*     */     {
/* 389 */       min = v;
/*     */     }
/*     */     
/* 392 */     if (y == 1 || x == 1 || y == ymax + 1 || x == xmax + 1) {
/*     */       
/* 394 */       map[offset] = min; return min;
/*     */     } 
/*     */     
/* 397 */     v = map[r1 + 1] + sqrt5;
/*     */     
/* 399 */     if (v < min)
/*     */     {
/* 401 */       min = v;
/*     */     }
/*     */     
/* 404 */     v = map[r1 + 3] + sqrt5;
/*     */     
/* 406 */     if (v < min)
/*     */     {
/* 408 */       min = v;
/*     */     }
/*     */     
/* 411 */     v = map[r2 + 4] + sqrt5;
/*     */     
/* 413 */     if (v < min)
/*     */     {
/* 415 */       min = v;
/*     */     }
/*     */     
/* 418 */     v = map[r4 + 4] + sqrt5;
/*     */     
/* 420 */     if (v < min)
/*     */     {
/* 422 */       min = v;
/*     */     }
/*     */     
/* 425 */     v = map[r5 + 3] + sqrt5;
/*     */     
/* 427 */     if (v < min)
/*     */     {
/* 429 */       min = v;
/*     */     }
/*     */     
/* 432 */     v = map[r5 + 1] + sqrt5;
/*     */     
/* 434 */     if (v < min)
/*     */     {
/* 436 */       min = v;
/*     */     }
/*     */     
/* 439 */     v = map[r4] + sqrt5;
/*     */     
/* 441 */     if (v < min)
/*     */     {
/* 443 */       min = v;
/*     */     }
/*     */     
/* 446 */     v = map[r2] + sqrt5;
/*     */     
/* 448 */     if (v < min)
/*     */     {
/* 450 */       min = v;
/*     */     }
/*     */     
/* 453 */     map[offset] = min; return min;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int setValue(int[] map, int width, int offset) {
/* 460 */     int r1 = offset - width - width - 2;
/* 461 */     int r2 = r1 + width;
/* 462 */     int r3 = r2 + width;
/* 463 */     int r4 = r3 + width;
/* 464 */     int r5 = r4 + width;
/* 465 */     int v = map[r2 + 2] + 41;
/* 466 */     int min = v;
/* 467 */     v = map[r3 + 1] + 41;
/*     */     
/* 469 */     if (v < min)
/*     */     {
/* 471 */       min = v;
/*     */     }
/*     */     
/* 474 */     v = map[r3 + 3] + 41;
/*     */     
/* 476 */     if (v < min)
/*     */     {
/* 478 */       min = v;
/*     */     }
/*     */     
/* 481 */     v = map[r4 + 2] + 41;
/*     */     
/* 483 */     if (v < min)
/*     */     {
/* 485 */       min = v;
/*     */     }
/*     */     
/* 488 */     v = map[r2 + 1] + sqrt2;
/*     */     
/* 490 */     if (v < min)
/*     */     {
/* 492 */       min = v;
/*     */     }
/*     */     
/* 495 */     v = map[r2 + 3] + sqrt2;
/*     */     
/* 497 */     if (v < min)
/*     */     {
/* 499 */       min = v;
/*     */     }
/*     */     
/* 502 */     v = map[r4 + 1] + sqrt2;
/*     */     
/* 504 */     if (v < min)
/*     */     {
/* 506 */       min = v;
/*     */     }
/*     */     
/* 509 */     v = map[r4 + 3] + sqrt2;
/*     */     
/* 511 */     if (v < min)
/*     */     {
/* 513 */       min = v;
/*     */     }
/*     */     
/* 516 */     v = map[r1 + 1] + sqrt5;
/*     */     
/* 518 */     if (v < min)
/*     */     {
/* 520 */       min = v;
/*     */     }
/*     */     
/* 523 */     v = map[r1 + 3] + sqrt5;
/*     */     
/* 525 */     if (v < min)
/*     */     {
/* 527 */       min = v;
/*     */     }
/*     */     
/* 530 */     v = map[r2 + 4] + sqrt5;
/*     */     
/* 532 */     if (v < min)
/*     */     {
/* 534 */       min = v;
/*     */     }
/*     */     
/* 537 */     v = map[r4 + 4] + sqrt5;
/*     */     
/* 539 */     if (v < min)
/*     */     {
/* 541 */       min = v;
/*     */     }
/*     */     
/* 544 */     v = map[r5 + 3] + sqrt5;
/*     */     
/* 546 */     if (v < min)
/*     */     {
/* 548 */       min = v;
/*     */     }
/*     */     
/* 551 */     v = map[r5 + 1] + sqrt5;
/*     */     
/* 553 */     if (v < min)
/*     */     {
/* 555 */       min = v;
/*     */     }
/*     */     
/* 558 */     v = map[r4] + sqrt5;
/*     */     
/* 560 */     if (v < min)
/*     */     {
/* 562 */       min = v;
/*     */     }
/*     */     
/* 565 */     v = map[r2] + sqrt5;
/*     */     
/* 567 */     if (v < min)
/*     */     {
/* 569 */       min = v;
/*     */     }
/*     */     
/* 572 */     map[offset] = min; return min;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 577 */     return "Stylize/Shapeburst...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ShapeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */