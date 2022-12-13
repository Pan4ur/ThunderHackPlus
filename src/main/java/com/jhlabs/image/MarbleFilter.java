/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Noise;
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
/*     */ public class MarbleFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   private float[] sinTable;
/*     */   private float[] cosTable;
/*  29 */   private float xScale = 4.0F;
/*  30 */   private float yScale = 4.0F;
/*  31 */   private float amount = 1.0F;
/*  32 */   private float turbulence = 1.0F;
/*     */ 
/*     */   
/*     */   public MarbleFilter() {
/*  36 */     setEdgeAction(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setXScale(float xScale) {
/*  46 */     this.xScale = xScale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getXScale() {
/*  56 */     return this.xScale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setYScale(float yScale) {
/*  66 */     this.yScale = yScale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getYScale() {
/*  76 */     return this.yScale;
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
/*  88 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  98 */     return this.amount;
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
/*     */   public void setTurbulence(float turbulence) {
/* 110 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/* 120 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */   
/*     */   private void initialize() {
/* 125 */     this.sinTable = new float[256];
/* 126 */     this.cosTable = new float[256];
/*     */     
/* 128 */     for (int i = 0; i < 256; i++) {
/*     */       
/* 130 */       float angle = 6.2831855F * i / 256.0F * this.turbulence;
/* 131 */       this.sinTable[i] = (float)(-this.yScale * Math.sin(angle));
/* 132 */       this.cosTable[i] = (float)(this.yScale * Math.cos(angle));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private int displacementMap(int x, int y) {
/* 138 */     return PixelUtils.clamp((int)(127.0F * (1.0F + Noise.noise2(x / this.xScale, y / this.xScale))));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 143 */     int displacement = displacementMap(x, y);
/* 144 */     out[0] = x + this.sinTable[displacement];
/* 145 */     out[1] = y + this.cosTable[displacement];
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 150 */     initialize();
/* 151 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 156 */     return "Distort/Marble...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MarbleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */