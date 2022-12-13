/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Noise;
/*     */ import java.awt.geom.Point2D;
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
/*     */ public class FlareFilter
/*     */   extends PointFilter
/*     */ {
/*  29 */   private int rays = 50;
/*     */   private float radius;
/*  31 */   private float baseAmount = 1.0F;
/*  32 */   private float ringAmount = 0.2F;
/*  33 */   private float rayAmount = 0.1F;
/*  34 */   private int color = -1;
/*     */   private int width;
/*  36 */   private float centreX = 0.5F, centreY = 0.5F; private int height;
/*  37 */   private float ringWidth = 1.6F;
/*     */   
/*  39 */   private float linear = 0.03F;
/*  40 */   private float gauss = 0.006F;
/*  41 */   private float mix = 0.5F;
/*  42 */   private float falloff = 6.0F;
/*     */   
/*     */   private float sigma;
/*     */   private float icentreX;
/*     */   private float icentreY;
/*     */   
/*     */   public FlareFilter() {
/*  49 */     setRadius(50.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColor(int color) {
/*  54 */     this.color = color;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColor() {
/*  59 */     return this.color;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRingWidth(float ringWidth) {
/*  64 */     this.ringWidth = ringWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRingWidth() {
/*  69 */     return this.ringWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBaseAmount(float baseAmount) {
/*  74 */     this.baseAmount = baseAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBaseAmount() {
/*  79 */     return this.baseAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRingAmount(float ringAmount) {
/*  84 */     this.ringAmount = ringAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRingAmount() {
/*  89 */     return this.ringAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRayAmount(float rayAmount) {
/*  94 */     this.rayAmount = rayAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRayAmount() {
/*  99 */     return this.rayAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 104 */     this.centreX = (float)centre.getX();
/* 105 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 110 */     return new Point2D.Float(this.centreX, this.centreY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/* 121 */     this.radius = radius;
/* 122 */     this.sigma = radius / 3.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 132 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDimensions(int width, int height) {
/* 137 */     this.width = width;
/* 138 */     this.height = height;
/* 139 */     this.icentreX = this.centreX * width;
/* 140 */     this.icentreY = this.centreY * height;
/* 141 */     super.setDimensions(width, height);
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 146 */     float ring, dx = x - this.icentreX;
/* 147 */     float dy = y - this.icentreY;
/* 148 */     float distance = (float)Math.sqrt((dx * dx + dy * dy));
/* 149 */     float a = (float)Math.exp((-distance * distance * this.gauss)) * this.mix + (float)Math.exp((-distance * this.linear)) * (1.0F - this.mix);
/*     */     
/* 151 */     a *= this.baseAmount;
/*     */     
/* 153 */     if (distance > this.radius + this.ringWidth)
/*     */     {
/* 155 */       a = ImageMath.lerp((distance - this.radius + this.ringWidth) / this.falloff, a, 0.0F);
/*     */     }
/*     */     
/* 158 */     if (distance < this.radius - this.ringWidth || distance > this.radius + this.ringWidth) {
/*     */       
/* 160 */       ring = 0.0F;
/*     */     }
/*     */     else {
/*     */       
/* 164 */       ring = Math.abs(distance - this.radius) / this.ringWidth;
/* 165 */       ring = 1.0F - ring * ring * (3.0F - 2.0F * ring);
/* 166 */       ring *= this.ringAmount;
/*     */     } 
/*     */     
/* 169 */     a += ring;
/* 170 */     float angle = (float)Math.atan2(dx, dy) + 3.1415927F;
/* 171 */     angle = (ImageMath.mod(angle / 3.1415927F * 17.0F + 1.0F + Noise.noise1(angle * 10.0F), 1.0F) - 0.5F) * 2.0F;
/* 172 */     angle = Math.abs(angle);
/* 173 */     angle = (float)Math.pow(angle, 5.0D);
/* 174 */     float b = this.rayAmount * angle / (1.0F + distance * 0.1F);
/* 175 */     a += b;
/* 176 */     a = ImageMath.clamp(a, 0.0F, 1.0F);
/* 177 */     return ImageMath.mixColors(a, rgb, this.color);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 182 */     return "Stylize/Flare...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FlareFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */