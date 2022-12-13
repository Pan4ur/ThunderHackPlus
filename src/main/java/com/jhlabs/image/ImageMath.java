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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ImageMath
/*     */ {
/*     */   public static final float PI = 3.1415927F;
/*     */   public static final float HALF_PI = 1.5707964F;
/*     */   public static final float QUARTER_PI = 0.7853982F;
/*     */   public static final float TWO_PI = 6.2831855F;
/*     */   private static final float m00 = -0.5F;
/*     */   private static final float m01 = 1.5F;
/*     */   private static final float m02 = -1.5F;
/*     */   private static final float m03 = 0.5F;
/*     */   private static final float m10 = 1.0F;
/*     */   private static final float m11 = -2.5F;
/*     */   private static final float m12 = 2.0F;
/*     */   private static final float m13 = -0.5F;
/*     */   private static final float m20 = -0.5F;
/*     */   private static final float m21 = 0.0F;
/*     */   private static final float m22 = 0.5F;
/*     */   private static final float m23 = 0.0F;
/*     */   private static final float m30 = 0.0F;
/*     */   private static final float m31 = 1.0F;
/*     */   private static final float m32 = 0.0F;
/*     */   private static final float m33 = 0.0F;
/*     */   
/*     */   public static float bias(float a, float b) {
/*  54 */     return a / ((1.0F / b - 2.0F) * (1.0F - a) + 1.0F);
/*     */   }
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
/*     */   public static float gain(float a, float b) {
/*  77 */     float c = (1.0F / b - 2.0F) * (1.0F - 2.0F * a);
/*     */     
/*  79 */     if (a < 0.5D)
/*     */     {
/*  81 */       return a / (c + 1.0F);
/*     */     }
/*     */ 
/*     */     
/*  85 */     return (c - a) / (c - 1.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float step(float a, float x) {
/*  97 */     return (x < a) ? 0.0F : 1.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float pulse(float a, float b, float x) {
/* 109 */     return (x < a || x >= b) ? 0.0F : 1.0F;
/*     */   }
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
/*     */   public static float smoothPulse(float a1, float a2, float b1, float b2, float x) {
/* 123 */     if (x < a1 || x >= b2)
/*     */     {
/* 125 */       return 0.0F;
/*     */     }
/*     */     
/* 128 */     if (x >= a2) {
/*     */       
/* 130 */       if (x < b1)
/*     */       {
/* 132 */         return 1.0F;
/*     */       }
/*     */       
/* 135 */       x = (x - b1) / (b2 - b1);
/* 136 */       return 1.0F - x * x * (3.0F - 2.0F * x);
/*     */     } 
/*     */     
/* 139 */     x = (x - a1) / (a2 - a1);
/* 140 */     return x * x * (3.0F - 2.0F * x);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float smoothStep(float a, float b, float x) {
/* 152 */     if (x < a)
/*     */     {
/* 154 */       return 0.0F;
/*     */     }
/*     */     
/* 157 */     if (x >= b)
/*     */     {
/* 159 */       return 1.0F;
/*     */     }
/*     */     
/* 162 */     x = (x - a) / (b - a);
/* 163 */     return x * x * (3.0F - 2.0F * x);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float circleUp(float x) {
/* 173 */     x = 1.0F - x;
/* 174 */     return (float)Math.sqrt((1.0F - x * x));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float circleDown(float x) {
/* 184 */     return 1.0F - (float)Math.sqrt((1.0F - x * x));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float clamp(float x, float a, float b) {
/* 196 */     return (x < a) ? a : ((x > b) ? b : x);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int clamp(int x, int a, int b) {
/* 208 */     return (x < a) ? a : ((x > b) ? b : x);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double mod(double a, double b) {
/* 219 */     int n = (int)(a / b);
/* 220 */     a -= n * b;
/*     */     
/* 222 */     if (a < 0.0D)
/*     */     {
/* 224 */       return a + b;
/*     */     }
/*     */     
/* 227 */     return a;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float mod(float a, float b) {
/* 238 */     int n = (int)(a / b);
/* 239 */     a -= n * b;
/*     */     
/* 241 */     if (a < 0.0F)
/*     */     {
/* 243 */       return a + b;
/*     */     }
/*     */     
/* 246 */     return a;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mod(int a, int b) {
/* 257 */     int n = a / b;
/* 258 */     a -= n * b;
/*     */     
/* 260 */     if (a < 0)
/*     */     {
/* 262 */       return a + b;
/*     */     }
/*     */     
/* 265 */     return a;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float triangle(float x) {
/* 275 */     float r = mod(x, 1.0F);
/* 276 */     return 2.0F * ((r < 0.5D) ? r : (1.0F - r));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float lerp(float t, float a, float b) {
/* 288 */     return a + t * (b - a);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int lerp(float t, int a, int b) {
/* 300 */     return (int)(a + t * (b - a));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mixColors(float t, int rgb1, int rgb2) {
/* 312 */     int a1 = rgb1 >> 24 & 0xFF;
/* 313 */     int r1 = rgb1 >> 16 & 0xFF;
/* 314 */     int g1 = rgb1 >> 8 & 0xFF;
/* 315 */     int b1 = rgb1 & 0xFF;
/* 316 */     int a2 = rgb2 >> 24 & 0xFF;
/* 317 */     int r2 = rgb2 >> 16 & 0xFF;
/* 318 */     int g2 = rgb2 >> 8 & 0xFF;
/* 319 */     int b2 = rgb2 & 0xFF;
/* 320 */     a1 = lerp(t, a1, a2);
/* 321 */     r1 = lerp(t, r1, r2);
/* 322 */     g1 = lerp(t, g1, g2);
/* 323 */     b1 = lerp(t, b1, b2);
/* 324 */     return a1 << 24 | r1 << 16 | g1 << 8 | b1;
/*     */   }
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
/*     */   public static int bilinearInterpolate(float x, float y, int nw, int ne, int sw, int se) {
/* 337 */     int a0 = nw >> 24 & 0xFF;
/* 338 */     int r0 = nw >> 16 & 0xFF;
/* 339 */     int g0 = nw >> 8 & 0xFF;
/* 340 */     int b0 = nw & 0xFF;
/* 341 */     int a1 = ne >> 24 & 0xFF;
/* 342 */     int r1 = ne >> 16 & 0xFF;
/* 343 */     int g1 = ne >> 8 & 0xFF;
/* 344 */     int b1 = ne & 0xFF;
/* 345 */     int a2 = sw >> 24 & 0xFF;
/* 346 */     int r2 = sw >> 16 & 0xFF;
/* 347 */     int g2 = sw >> 8 & 0xFF;
/* 348 */     int b2 = sw & 0xFF;
/* 349 */     int a3 = se >> 24 & 0xFF;
/* 350 */     int r3 = se >> 16 & 0xFF;
/* 351 */     int g3 = se >> 8 & 0xFF;
/* 352 */     int b3 = se & 0xFF;
/* 353 */     float cx = 1.0F - x;
/* 354 */     float cy = 1.0F - y;
/* 355 */     float m0 = cx * a0 + x * a1;
/* 356 */     float m1 = cx * a2 + x * a3;
/* 357 */     int a = (int)(cy * m0 + y * m1);
/* 358 */     m0 = cx * r0 + x * r1;
/* 359 */     m1 = cx * r2 + x * r3;
/* 360 */     int r = (int)(cy * m0 + y * m1);
/* 361 */     m0 = cx * g0 + x * g1;
/* 362 */     m1 = cx * g2 + x * g3;
/* 363 */     int g = (int)(cy * m0 + y * m1);
/* 364 */     m0 = cx * b0 + x * b1;
/* 365 */     m1 = cx * b2 + x * b3;
/* 366 */     int b = (int)(cy * m0 + y * m1);
/* 367 */     return a << 24 | r << 16 | g << 8 | b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int brightnessNTSC(int rgb) {
/* 377 */     int r = rgb >> 16 & 0xFF;
/* 378 */     int g = rgb >> 8 & 0xFF;
/* 379 */     int b = rgb & 0xFF;
/* 380 */     return (int)(r * 0.299F + g * 0.587F + b * 0.114F);
/*     */   }
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static float spline(float x, int numKnots, float[] knots) {
/* 411 */     int numSpans = numKnots - 3;
/*     */ 
/*     */ 
/*     */     
/* 415 */     if (numSpans < 1)
/*     */     {
/* 417 */       throw new IllegalArgumentException("Too few knots in spline");
/*     */     }
/*     */     
/* 420 */     x = clamp(x, 0.0F, 1.0F) * numSpans;
/* 421 */     int span = (int)x;
/*     */     
/* 423 */     if (span > numKnots - 4)
/*     */     {
/* 425 */       span = numKnots - 4;
/*     */     }
/*     */     
/* 428 */     x -= span;
/* 429 */     float k0 = knots[span];
/* 430 */     float k1 = knots[span + 1];
/* 431 */     float k2 = knots[span + 2];
/* 432 */     float k3 = knots[span + 3];
/* 433 */     float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
/* 434 */     float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
/* 435 */     float c1 = -0.5F * k0 + 0.0F * k1 + 0.5F * k2 + 0.0F * k3;
/* 436 */     float c0 = 0.0F * k0 + 1.0F * k1 + 0.0F * k2 + 0.0F * k3;
/* 437 */     return ((c3 * x + c2) * x + c1) * x + c0;
/*     */   }
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
/*     */   public static float spline(float x, int numKnots, int[] xknots, int[] yknots) {
/* 451 */     int numSpans = numKnots - 3;
/*     */ 
/*     */ 
/*     */     
/* 455 */     if (numSpans < 1)
/*     */     {
/* 457 */       throw new IllegalArgumentException("Too few knots in spline");
/*     */     }
/*     */     int span;
/* 460 */     for (span = 0; span < numSpans && 
/* 461 */       xknots[span + 1] <= x; span++);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 466 */     if (span > numKnots - 3)
/*     */     {
/* 468 */       span = numKnots - 3;
/*     */     }
/*     */     
/* 471 */     float t = (x - xknots[span]) / (xknots[span + 1] - xknots[span]);
/* 472 */     span--;
/*     */     
/* 474 */     if (span < 0) {
/*     */       
/* 476 */       span = 0;
/* 477 */       t = 0.0F;
/*     */     } 
/*     */     
/* 480 */     float k0 = yknots[span];
/* 481 */     float k1 = yknots[span + 1];
/* 482 */     float k2 = yknots[span + 2];
/* 483 */     float k3 = yknots[span + 3];
/* 484 */     float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
/* 485 */     float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
/* 486 */     float c1 = -0.5F * k0 + 0.0F * k1 + 0.5F * k2 + 0.0F * k3;
/* 487 */     float c0 = 0.0F * k0 + 1.0F * k1 + 0.0F * k2 + 0.0F * k3;
/* 488 */     return ((c3 * t + c2) * t + c1) * t + c0;
/*     */   }
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
/*     */   public static int colorSpline(float x, int numKnots, int[] knots) {
/* 501 */     int numSpans = numKnots - 3;
/*     */ 
/*     */ 
/*     */     
/* 505 */     if (numSpans < 1)
/*     */     {
/* 507 */       throw new IllegalArgumentException("Too few knots in spline");
/*     */     }
/*     */     
/* 510 */     x = clamp(x, 0.0F, 1.0F) * numSpans;
/* 511 */     int span = (int)x;
/*     */     
/* 513 */     if (span > numKnots - 4)
/*     */     {
/* 515 */       span = numKnots - 4;
/*     */     }
/*     */     
/* 518 */     x -= span;
/* 519 */     int v = 0;
/*     */     
/* 521 */     for (int i = 0; i < 4; i++) {
/*     */       
/* 523 */       int shift = i * 8;
/* 524 */       float k0 = (knots[span] >> shift & 0xFF);
/* 525 */       float k1 = (knots[span + 1] >> shift & 0xFF);
/* 526 */       float k2 = (knots[span + 2] >> shift & 0xFF);
/* 527 */       float k3 = (knots[span + 3] >> shift & 0xFF);
/* 528 */       float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
/* 529 */       float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
/* 530 */       float c1 = -0.5F * k0 + 0.0F * k1 + 0.5F * k2 + 0.0F * k3;
/* 531 */       float c0 = 0.0F * k0 + 1.0F * k1 + 0.0F * k2 + 0.0F * k3;
/* 532 */       int n = (int)(((c3 * x + c2) * x + c1) * x + c0);
/*     */       
/* 534 */       if (n < 0) {
/*     */         
/* 536 */         n = 0;
/*     */       }
/* 538 */       else if (n > 255) {
/*     */         
/* 540 */         n = 255;
/*     */       } 
/*     */       
/* 543 */       v |= n << shift;
/*     */     } 
/*     */     
/* 546 */     return v;
/*     */   }
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
/*     */   public static int colorSpline(int x, int numKnots, int[] xknots, int[] yknots) {
/* 560 */     int numSpans = numKnots - 3;
/*     */ 
/*     */ 
/*     */     
/* 564 */     if (numSpans < 1)
/*     */     {
/* 566 */       throw new IllegalArgumentException("Too few knots in spline");
/*     */     }
/*     */     int span;
/* 569 */     for (span = 0; span < numSpans && 
/* 570 */       xknots[span + 1] <= x; span++);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 575 */     if (span > numKnots - 3)
/*     */     {
/* 577 */       span = numKnots - 3;
/*     */     }
/*     */     
/* 580 */     float t = (x - xknots[span]) / (xknots[span + 1] - xknots[span]);
/* 581 */     span--;
/*     */     
/* 583 */     if (span < 0) {
/*     */       
/* 585 */       span = 0;
/* 586 */       t = 0.0F;
/*     */     } 
/*     */     
/* 589 */     int v = 0;
/*     */     
/* 591 */     for (int i = 0; i < 4; i++) {
/*     */       
/* 593 */       int shift = i * 8;
/* 594 */       float k0 = (yknots[span] >> shift & 0xFF);
/* 595 */       float k1 = (yknots[span + 1] >> shift & 0xFF);
/* 596 */       float k2 = (yknots[span + 2] >> shift & 0xFF);
/* 597 */       float k3 = (yknots[span + 3] >> shift & 0xFF);
/* 598 */       float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
/* 599 */       float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
/* 600 */       float c1 = -0.5F * k0 + 0.0F * k1 + 0.5F * k2 + 0.0F * k3;
/* 601 */       float c0 = 0.0F * k0 + 1.0F * k1 + 0.0F * k2 + 0.0F * k3;
/* 602 */       int n = (int)(((c3 * t + c2) * t + c1) * t + c0);
/*     */       
/* 604 */       if (n < 0) {
/*     */         
/* 606 */         n = 0;
/*     */       }
/* 608 */       else if (n > 255) {
/*     */         
/* 610 */         n = 255;
/*     */       } 
/*     */       
/* 613 */       v |= n << shift;
/*     */     } 
/*     */     
/* 616 */     return v;
/*     */   }
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
/*     */   public static void resample(int[] source, int[] dest, int length, int offset, int stride, float[] out) {
/* 637 */     int srcIndex = offset;
/* 638 */     int destIndex = offset;
/* 639 */     int lastIndex = source.length;
/*     */     
/* 641 */     float[] in = new float[length + 2];
/* 642 */     int i = 0;
/*     */     
/* 644 */     for (int j = 0; j < length; j++) {
/*     */       
/* 646 */       while (out[i + 1] < j)
/*     */       {
/* 648 */         i++;
/*     */       }
/*     */       
/* 651 */       in[j] = i + (j - out[i]) / (out[i + 1] - out[i]);
/*     */     } 
/*     */ 
/*     */     
/* 655 */     in[length] = length;
/* 656 */     in[length + 1] = length;
/* 657 */     float inSegment = 1.0F;
/* 658 */     float outSegment = in[1];
/* 659 */     float sizfac = outSegment;
/* 660 */     float bSum = 0.0F, gSum = bSum, rSum = gSum, aSum = rSum;
/* 661 */     int rgb = source[srcIndex];
/* 662 */     int a = rgb >> 24 & 0xFF;
/* 663 */     int r = rgb >> 16 & 0xFF;
/* 664 */     int g = rgb >> 8 & 0xFF;
/* 665 */     int b = rgb & 0xFF;
/* 666 */     srcIndex += stride;
/* 667 */     rgb = source[srcIndex];
/* 668 */     int nextA = rgb >> 24 & 0xFF;
/* 669 */     int nextR = rgb >> 16 & 0xFF;
/* 670 */     int nextG = rgb >> 8 & 0xFF;
/* 671 */     int nextB = rgb & 0xFF;
/* 672 */     srcIndex += stride;
/* 673 */     i = 1;
/*     */     
/* 675 */     while (i <= length) {
/*     */       
/* 677 */       float aIntensity = inSegment * a + (1.0F - inSegment) * nextA;
/* 678 */       float rIntensity = inSegment * r + (1.0F - inSegment) * nextR;
/* 679 */       float gIntensity = inSegment * g + (1.0F - inSegment) * nextG;
/* 680 */       float bIntensity = inSegment * b + (1.0F - inSegment) * nextB;
/*     */       
/* 682 */       if (inSegment < outSegment) {
/*     */         
/* 684 */         aSum += aIntensity * inSegment;
/* 685 */         rSum += rIntensity * inSegment;
/* 686 */         gSum += gIntensity * inSegment;
/* 687 */         bSum += bIntensity * inSegment;
/* 688 */         outSegment -= inSegment;
/* 689 */         inSegment = 1.0F;
/* 690 */         a = nextA;
/* 691 */         r = nextR;
/* 692 */         g = nextG;
/* 693 */         b = nextB;
/*     */         
/* 695 */         if (srcIndex < lastIndex)
/*     */         {
/* 697 */           rgb = source[srcIndex];
/*     */         }
/*     */         
/* 700 */         nextA = rgb >> 24 & 0xFF;
/* 701 */         nextR = rgb >> 16 & 0xFF;
/* 702 */         nextG = rgb >> 8 & 0xFF;
/* 703 */         nextB = rgb & 0xFF;
/* 704 */         srcIndex += stride;
/*     */         
/*     */         continue;
/*     */       } 
/* 708 */       aSum += aIntensity * outSegment;
/* 709 */       rSum += rIntensity * outSegment;
/* 710 */       gSum += gIntensity * outSegment;
/* 711 */       bSum += bIntensity * outSegment;
/* 712 */       dest[destIndex] = 
/* 713 */         (int)Math.min(aSum / sizfac, 255.0F) << 24 | 
/* 714 */         (int)Math.min(rSum / sizfac, 255.0F) << 16 | 
/* 715 */         (int)Math.min(gSum / sizfac, 255.0F) << 8 | 
/* 716 */         (int)Math.min(bSum / sizfac, 255.0F);
/* 717 */       destIndex += stride;
/* 718 */       aSum = rSum = gSum = bSum = 0.0F;
/* 719 */       inSegment -= outSegment;
/* 720 */       outSegment = in[i + 1] - in[i];
/* 721 */       sizfac = outSegment;
/* 722 */       i++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void premultiply(int[] p, int offset, int length) {
/* 732 */     length += offset;
/*     */     
/* 734 */     for (int i = offset; i < length; i++) {
/*     */       
/* 736 */       int rgb = p[i];
/* 737 */       int a = rgb >> 24 & 0xFF;
/* 738 */       int r = rgb >> 16 & 0xFF;
/* 739 */       int g = rgb >> 8 & 0xFF;
/* 740 */       int b = rgb & 0xFF;
/* 741 */       float f = a * 0.003921569F;
/* 742 */       r = (int)(r * f);
/* 743 */       g = (int)(g * f);
/* 744 */       b = (int)(b * f);
/* 745 */       p[i] = a << 24 | r << 16 | g << 8 | b;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unpremultiply(int[] p, int offset, int length) {
/* 754 */     length += offset;
/*     */     
/* 756 */     for (int i = offset; i < length; i++) {
/*     */       
/* 758 */       int rgb = p[i];
/* 759 */       int a = rgb >> 24 & 0xFF;
/* 760 */       int r = rgb >> 16 & 0xFF;
/* 761 */       int g = rgb >> 8 & 0xFF;
/* 762 */       int b = rgb & 0xFF;
/*     */       
/* 764 */       if (a != 0 && a != 255) {
/*     */         
/* 766 */         float f = 255.0F / a;
/* 767 */         r = (int)(r * f);
/* 768 */         g = (int)(g * f);
/* 769 */         b = (int)(b * f);
/*     */         
/* 771 */         if (r > 255)
/*     */         {
/* 773 */           r = 255;
/*     */         }
/*     */         
/* 776 */         if (g > 255)
/*     */         {
/* 778 */           g = 255;
/*     */         }
/*     */         
/* 781 */         if (b > 255)
/*     */         {
/* 783 */           b = 255;
/*     */         }
/*     */         
/* 786 */         p[i] = a << 24 | r << 16 | g << 8 | b;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ImageMath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */