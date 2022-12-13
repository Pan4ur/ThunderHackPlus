/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Date;
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
/*     */ public class QuiltFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private Random randomGenerator;
/*  27 */   private long seed = 567L;
/*  28 */   private int iterations = 25000;
/*  29 */   private float a = -0.59F;
/*  30 */   private float b = 0.2F;
/*  31 */   private float c = 0.1F;
/*  32 */   private float d = 0.0F;
/*  33 */   private int k = 0;
/*  34 */   private Colormap colormap = new LinearColormap();
/*     */ 
/*     */   
/*     */   public QuiltFilter() {
/*  38 */     this.randomGenerator = new Random();
/*     */   }
/*     */ 
/*     */   
/*     */   public void randomize() {
/*  43 */     this.seed = (new Date()).getTime();
/*  44 */     this.randomGenerator.setSeed(this.seed);
/*  45 */     this.a = this.randomGenerator.nextFloat();
/*  46 */     this.b = this.randomGenerator.nextFloat();
/*  47 */     this.c = this.randomGenerator.nextFloat();
/*  48 */     this.d = this.randomGenerator.nextFloat();
/*  49 */     this.k = this.randomGenerator.nextInt() % 20 - 10;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIterations(int iterations) {
/*  60 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIterations() {
/*  70 */     return this.iterations;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setA(float a) {
/*  75 */     this.a = a;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getA() {
/*  80 */     return this.a;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setB(float b) {
/*  85 */     this.b = b;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getB() {
/*  90 */     return this.b;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setC(float c) {
/*  95 */     this.c = c;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getC() {
/* 100 */     return this.c;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setD(float d) {
/* 105 */     this.d = d;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getD() {
/* 110 */     return this.d;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setK(int k) {
/* 115 */     this.k = k;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getK() {
/* 120 */     return this.k;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 130 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 140 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 145 */     int[] outPixels = new int[width * height];
/* 146 */     int i = 0;
/* 147 */     int max = 0;
/* 148 */     float x = 0.1F;
/* 149 */     float y = 0.3F;
/*     */     int n;
/* 151 */     for (n = 0; n < 20; n++) {
/*     */       
/* 153 */       float mx = 3.1415927F * x;
/* 154 */       float my = 3.1415927F * y;
/* 155 */       float smx2 = (float)Math.sin((2.0F * mx));
/* 156 */       float smy2 = (float)Math.sin((2.0F * my));
/*     */       
/* 158 */       float x1 = (float)((this.a * smx2) + (this.b * smx2) * Math.cos((2.0F * my)) + this.c * Math.sin((4.0F * mx)) + this.d * Math.sin((6.0F * mx)) * Math.cos((4.0F * my)) + (this.k * x));
/* 159 */       x1 = (x1 >= 0.0F) ? (x1 - (int)x1) : (x1 - (int)x1 + 1.0F);
/*     */       
/* 161 */       float y1 = (float)((this.a * smy2) + (this.b * smy2) * Math.cos((2.0F * mx)) + this.c * Math.sin((4.0F * my)) + this.d * Math.sin((6.0F * my)) * Math.cos((4.0F * mx)) + (this.k * y));
/* 162 */       y1 = (y1 >= 0.0F) ? (y1 - (int)y1) : (y1 - (int)y1 + 1.0F);
/* 163 */       x = x1;
/* 164 */       y = y1;
/*     */     } 
/*     */     
/* 167 */     for (n = 0; n < this.iterations; n++) {
/*     */       
/* 169 */       float mx = 3.1415927F * x;
/* 170 */       float my = 3.1415927F * y;
/*     */       
/* 172 */       float x1 = (float)(this.a * Math.sin((2.0F * mx)) + this.b * Math.sin((2.0F * mx)) * Math.cos((2.0F * my)) + this.c * Math.sin((4.0F * mx)) + this.d * Math.sin((6.0F * mx)) * Math.cos((4.0F * my)) + (this.k * x));
/* 173 */       x1 = (x1 >= 0.0F) ? (x1 - (int)x1) : (x1 - (int)x1 + 1.0F);
/*     */       
/* 175 */       float y1 = (float)(this.a * Math.sin((2.0F * my)) + this.b * Math.sin((2.0F * my)) * Math.cos((2.0F * mx)) + this.c * Math.sin((4.0F * my)) + this.d * Math.sin((6.0F * my)) * Math.cos((4.0F * mx)) + (this.k * y));
/* 176 */       y1 = (y1 >= 0.0F) ? (y1 - (int)y1) : (y1 - (int)y1 + 1.0F);
/* 177 */       x = x1;
/* 178 */       y = y1;
/* 179 */       int ix = (int)(width * x);
/* 180 */       int iy = (int)(height * y);
/*     */       
/* 182 */       if (ix >= 0 && ix < width && iy >= 0 && iy < height) {
/*     */         
/* 184 */         outPixels[width * iy + ix] = outPixels[width * iy + ix] + 1; int t = outPixels[width * iy + ix];
/*     */         
/* 186 */         if (t > max)
/*     */         {
/* 188 */           max = t;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 193 */     if (this.colormap != null) {
/*     */       
/* 195 */       int index = 0;
/*     */       
/* 197 */       for (y = 0.0F; y < height; y++) {
/*     */         
/* 199 */         for (x = 0.0F; x < width; x++) {
/*     */           
/* 201 */           outPixels[index] = this.colormap.getColor(outPixels[index] / max);
/* 202 */           index++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 207 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 212 */     return "Texture/Chaotic Quilt...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\QuiltFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */