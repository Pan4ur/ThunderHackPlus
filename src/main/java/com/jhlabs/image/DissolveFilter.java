/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DissolveFilter
/*    */   extends PointFilter
/*    */ {
/* 27 */   private float density = 1.0F;
/* 28 */   private float softness = 0.0F;
/*    */ 
/*    */ 
/*    */   
/*    */   private float minDensity;
/*    */ 
/*    */ 
/*    */   
/*    */   private float maxDensity;
/*    */ 
/*    */ 
/*    */   
/*    */   private Random randomNumbers;
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDensity(float density) {
/* 45 */     this.density = density;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getDensity() {
/* 55 */     return this.density;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSoftness(float softness) {
/* 67 */     this.softness = softness;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getSoftness() {
/* 77 */     return this.softness;
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 82 */     float d = (1.0F - this.density) * (1.0F + this.softness);
/* 83 */     this.minDensity = d - this.softness;
/* 84 */     this.maxDensity = d;
/* 85 */     this.randomNumbers = new Random(0L);
/* 86 */     return super.filter(src, dst);
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 91 */     int a = rgb >> 24 & 0xFF;
/* 92 */     float v = this.randomNumbers.nextFloat();
/* 93 */     float f = ImageMath.smoothStep(this.minDensity, this.maxDensity, v);
/* 94 */     return (int)(a * f) << 24 | rgb & 0xFFFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 99 */     return "Stylize/Dissolve...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DissolveFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */