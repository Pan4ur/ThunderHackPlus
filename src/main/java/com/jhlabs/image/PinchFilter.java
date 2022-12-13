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
/*     */ public class PinchFilter
/*     */   extends TransformFilter
/*     */ {
/*  28 */   private float angle = 0.0F;
/*  29 */   private float centreX = 0.5F;
/*  30 */   private float centreY = 0.5F;
/*  31 */   private float radius = 100.0F;
/*  32 */   private float amount = 0.5F;
/*     */   
/*  34 */   private float radius2 = 0.0F;
/*     */ 
/*     */   
/*     */   private float icentreX;
/*     */ 
/*     */   
/*     */   private float icentreY;
/*     */ 
/*     */   
/*     */   private float width;
/*     */ 
/*     */   
/*     */   private float height;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  51 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  61 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/*  71 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/*  81 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/*  91 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 101 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 111 */     this.centreX = (float)centre.getX();
/* 112 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 122 */     return new Point2D.Float(this.centreX, this.centreY);
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
/* 133 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 143 */     return this.radius;
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
/* 155 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/* 165 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 170 */     this.width = src.getWidth();
/* 171 */     this.height = src.getHeight();
/* 172 */     this.icentreX = this.width * this.centreX;
/* 173 */     this.icentreY = this.height * this.centreY;
/*     */     
/* 175 */     if (this.radius == 0.0F)
/*     */     {
/* 177 */       this.radius = Math.min(this.icentreX, this.icentreY);
/*     */     }
/*     */     
/* 180 */     this.radius2 = this.radius * this.radius;
/* 181 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 186 */     float dx = x - this.icentreX;
/* 187 */     float dy = y - this.icentreY;
/* 188 */     float distance = dx * dx + dy * dy;
/*     */     
/* 190 */     if (distance > this.radius2 || distance == 0.0F) {
/*     */       
/* 192 */       out[0] = x;
/* 193 */       out[1] = y;
/*     */     }
/*     */     else {
/*     */       
/* 197 */       float d = (float)Math.sqrt((distance / this.radius2));
/* 198 */       float t = (float)Math.pow(Math.sin(1.5707963267948966D * d), -this.amount);
/* 199 */       dx *= t;
/* 200 */       dy *= t;
/* 201 */       float e = 1.0F - d;
/* 202 */       float a = this.angle * e * e;
/* 203 */       float s = (float)Math.sin(a);
/* 204 */       float c = (float)Math.cos(a);
/* 205 */       out[0] = this.icentreX + c * dx - s * dy;
/* 206 */       out[1] = this.icentreY + s * dx + c * dy;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 212 */     return "Distort/Pinch...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PinchFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */