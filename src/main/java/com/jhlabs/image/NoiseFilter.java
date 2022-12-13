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
/*     */ public class NoiseFilter
/*     */   extends PointFilter
/*     */ {
/*     */   public static final int GAUSSIAN = 0;
/*     */   public static final int UNIFORM = 1;
/*  37 */   private int amount = 25;
/*  38 */   private int distribution = 1;
/*     */   private boolean monochrome = false;
/*  40 */   private float density = 1.0F;
/*  41 */   private Random randomNumbers = new Random();
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
/*     */   public void setAmount(int amount) {
/*  56 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAmount() {
/*  66 */     return this.amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistribution(int distribution) {
/*  76 */     this.distribution = distribution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDistribution() {
/*  86 */     return this.distribution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMonochrome(boolean monochrome) {
/*  96 */     this.monochrome = monochrome;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getMonochrome() {
/* 106 */     return this.monochrome;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDensity(float density) {
/* 116 */     this.density = density;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDensity() {
/* 126 */     return this.density;
/*     */   }
/*     */ 
/*     */   
/*     */   private int random(int x) {
/* 131 */     x += (int)(((this.distribution == 0) ? this.randomNumbers.nextGaussian() : (2.0F * this.randomNumbers.nextFloat() - 1.0F)) * this.amount);
/*     */     
/* 133 */     if (x < 0) {
/*     */       
/* 135 */       x = 0;
/*     */     }
/* 137 */     else if (x > 255) {
/*     */       
/* 139 */       x = 255;
/*     */     } 
/*     */     
/* 142 */     return x;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 147 */     if (this.randomNumbers.nextFloat() <= this.density) {
/*     */       
/* 149 */       int a = rgb & 0xFF000000;
/* 150 */       int r = rgb >> 16 & 0xFF;
/* 151 */       int g = rgb >> 8 & 0xFF;
/* 152 */       int b = rgb & 0xFF;
/*     */       
/* 154 */       if (this.monochrome) {
/*     */         
/* 156 */         int n = (int)(((this.distribution == 0) ? this.randomNumbers.nextGaussian() : (2.0F * this.randomNumbers.nextFloat() - 1.0F)) * this.amount);
/* 157 */         r = PixelUtils.clamp(r + n);
/* 158 */         g = PixelUtils.clamp(g + n);
/* 159 */         b = PixelUtils.clamp(b + n);
/*     */       }
/*     */       else {
/*     */         
/* 163 */         r = random(r);
/* 164 */         g = random(g);
/* 165 */         b = random(b);
/*     */       } 
/*     */       
/* 168 */       return a | r << 16 | g << 8 | b;
/*     */     } 
/*     */     
/* 171 */     return rgb;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 176 */     return "Stylize/Add Noise...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\NoiseFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */