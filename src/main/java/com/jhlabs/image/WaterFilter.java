/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class WaterFilter
/*     */   extends TransformFilter
/*     */ {
/*  29 */   private float wavelength = 16.0F;
/*  30 */   private float amplitude = 10.0F;
/*  31 */   private float phase = 0.0F;
/*  32 */   private float centreX = 0.5F;
/*  33 */   private float centreY = 0.5F;
/*  34 */   private float radius = 50.0F;
/*     */   
/*  36 */   private float radius2 = 0.0F;
/*     */   
/*     */   private float icentreX;
/*     */   private float icentreY;
/*     */   
/*     */   public WaterFilter() {
/*  42 */     setEdgeAction(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWavelength(float wavelength) {
/*  52 */     this.wavelength = wavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getWavelength() {
/*  62 */     return this.wavelength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAmplitude(float amplitude) {
/*  72 */     this.amplitude = amplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmplitude() {
/*  82 */     return this.amplitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPhase(float phase) {
/*  92 */     this.phase = phase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getPhase() {
/* 102 */     return this.phase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/* 112 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/* 122 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 132 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 142 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 152 */     this.centreX = (float)centre.getX();
/* 153 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 163 */     return new Point2D.Float(this.centreX, this.centreY);
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
/* 174 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 184 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean inside(int v, int a, int b) {
/* 189 */     return (a <= v && v <= b);
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 194 */     this.icentreX = src.getWidth() * this.centreX;
/* 195 */     this.icentreY = src.getHeight() * this.centreY;
/*     */     
/* 197 */     if (this.radius == 0.0F)
/*     */     {
/* 199 */       this.radius = Math.min(this.icentreX, this.icentreY);
/*     */     }
/*     */     
/* 202 */     this.radius2 = this.radius * this.radius;
/* 203 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 208 */     float dx = x - this.icentreX;
/* 209 */     float dy = y - this.icentreY;
/* 210 */     float distance2 = dx * dx + dy * dy;
/*     */     
/* 212 */     if (distance2 > this.radius2) {
/*     */       
/* 214 */       out[0] = x;
/* 215 */       out[1] = y;
/*     */     }
/*     */     else {
/*     */       
/* 219 */       float distance = (float)Math.sqrt(distance2);
/* 220 */       float amount = this.amplitude * (float)Math.sin((distance / this.wavelength * 6.2831855F - this.phase));
/* 221 */       amount *= (this.radius - distance) / this.radius;
/*     */       
/* 223 */       if (distance != 0.0F)
/*     */       {
/* 225 */         amount *= this.wavelength / distance;
/*     */       }
/*     */       
/* 228 */       out[0] = x + dx * amount;
/* 229 */       out[1] = y + dy * amount;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 235 */     return "Distort/Water Ripples...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WaterFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */