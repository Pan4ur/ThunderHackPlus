/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class ScratchFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  26 */   private float density = 0.1F;
/*     */   private float angle;
/*  28 */   private float angleVariation = 1.0F;
/*  29 */   private float width = 0.5F;
/*  30 */   private float length = 0.5F;
/*  31 */   private int color = -1;
/*  32 */   private int seed = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  40 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  45 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAngleVariation(float angleVariation) {
/*  50 */     this.angleVariation = angleVariation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngleVariation() {
/*  55 */     return this.angleVariation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDensity(float density) {
/*  60 */     this.density = density;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDensity() {
/*  65 */     return this.density;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLength(float length) {
/*  70 */     this.length = length;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLength() {
/*  75 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWidth(float width) {
/*  80 */     this.width = width;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWidth() {
/*  85 */     return this.width;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColor(int color) {
/*  90 */     this.color = color;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColor() {
/*  95 */     return this.color;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSeed(int seed) {
/* 100 */     this.seed = seed;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSeed() {
/* 105 */     return this.seed;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 110 */     if (dst == null)
/*     */     {
/* 112 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 115 */     int width = src.getWidth();
/* 116 */     int height = src.getHeight();
/* 117 */     int numScratches = (int)(this.density * width * height / 100.0F);
/* 118 */     float l = this.length * width;
/* 119 */     Random random = new Random(this.seed);
/* 120 */     Graphics2D g = dst.createGraphics();
/* 121 */     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 122 */     g.setColor(new Color(this.color));
/* 123 */     g.setStroke(new BasicStroke(this.width));
/*     */     
/* 125 */     for (int i = 0; i < numScratches; i++) {
/*     */       
/* 127 */       float x = width * random.nextFloat();
/* 128 */       float y = height * random.nextFloat();
/* 129 */       float a = this.angle + 6.2831855F * this.angleVariation * (random.nextFloat() - 0.5F);
/* 130 */       float s = (float)Math.sin(a) * l;
/* 131 */       float c = (float)Math.cos(a) * l;
/* 132 */       float x1 = x - c;
/* 133 */       float y1 = y - s;
/* 134 */       float x2 = x + c;
/* 135 */       float y2 = y + s;
/* 136 */       g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
/*     */     } 
/*     */     
/* 139 */     g.dispose();
/* 140 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 145 */     return "Render/Scratches...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ScratchFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */