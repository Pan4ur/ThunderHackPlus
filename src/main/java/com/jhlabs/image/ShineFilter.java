/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.composite.AddComposite;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ public class ShineFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  26 */   private float radius = 5.0F;
/*  27 */   private float angle = 5.4977875F;
/*  28 */   private float distance = 5.0F;
/*  29 */   private float bevel = 0.5F;
/*     */   private boolean shadowOnly = false;
/*  31 */   private int shineColor = -1;
/*  32 */   private float brightness = 0.2F;
/*  33 */   private float softness = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  41 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  46 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  51 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  56 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/*  65 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/*  74 */     return this.radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBevel(float bevel) {
/*  79 */     this.bevel = bevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBevel() {
/*  84 */     return this.bevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShineColor(int shineColor) {
/*  89 */     this.shineColor = shineColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getShineColor() {
/*  94 */     return this.shineColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShadowOnly(boolean shadowOnly) {
/*  99 */     this.shadowOnly = shadowOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getShadowOnly() {
/* 104 */     return this.shadowOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBrightness(float brightness) {
/* 109 */     this.brightness = brightness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBrightness() {
/* 114 */     return this.brightness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSoftness(float softness) {
/* 119 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/* 124 */     return this.softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 129 */     int width = src.getWidth();
/* 130 */     int height = src.getHeight();
/*     */     
/* 132 */     if (dst == null)
/*     */     {
/* 134 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 137 */     float xOffset = this.distance * (float)Math.cos(this.angle);
/* 138 */     float yOffset = -this.distance * (float)Math.sin(this.angle);
/* 139 */     BufferedImage matte = new BufferedImage(width, height, 2);
/* 140 */     ErodeAlphaFilter s = new ErodeAlphaFilter(this.bevel * 10.0F, 0.75F, 0.1F);
/* 141 */     matte = s.filter(src, null);
/* 142 */     BufferedImage shineLayer = new BufferedImage(width, height, 2);
/* 143 */     Graphics2D g = shineLayer.createGraphics();
/* 144 */     g.setColor(new Color(this.shineColor));
/* 145 */     g.fillRect(0, 0, width, height);
/* 146 */     g.setComposite(AlphaComposite.DstIn);
/* 147 */     g.drawRenderedImage(matte, (AffineTransform)null);
/* 148 */     g.setComposite(AlphaComposite.DstOut);
/* 149 */     g.translate(xOffset, yOffset);
/* 150 */     g.drawRenderedImage(matte, (AffineTransform)null);
/* 151 */     g.dispose();
/* 152 */     shineLayer = (new GaussianFilter(this.radius)).filter(shineLayer, null);
/* 153 */     shineLayer = (new RescaleFilter(3.0F * this.brightness)).filter(shineLayer, shineLayer);
/* 154 */     g = dst.createGraphics();
/* 155 */     g.drawRenderedImage(src, (AffineTransform)null);
/* 156 */     g.setComposite((Composite)new AddComposite(1.0F));
/* 157 */     g.drawRenderedImage(shineLayer, (AffineTransform)null);
/* 158 */     g.dispose();
/* 159 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 164 */     return "Stylize/Shine...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ShineFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */