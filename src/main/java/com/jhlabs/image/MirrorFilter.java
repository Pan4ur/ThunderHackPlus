/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Shape;
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
/*     */ public class MirrorFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  25 */   private float opacity = 1.0F;
/*  26 */   private float centreY = 0.5F;
/*     */ 
/*     */ 
/*     */   
/*     */   private float distance;
/*     */ 
/*     */   
/*     */   private float angle;
/*     */ 
/*     */   
/*     */   private float rotation;
/*     */ 
/*     */   
/*     */   private float gap;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  44 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  54 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  59 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  64 */     return this.distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRotation(float rotation) {
/*  69 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotation() {
/*  74 */     return this.rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGap(float gap) {
/*  79 */     this.gap = gap;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGap() {
/*  84 */     return this.gap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOpacity(float opacity) {
/*  94 */     this.opacity = opacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOpacity() {
/* 104 */     return this.opacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 109 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 114 */     return this.centreY;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 119 */     if (dst == null)
/*     */     {
/* 121 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 124 */     BufferedImage tsrc = src;
/*     */     
/* 126 */     int width = src.getWidth();
/* 127 */     int height = src.getHeight();
/* 128 */     int h = (int)(this.centreY * height);
/* 129 */     int d = (int)(this.gap * height);
/* 130 */     Graphics2D g = dst.createGraphics();
/* 131 */     Shape clip = g.getClip();
/* 132 */     g.clipRect(0, 0, width, h);
/* 133 */     g.drawRenderedImage(src, (AffineTransform)null);
/* 134 */     g.setClip(clip);
/* 135 */     g.clipRect(0, h + d, width, height - h - d);
/* 136 */     g.translate(0, 2 * h + d);
/* 137 */     g.scale(1.0D, -1.0D);
/* 138 */     g.drawRenderedImage(src, (AffineTransform)null);
/* 139 */     g.setPaint(new GradientPaint(0.0F, 0.0F, new Color(1.0F, 0.0F, 0.0F, 0.0F), 0.0F, h, new Color(0.0F, 1.0F, 0.0F, this.opacity)));
/* 140 */     g.setComposite(AlphaComposite.getInstance(6));
/* 141 */     g.fillRect(0, 0, width, h);
/* 142 */     g.setClip(clip);
/* 143 */     g.dispose();
/* 144 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 149 */     return "Effects/Mirror...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MirrorFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */