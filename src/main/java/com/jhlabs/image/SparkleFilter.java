/*     */ package com.jhlabs.image;
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
/*     */ public class SparkleFilter
/*     */   extends PointFilter
/*     */ {
/*  24 */   private int rays = 50;
/*  25 */   private int radius = 25;
/*  26 */   private int amount = 50;
/*  27 */   private int color = -1;
/*  28 */   private int randomness = 25;
/*     */   private int width;
/*     */   private int height;
/*  31 */   private long seed = 371L; private int centreX; private int centreY;
/*     */   private float[] rayLengths;
/*  33 */   private Random randomNumbers = new Random();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColor(int color) {
/*  41 */     this.color = color;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColor() {
/*  46 */     return this.color;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRandomness(int randomness) {
/*  51 */     this.randomness = randomness;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRandomness() {
/*  56 */     return this.randomness;
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
/*     */   public void setAmount(int amount) {
/*  68 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAmount() {
/*  78 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRays(int rays) {
/*  83 */     this.rays = rays;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRays() {
/*  88 */     return this.rays;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(int radius) {
/*  99 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRadius() {
/* 109 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDimensions(int width, int height) {
/* 114 */     this.width = width;
/* 115 */     this.height = height;
/* 116 */     this.centreX = width / 2;
/* 117 */     this.centreY = height / 2;
/* 118 */     super.setDimensions(width, height);
/* 119 */     this.randomNumbers.setSeed(this.seed);
/* 120 */     this.rayLengths = new float[this.rays];
/*     */     
/* 122 */     for (int i = 0; i < this.rays; i++)
/*     */     {
/* 124 */       this.rayLengths[i] = this.radius + this.randomness / 100.0F * this.radius * (float)this.randomNumbers.nextGaussian();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 130 */     float dx = (x - this.centreX);
/* 131 */     float dy = (y - this.centreY);
/* 132 */     float distance = dx * dx + dy * dy;
/* 133 */     float angle = (float)Math.atan2(dy, dx);
/* 134 */     float d = (angle + 3.1415927F) / 6.2831855F * this.rays;
/* 135 */     int i = (int)d;
/* 136 */     float f = d - i;
/*     */     
/* 138 */     if (this.radius != 0) {
/*     */       
/* 140 */       float length = ImageMath.lerp(f, this.rayLengths[i % this.rays], this.rayLengths[(i + 1) % this.rays]);
/* 141 */       float g = length * length / (distance + 1.0E-4F);
/* 142 */       g = (float)Math.pow(g, (100 - this.amount) / 50.0D);
/* 143 */       f -= 0.5F;
/*     */       
/* 145 */       f = 1.0F - f * f;
/* 146 */       f *= g;
/*     */     } 
/*     */     
/* 149 */     f = ImageMath.clamp(f, 0.0F, 1.0F);
/* 150 */     return ImageMath.mixColors(f, rgb, this.color);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 155 */     return "Stylize/Sparkle...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SparkleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */