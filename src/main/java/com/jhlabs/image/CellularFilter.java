/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Function2D;
/*     */ import com.jhlabs.math.Noise;
/*     */ import java.awt.Rectangle;
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
/*     */ public class CellularFilter
/*     */   extends WholeImageFilter
/*     */   implements Function2D, Cloneable
/*     */ {
/*  29 */   protected float scale = 32.0F;
/*  30 */   protected float stretch = 1.0F;
/*  31 */   protected float angle = 0.0F;
/*  32 */   public float amount = 1.0F;
/*  33 */   public float turbulence = 1.0F;
/*  34 */   public float gain = 0.5F;
/*  35 */   public float bias = 0.5F;
/*  36 */   public float distancePower = 2.0F;
/*     */   public boolean useColor = false;
/*  38 */   protected Colormap colormap = new Gradient();
/*  39 */   protected float[] coefficients = new float[] { 1.0F, 0.0F, 0.0F, 0.0F };
/*     */   protected float angleCoefficient;
/*  41 */   protected Random random = new Random();
/*  42 */   protected float m00 = 1.0F;
/*  43 */   protected float m01 = 0.0F;
/*  44 */   protected float m10 = 0.0F;
/*  45 */   protected float m11 = 1.0F;
/*  46 */   protected Point[] results = null;
/*  47 */   protected float randomness = 0.0F;
/*  48 */   protected int gridType = 2;
/*     */   
/*     */   private float min;
/*     */   
/*     */   private float max;
/*     */   private static byte[] probabilities;
/*     */   private float gradientCoefficient;
/*     */   public static final int RANDOM = 0;
/*     */   public static final int SQUARE = 1;
/*     */   public static final int HEXAGONAL = 2;
/*     */   public static final int OCTAGONAL = 3;
/*     */   public static final int TRIANGULAR = 4;
/*     */   
/*     */   public CellularFilter() {
/*  62 */     this.results = new Point[3];
/*     */     
/*  64 */     for (int j = 0; j < this.results.length; j++)
/*     */     {
/*  66 */       this.results[j] = new Point();
/*     */     }
/*     */     
/*  69 */     if (probabilities == null) {
/*     */       
/*  71 */       probabilities = new byte[8192];
/*  72 */       float factorial = 1.0F;
/*  73 */       float total = 0.0F;
/*  74 */       float mean = 2.5F;
/*     */       
/*  76 */       for (int i = 0; i < 10; i++) {
/*     */         
/*  78 */         if (i > 1)
/*     */         {
/*  80 */           factorial *= i;
/*     */         }
/*     */         
/*  83 */         float probability = (float)Math.pow(mean, i) * (float)Math.exp(-mean) / factorial;
/*  84 */         int start = (int)(total * 8192.0F);
/*  85 */         total += probability;
/*  86 */         int end = (int)(total * 8192.0F);
/*     */         
/*  88 */         for (int k = start; k < end; k++)
/*     */         {
/*  90 */           probabilities[k] = (byte)i;
/*     */         }
/*     */       } 
/*     */     } 
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
/*     */   public void setScale(float scale) {
/* 105 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 115 */     return this.scale;
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
/*     */   public void setStretch(float stretch) {
/* 127 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 137 */     return this.stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/* 148 */     this.angle = angle;
/* 149 */     float cos = (float)Math.cos(angle);
/* 150 */     float sin = (float)Math.sin(angle);
/* 151 */     this.m00 = cos;
/* 152 */     this.m01 = sin;
/* 153 */     this.m10 = -sin;
/* 154 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 164 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCoefficient(int i, float v) {
/* 169 */     this.coefficients[i] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCoefficient(int i) {
/* 174 */     return this.coefficients[i];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAngleCoefficient(float angleCoefficient) {
/* 179 */     this.angleCoefficient = angleCoefficient;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngleCoefficient() {
/* 184 */     return this.angleCoefficient;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGradientCoefficient(float gradientCoefficient) {
/* 189 */     this.gradientCoefficient = gradientCoefficient;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGradientCoefficient() {
/* 194 */     return this.gradientCoefficient;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setF1(float v) {
/* 199 */     this.coefficients[0] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getF1() {
/* 204 */     return this.coefficients[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setF2(float v) {
/* 209 */     this.coefficients[1] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getF2() {
/* 214 */     return this.coefficients[1];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setF3(float v) {
/* 219 */     this.coefficients[2] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getF3() {
/* 224 */     return this.coefficients[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setF4(float v) {
/* 229 */     this.coefficients[3] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getF4() {
/* 234 */     return this.coefficients[3];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 244 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 254 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRandomness(float randomness) {
/* 259 */     this.randomness = randomness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRandomness() {
/* 264 */     return this.randomness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGridType(int gridType) {
/* 269 */     this.gridType = gridType;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getGridType() {
/* 274 */     return this.gridType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDistancePower(float distancePower) {
/* 279 */     this.distancePower = distancePower;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDistancePower() {
/* 284 */     return this.distancePower;
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
/*     */   public void setTurbulence(float turbulence) {
/* 296 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/* 306 */     return this.turbulence;
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
/*     */   public void setAmount(float amount) {
/* 318 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/* 328 */     return this.amount;
/*     */   }
/*     */   
/*     */   public class Point {
/*     */     public int index;
/*     */     public float x;
/*     */     public float y;
/*     */     public float dx;
/*     */     public float dy;
/*     */     public float cubeX;
/*     */     public float cubeY;
/*     */     public float distance; }
/*     */   
/*     */   private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
/*     */     int numPoints;
/* 343 */     this.random.setSeed((571 * cubeX + 23 * cubeY));
/*     */     
/* 345 */     switch (this.gridType) {
/*     */ 
/*     */       
/*     */       default:
/* 349 */         numPoints = probabilities[this.random.nextInt() & 0x1FFF];
/*     */         break;
/*     */       
/*     */       case 1:
/* 353 */         numPoints = 1;
/*     */         break;
/*     */       
/*     */       case 2:
/* 357 */         numPoints = 1;
/*     */         break;
/*     */       
/*     */       case 3:
/* 361 */         numPoints = 2;
/*     */         break;
/*     */       
/*     */       case 4:
/* 365 */         numPoints = 2;
/*     */         break;
/*     */     } 
/*     */     
/* 369 */     for (int i = 0; i < numPoints; i++) {
/*     */       
/* 371 */       float d, px = 0.0F, py = 0.0F;
/* 372 */       float weight = 1.0F;
/*     */       
/* 374 */       switch (this.gridType) {
/*     */         
/*     */         case 0:
/* 377 */           px = this.random.nextFloat();
/* 378 */           py = this.random.nextFloat();
/*     */           break;
/*     */         
/*     */         case 1:
/* 382 */           px = py = 0.5F;
/*     */           
/* 384 */           if (this.randomness != 0.0F) {
/*     */             
/* 386 */             px = (float)(px + this.randomness * (this.random.nextFloat() - 0.5D));
/* 387 */             py = (float)(py + this.randomness * (this.random.nextFloat() - 0.5D));
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 2:
/* 393 */           if ((cubeX & 0x1) == 0) {
/*     */             
/* 395 */             px = 0.75F;
/* 396 */             py = 0.0F;
/*     */           }
/*     */           else {
/*     */             
/* 400 */             px = 0.75F;
/* 401 */             py = 0.5F;
/*     */           } 
/*     */           
/* 404 */           if (this.randomness != 0.0F) {
/*     */             
/* 406 */             px += this.randomness * Noise.noise2(271.0F * (cubeX + px), 271.0F * (cubeY + py));
/* 407 */             py += this.randomness * Noise.noise2(271.0F * (cubeX + px) + 89.0F, 271.0F * (cubeY + py) + 137.0F);
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 3:
/* 413 */           switch (i) {
/*     */             
/*     */             case 0:
/* 416 */               px = 0.207F;
/* 417 */               py = 0.207F;
/*     */               break;
/*     */             
/*     */             case 1:
/* 421 */               px = 0.707F;
/* 422 */               py = 0.707F;
/* 423 */               weight = 1.6F;
/*     */               break;
/*     */           } 
/*     */           
/* 427 */           if (this.randomness != 0.0F) {
/*     */             
/* 429 */             px += this.randomness * Noise.noise2(271.0F * (cubeX + px), 271.0F * (cubeY + py));
/* 430 */             py += this.randomness * Noise.noise2(271.0F * (cubeX + px) + 89.0F, 271.0F * (cubeY + py) + 137.0F);
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 4:
/* 436 */           if ((cubeY & 0x1) == 0) {
/*     */             
/* 438 */             if (i == 0)
/*     */             {
/* 440 */               px = 0.25F;
/* 441 */               py = 0.35F;
/*     */             }
/*     */             else
/*     */             {
/* 445 */               px = 0.75F;
/* 446 */               py = 0.65F;
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 451 */           else if (i == 0) {
/*     */             
/* 453 */             px = 0.75F;
/* 454 */             py = 0.35F;
/*     */           }
/*     */           else {
/*     */             
/* 458 */             px = 0.25F;
/* 459 */             py = 0.65F;
/*     */           } 
/*     */ 
/*     */           
/* 463 */           if (this.randomness != 0.0F) {
/*     */             
/* 465 */             px += this.randomness * Noise.noise2(271.0F * (cubeX + px), 271.0F * (cubeY + py));
/* 466 */             py += this.randomness * Noise.noise2(271.0F * (cubeX + px) + 89.0F, 271.0F * (cubeY + py) + 137.0F);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */ 
/*     */       
/* 472 */       float dx = Math.abs(x - px);
/* 473 */       float dy = Math.abs(y - py);
/*     */       
/* 475 */       dx *= weight;
/* 476 */       dy *= weight;
/*     */       
/* 478 */       if (this.distancePower == 1.0F) {
/*     */         
/* 480 */         d = dx + dy;
/*     */       }
/* 482 */       else if (this.distancePower == 2.0F) {
/*     */         
/* 484 */         d = (float)Math.sqrt((dx * dx + dy * dy));
/*     */       }
/*     */       else {
/*     */         
/* 488 */         d = (float)Math.pow(((float)Math.pow(dx, this.distancePower) + (float)Math.pow(dy, this.distancePower)), (1.0F / this.distancePower));
/*     */       } 
/*     */ 
/*     */       
/* 492 */       if (d < (results[0]).distance) {
/*     */         
/* 494 */         Point p = results[2];
/* 495 */         results[2] = results[1];
/* 496 */         results[1] = results[0];
/* 497 */         results[0] = p;
/* 498 */         p.distance = d;
/* 499 */         p.dx = dx;
/* 500 */         p.dy = dy;
/* 501 */         p.x = cubeX + px;
/* 502 */         p.y = cubeY + py;
/*     */       }
/* 504 */       else if (d < (results[1]).distance) {
/*     */         
/* 506 */         Point p = results[2];
/* 507 */         results[2] = results[1];
/* 508 */         results[1] = p;
/* 509 */         p.distance = d;
/* 510 */         p.dx = dx;
/* 511 */         p.dy = dy;
/* 512 */         p.x = cubeX + px;
/* 513 */         p.y = cubeY + py;
/*     */       }
/* 515 */       else if (d < (results[2]).distance) {
/*     */         
/* 517 */         Point p = results[2];
/* 518 */         p.distance = d;
/* 519 */         p.dx = dx;
/* 520 */         p.dy = dy;
/* 521 */         p.x = cubeX + px;
/* 522 */         p.y = cubeY + py;
/*     */       } 
/*     */     } 
/*     */     
/* 526 */     return (results[2]).distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float evaluate(float x, float y) {
/* 531 */     for (int j = 0; j < this.results.length; j++)
/*     */     {
/* 533 */       (this.results[j]).distance = Float.POSITIVE_INFINITY;
/*     */     }
/*     */     
/* 536 */     int ix = (int)x;
/* 537 */     int iy = (int)y;
/* 538 */     float fx = x - ix;
/* 539 */     float fy = y - iy;
/* 540 */     float d = checkCube(fx, fy, ix, iy, this.results);
/*     */     
/* 542 */     if (d > fy)
/*     */     {
/* 544 */       d = checkCube(fx, fy + 1.0F, ix, iy - 1, this.results);
/*     */     }
/*     */     
/* 547 */     if (d > 1.0F - fy)
/*     */     {
/* 549 */       d = checkCube(fx, fy - 1.0F, ix, iy + 1, this.results);
/*     */     }
/*     */     
/* 552 */     if (d > fx) {
/*     */       
/* 554 */       checkCube(fx + 1.0F, fy, ix - 1, iy, this.results);
/*     */       
/* 556 */       if (d > fy)
/*     */       {
/* 558 */         d = checkCube(fx + 1.0F, fy + 1.0F, ix - 1, iy - 1, this.results);
/*     */       }
/*     */       
/* 561 */       if (d > 1.0F - fy)
/*     */       {
/* 563 */         d = checkCube(fx + 1.0F, fy - 1.0F, ix - 1, iy + 1, this.results);
/*     */       }
/*     */     } 
/*     */     
/* 567 */     if (d > 1.0F - fx) {
/*     */       
/* 569 */       d = checkCube(fx - 1.0F, fy, ix + 1, iy, this.results);
/*     */       
/* 571 */       if (d > fy)
/*     */       {
/* 573 */         d = checkCube(fx - 1.0F, fy + 1.0F, ix + 1, iy - 1, this.results);
/*     */       }
/*     */       
/* 576 */       if (d > 1.0F - fy)
/*     */       {
/* 578 */         d = checkCube(fx - 1.0F, fy - 1.0F, ix + 1, iy + 1, this.results);
/*     */       }
/*     */     } 
/*     */     
/* 582 */     float t = 0.0F;
/*     */     
/* 584 */     for (int i = 0; i < 3; i++)
/*     */     {
/* 586 */       t += this.coefficients[i] * (this.results[i]).distance;
/*     */     }
/*     */     
/* 589 */     if (this.angleCoefficient != 0.0F) {
/*     */       
/* 591 */       float angle = (float)Math.atan2((y - (this.results[0]).y), (x - (this.results[0]).x));
/*     */       
/* 593 */       if (angle < 0.0F)
/*     */       {
/* 595 */         angle += 6.2831855F;
/*     */       }
/*     */       
/* 598 */       angle /= 12.566371F;
/* 599 */       t += this.angleCoefficient * angle;
/*     */     } 
/*     */     
/* 602 */     if (this.gradientCoefficient != 0.0F) {
/*     */       
/* 604 */       float a = 1.0F / ((this.results[0]).dy + (this.results[0]).dx);
/* 605 */       t += this.gradientCoefficient * a;
/*     */     } 
/*     */     
/* 608 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public float turbulence2(float x, float y, float freq) {
/* 613 */     float t = 0.0F;
/*     */     float f;
/* 615 */     for (f = 1.0F; f <= freq; f *= 2.0F)
/*     */     {
/* 617 */       t += evaluate(f * x, f * y) / f;
/*     */     }
/*     */     
/* 620 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPixel(int x, int y, int[] inPixels, int width, int height) {
/* 625 */     float nx = this.m00 * x + this.m01 * y;
/* 626 */     float ny = this.m10 * x + this.m11 * y;
/* 627 */     nx /= this.scale;
/* 628 */     ny /= this.scale * this.stretch;
/* 629 */     nx += 1000.0F;
/* 630 */     ny += 1000.0F;
/* 631 */     float f = (this.turbulence == 1.0F) ? evaluate(nx, ny) : turbulence2(nx, ny, this.turbulence);
/*     */ 
/*     */     
/* 634 */     f *= 2.0F;
/* 635 */     f *= this.amount;
/* 636 */     int a = -16777216;
/*     */ 
/*     */     
/* 639 */     if (this.colormap != null) {
/*     */       
/* 641 */       int i = this.colormap.getColor(f);
/*     */       
/* 643 */       if (this.useColor) {
/*     */         
/* 645 */         int srcx = ImageMath.clamp((int)(((this.results[0]).x - 1000.0F) * this.scale), 0, width - 1);
/* 646 */         int srcy = ImageMath.clamp((int)(((this.results[0]).y - 1000.0F) * this.scale), 0, height - 1);
/* 647 */         i = inPixels[srcy * width + srcx];
/* 648 */         f = ((this.results[1]).distance - (this.results[0]).distance) / ((this.results[1]).distance + (this.results[0]).distance);
/* 649 */         f = ImageMath.smoothStep(this.coefficients[1], this.coefficients[0], f);
/* 650 */         i = ImageMath.mixColors(f, -16777216, i);
/*     */       } 
/*     */       
/* 653 */       return i;
/*     */     } 
/*     */ 
/*     */     
/* 657 */     int v = PixelUtils.clamp((int)(f * 255.0F));
/* 658 */     int r = v << 16;
/* 659 */     int g = v << 8;
/* 660 */     int b = v;
/* 661 */     return a | r | g | b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 670 */     int index = 0;
/* 671 */     int[] outPixels = new int[width * height];
/*     */     
/* 673 */     for (int y = 0; y < height; y++) {
/*     */       
/* 675 */       for (int x = 0; x < width; x++)
/*     */       {
/* 677 */         outPixels[index++] = getPixel(x, y, inPixels, width, height);
/*     */       }
/*     */     } 
/*     */     
/* 681 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 686 */     CellularFilter f = (CellularFilter)super.clone();
/* 687 */     f.coefficients = (float[])this.coefficients.clone();
/* 688 */     f.results = (Point[])this.results.clone();
/* 689 */     f.random = new Random();
/*     */ 
/*     */     
/* 692 */     return f;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 697 */     return "Texture/Cellular...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\CellularFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */