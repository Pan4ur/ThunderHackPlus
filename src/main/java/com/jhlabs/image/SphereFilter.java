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
/*     */ public class SphereFilter
/*     */   extends TransformFilter
/*     */ {
/*  28 */   private float a = 0.0F;
/*  29 */   private float b = 0.0F;
/*  30 */   private float a2 = 0.0F;
/*  31 */   private float b2 = 0.0F;
/*  32 */   private float centreX = 0.5F;
/*  33 */   private float centreY = 0.5F;
/*  34 */   private float refractionIndex = 1.5F;
/*     */   
/*     */   private float icentreX;
/*     */   
/*     */   private float icentreY;
/*     */   
/*     */   public SphereFilter() {
/*  41 */     setEdgeAction(1);
/*  42 */     setRadius(100.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRefractionIndex(float refractionIndex) {
/*  52 */     this.refractionIndex = refractionIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRefractionIndex() {
/*  62 */     return this.refractionIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float r) {
/*  73 */     this.a = r;
/*  74 */     this.b = r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/*  84 */     return this.a;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/*  94 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/*  99 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 109 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 119 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 129 */     this.centreX = (float)centre.getX();
/* 130 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 140 */     return new Point2D.Float(this.centreX, this.centreY);
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 145 */     int width = src.getWidth();
/* 146 */     int height = src.getHeight();
/* 147 */     this.icentreX = width * this.centreX;
/* 148 */     this.icentreY = height * this.centreY;
/*     */     
/* 150 */     if (this.a == 0.0F)
/*     */     {
/* 152 */       this.a = (width / 2);
/*     */     }
/*     */     
/* 155 */     if (this.b == 0.0F)
/*     */     {
/* 157 */       this.b = (height / 2);
/*     */     }
/*     */     
/* 160 */     this.a2 = this.a * this.a;
/* 161 */     this.b2 = this.b * this.b;
/* 162 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 167 */     float dx = x - this.icentreX;
/* 168 */     float dy = y - this.icentreY;
/* 169 */     float x2 = dx * dx;
/* 170 */     float y2 = dy * dy;
/*     */     
/* 172 */     if (y2 >= this.b2 - this.b2 * x2 / this.a2) {
/*     */       
/* 174 */       out[0] = x;
/* 175 */       out[1] = y;
/*     */     }
/*     */     else {
/*     */       
/* 179 */       float rRefraction = 1.0F / this.refractionIndex;
/* 180 */       float z = (float)Math.sqrt(((1.0F - x2 / this.a2 - y2 / this.b2) * this.a * this.b));
/* 181 */       float z2 = z * z;
/* 182 */       float xAngle = (float)Math.acos(dx / Math.sqrt((x2 + z2)));
/* 183 */       float angle1 = 1.5707964F - xAngle;
/* 184 */       float angle2 = (float)Math.asin(Math.sin(angle1) * rRefraction);
/* 185 */       angle2 = 1.5707964F - xAngle - angle2;
/* 186 */       out[0] = x - (float)Math.tan(angle2) * z;
/* 187 */       float yAngle = (float)Math.acos(dy / Math.sqrt((y2 + z2)));
/* 188 */       angle1 = 1.5707964F - yAngle;
/* 189 */       angle2 = (float)Math.asin(Math.sin(angle1) * rRefraction);
/* 190 */       angle2 = 1.5707964F - yAngle - angle2;
/* 191 */       out[1] = y - (float)Math.tan(angle2) * z;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 197 */     return "Distort/Sphere...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SphereFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */