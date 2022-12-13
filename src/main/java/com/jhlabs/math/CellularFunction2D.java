/*     */ package com.jhlabs.math;
/*     */ 
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
/*     */ public class CellularFunction2D
/*     */   implements Function2D
/*     */ {
/*  24 */   public float distancePower = 2.0F;
/*     */   public boolean cells = false;
/*     */   public boolean angular = false;
/*  27 */   private float[] coefficients = new float[] { 1.0F, 0.0F, 0.0F, 0.0F };
/*  28 */   private Random random = new Random();
/*  29 */   private Point[] results = null;
/*     */ 
/*     */   
/*     */   public CellularFunction2D() {
/*  33 */     this.results = new Point[2];
/*     */     
/*  35 */     for (int j = 0; j < this.results.length; j++)
/*     */     {
/*  37 */       this.results[j] = new Point();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCoefficient(int c, float v) {
/*  43 */     this.coefficients[c] = v;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCoefficient(int c) {
/*  48 */     return this.coefficients[c];
/*     */   }
/*     */   
/*     */   class Point
/*     */   {
/*     */     int index;
/*     */     float x;
/*     */     float y;
/*     */     float distance;
/*     */   }
/*     */   
/*     */   private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
/*  60 */     this.random.setSeed((571 * cubeX + 23 * cubeY));
/*  61 */     int numPoints = 3 + this.random.nextInt() % 4;
/*  62 */     numPoints = 4;
/*     */     
/*  64 */     for (int i = 0; i < numPoints; i++) {
/*     */       
/*  66 */       float d, px = this.random.nextFloat();
/*  67 */       float py = this.random.nextFloat();
/*  68 */       float dx = Math.abs(x - px);
/*  69 */       float dy = Math.abs(y - py);
/*     */ 
/*     */       
/*  72 */       if (this.distancePower == 1.0F) {
/*     */         
/*  74 */         d = dx + dy;
/*     */       }
/*  76 */       else if (this.distancePower == 2.0F) {
/*     */         
/*  78 */         d = (float)Math.sqrt((dx * dx + dy * dy));
/*     */       }
/*     */       else {
/*     */         
/*  82 */         d = (float)Math.pow(Math.pow(dx, this.distancePower) + Math.pow(dy, this.distancePower), (1.0F / this.distancePower));
/*     */       } 
/*     */ 
/*     */       
/*  86 */       for (int j = 0; j < results.length; j++) {
/*     */         
/*  88 */         if ((results[j]).distance == Double.POSITIVE_INFINITY) {
/*     */           
/*  90 */           Point last = results[j];
/*  91 */           last.distance = d;
/*  92 */           last.x = px;
/*  93 */           last.y = py;
/*  94 */           results[j] = last;
/*     */           break;
/*     */         } 
/*  97 */         if (d < (results[j]).distance) {
/*     */           
/*  99 */           Point last = results[results.length - 1];
/*     */           
/* 101 */           for (int k = results.length - 1; k > j; k--)
/*     */           {
/* 103 */             results[k] = results[k - 1];
/*     */           }
/*     */           
/* 106 */           last.distance = d;
/* 107 */           last.x = px;
/* 108 */           last.y = py;
/* 109 */           results[j] = last;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 115 */     return (results[1]).distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float evaluate(float x, float y) {
/* 120 */     for (int j = 0; j < this.results.length; j++)
/*     */     {
/* 122 */       (this.results[j]).distance = Float.POSITIVE_INFINITY;
/*     */     }
/*     */     
/* 125 */     int ix = (int)x;
/* 126 */     int iy = (int)y;
/* 127 */     float fx = x - ix;
/* 128 */     float fy = y - iy;
/* 129 */     float d = checkCube(fx, fy, ix, iy, this.results);
/*     */     
/* 131 */     if (d > fy)
/*     */     {
/* 133 */       d = checkCube(fx, fy + 1.0F, ix, iy - 1, this.results);
/*     */     }
/*     */     
/* 136 */     if (d > 1.0F - fy)
/*     */     {
/* 138 */       d = checkCube(fx, fy - 1.0F, ix, iy + 1, this.results);
/*     */     }
/*     */     
/* 141 */     if (d > fx) {
/*     */       
/* 143 */       checkCube(fx + 1.0F, fy, ix - 1, iy, this.results);
/*     */       
/* 145 */       if (d > fy)
/*     */       {
/* 147 */         d = checkCube(fx + 1.0F, fy + 1.0F, ix - 1, iy - 1, this.results);
/*     */       }
/*     */       
/* 150 */       if (d > 1.0F - fy)
/*     */       {
/* 152 */         d = checkCube(fx + 1.0F, fy - 1.0F, ix - 1, iy + 1, this.results);
/*     */       }
/*     */     } 
/*     */     
/* 156 */     if (d > 1.0F - fx) {
/*     */       
/* 158 */       d = checkCube(fx - 1.0F, fy, ix + 1, iy, this.results);
/*     */       
/* 160 */       if (d > fy)
/*     */       {
/* 162 */         d = checkCube(fx - 1.0F, fy + 1.0F, ix + 1, iy - 1, this.results);
/*     */       }
/*     */       
/* 165 */       if (d > 1.0F - fy)
/*     */       {
/* 167 */         d = checkCube(fx - 1.0F, fy - 1.0F, ix + 1, iy + 1, this.results);
/*     */       }
/*     */     } 
/*     */     
/* 171 */     float t = 0.0F;
/*     */     
/* 173 */     for (int i = 0; i < 2; i++)
/*     */     {
/* 175 */       t += this.coefficients[i] * (this.results[i]).distance;
/*     */     }
/*     */     
/* 178 */     if (this.angular)
/*     */     {
/* 180 */       t = (float)(t + Math.atan2((fy - (this.results[0]).y), (fx - (this.results[0]).x)) / 6.283185307179586D + 0.5D);
/*     */     }
/*     */     
/* 183 */     return t;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\CellularFunction2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */