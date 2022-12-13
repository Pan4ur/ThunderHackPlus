/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
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
/*     */ public class ShearFilter
/*     */   extends TransformFilter
/*     */ {
/*  24 */   private float xangle = 0.0F;
/*  25 */   private float yangle = 0.0F;
/*  26 */   private float shx = 0.0F;
/*  27 */   private float shy = 0.0F;
/*  28 */   private float xoffset = 0.0F;
/*  29 */   private float yoffset = 0.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean resize = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResize(boolean resize) {
/*  38 */     this.resize = resize;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResize() {
/*  43 */     return this.resize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setXAngle(float xangle) {
/*  48 */     this.xangle = xangle;
/*  49 */     initialize();
/*     */   }
/*     */ 
/*     */   
/*     */   public float getXAngle() {
/*  54 */     return this.xangle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setYAngle(float yangle) {
/*  59 */     this.yangle = yangle;
/*  60 */     initialize();
/*     */   }
/*     */ 
/*     */   
/*     */   public float getYAngle() {
/*  65 */     return this.yangle;
/*     */   }
/*     */ 
/*     */   
/*     */   private void initialize() {
/*  70 */     this.shx = (float)Math.sin(this.xangle);
/*  71 */     this.shy = (float)Math.sin(this.yangle);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle r) {
/*  76 */     float tangent = (float)Math.tan(this.xangle);
/*  77 */     this.xoffset = -r.height * tangent;
/*     */     
/*  79 */     if (tangent < 0.0D)
/*     */     {
/*  81 */       tangent = -tangent;
/*     */     }
/*     */     
/*  84 */     r.width = (int)(r.height * tangent + r.width + 0.999999F);
/*  85 */     tangent = (float)Math.tan(this.yangle);
/*  86 */     this.yoffset = -r.width * tangent;
/*     */     
/*  88 */     if (tangent < 0.0D)
/*     */     {
/*  90 */       tangent = -tangent;
/*     */     }
/*     */     
/*  93 */     r.height = (int)(r.width * tangent + r.height + 0.999999F);
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
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 148 */     out[0] = x + this.xoffset + y * this.shx;
/* 149 */     out[1] = y + this.yoffset + x * this.shy;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 154 */     return "Distort/Shear...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ShearFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */