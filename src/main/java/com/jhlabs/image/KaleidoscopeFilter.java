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
/*     */ public class KaleidoscopeFilter
/*     */   extends TransformFilter
/*     */ {
/*  28 */   private float angle = 0.0F;
/*  29 */   private float angle2 = 0.0F;
/*  30 */   private float centreX = 0.5F;
/*  31 */   private float centreY = 0.5F;
/*  32 */   private int sides = 3;
/*  33 */   private float radius = 0.0F;
/*     */ 
/*     */   
/*     */   private float icentreX;
/*     */ 
/*     */   
/*     */   private float icentreY;
/*     */ 
/*     */   
/*     */   public KaleidoscopeFilter() {
/*  43 */     setEdgeAction(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSides(int sides) {
/*  54 */     this.sides = sides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSides() {
/*  64 */     return this.sides;
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
/*  75 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  85 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle2(float angle2) {
/*  96 */     this.angle2 = angle2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle2() {
/* 106 */     return this.angle2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/* 116 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/* 126 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 136 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 146 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 156 */     this.centreX = (float)centre.getX();
/* 157 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 167 */     return new Point2D.Float(this.centreX, this.centreY);
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
/* 178 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 188 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 193 */     this.icentreX = src.getWidth() * this.centreX;
/* 194 */     this.icentreY = src.getHeight() * this.centreY;
/* 195 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 200 */     double dx = (x - this.icentreX);
/* 201 */     double dy = (y - this.icentreY);
/* 202 */     double r = Math.sqrt(dx * dx + dy * dy);
/* 203 */     double theta = Math.atan2(dy, dx) - this.angle - this.angle2;
/* 204 */     theta = ImageMath.triangle((float)(theta / Math.PI * this.sides * 0.5D));
/*     */     
/* 206 */     if (this.radius != 0.0F) {
/*     */       
/* 208 */       double c = Math.cos(theta);
/* 209 */       double radiusc = this.radius / c;
/* 210 */       r = radiusc * ImageMath.triangle((float)(r / radiusc));
/*     */     } 
/*     */     
/* 213 */     theta += this.angle;
/* 214 */     out[0] = (float)(this.icentreX + r * Math.cos(theta));
/* 215 */     out[1] = (float)(this.icentreY + r * Math.sin(theta));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 220 */     return "Distort/Kaleidoscope...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\KaleidoscopeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */