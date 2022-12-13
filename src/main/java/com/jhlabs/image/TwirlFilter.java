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
/*     */ 
/*     */ public class TwirlFilter
/*     */   extends TransformFilter
/*     */ {
/*  30 */   private float angle = 0.0F;
/*  31 */   private float centreX = 0.5F;
/*  32 */   private float centreY = 0.5F;
/*  33 */   private float radius = 100.0F;
/*     */   
/*  35 */   private float radius2 = 0.0F;
/*     */ 
/*     */   
/*     */   private float icentreX;
/*     */   
/*     */   private float icentreY;
/*     */ 
/*     */   
/*     */   public TwirlFilter() {
/*  44 */     setEdgeAction(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  54 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  64 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/*  74 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/*  84 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/*  94 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 104 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 114 */     this.centreX = (float)centre.getX();
/* 115 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 125 */     return new Point2D.Float(this.centreX, this.centreY);
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
/* 136 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 146 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 151 */     this.icentreX = src.getWidth() * this.centreX;
/* 152 */     this.icentreY = src.getHeight() * this.centreY;
/*     */     
/* 154 */     if (this.radius == 0.0F)
/*     */     {
/* 156 */       this.radius = Math.min(this.icentreX, this.icentreY);
/*     */     }
/*     */     
/* 159 */     this.radius2 = this.radius * this.radius;
/* 160 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 165 */     float dx = x - this.icentreX;
/* 166 */     float dy = y - this.icentreY;
/* 167 */     float distance = dx * dx + dy * dy;
/*     */     
/* 169 */     if (distance > this.radius2) {
/*     */       
/* 171 */       out[0] = x;
/* 172 */       out[1] = y;
/*     */     }
/*     */     else {
/*     */       
/* 176 */       distance = (float)Math.sqrt(distance);
/* 177 */       float a = (float)Math.atan2(dy, dx) + this.angle * (this.radius - distance) / this.radius;
/* 178 */       out[0] = this.icentreX + distance * (float)Math.cos(a);
/* 179 */       out[1] = this.icentreY + distance * (float)Math.sin(a);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 185 */     return "Distort/Twirl...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TwirlFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */