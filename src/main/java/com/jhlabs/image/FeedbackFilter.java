/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
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
/*     */ public class FeedbackFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  28 */   private float centreX = 0.5F, centreY = 0.5F;
/*     */   private float distance;
/*     */   private float angle;
/*     */   private float rotation;
/*     */   private float zoom;
/*  33 */   private float startAlpha = 1.0F;
/*  34 */   private float endAlpha = 1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int iterations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FeedbackFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FeedbackFilter(float distance, float angle, float rotation, float zoom) {
/*  53 */     this.distance = distance;
/*  54 */     this.angle = angle;
/*  55 */     this.rotation = rotation;
/*  56 */     this.zoom = zoom;
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
/*  67 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  77 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  87 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  97 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRotation(float rotation) {
/* 108 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 119 */     return this.rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setZoom(float zoom) {
/* 129 */     this.zoom = zoom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getZoom() {
/* 139 */     return this.zoom;
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
/*     */   public void setStartAlpha(float startAlpha) {
/* 151 */     this.startAlpha = startAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStartAlpha() {
/* 161 */     return this.startAlpha;
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
/*     */   public void setEndAlpha(float endAlpha) {
/* 173 */     this.endAlpha = endAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getEndAlpha() {
/* 183 */     return this.endAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/* 193 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/* 203 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 213 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 223 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 233 */     this.centreX = (float)centre.getX();
/* 234 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 244 */     return new Point2D.Float(this.centreX, this.centreY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIterations(int iterations) {
/* 255 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIterations() {
/* 265 */     return this.iterations;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 270 */     if (dst == null)
/*     */     {
/* 272 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 275 */     float cx = src.getWidth() * this.centreX;
/* 276 */     float cy = src.getHeight() * this.centreY;
/* 277 */     float imageRadius = (float)Math.sqrt((cx * cx + cy * cy));
/* 278 */     float translateX = (float)(this.distance * Math.cos(this.angle));
/* 279 */     float translateY = (float)(this.distance * -Math.sin(this.angle));
/* 280 */     float scale = (float)Math.exp(this.zoom);
/* 281 */     float rotate = this.rotation;
/*     */     
/* 283 */     if (this.iterations == 0) {
/*     */       
/* 285 */       Graphics2D graphics2D = dst.createGraphics();
/* 286 */       graphics2D.drawRenderedImage(src, (AffineTransform)null);
/* 287 */       graphics2D.dispose();
/* 288 */       return dst;
/*     */     } 
/*     */     
/* 291 */     Graphics2D g = dst.createGraphics();
/* 292 */     g.drawImage(src, (AffineTransform)null, (ImageObserver)null);
/*     */     
/* 294 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/* 296 */       g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 297 */       g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/* 298 */       g.setComposite(AlphaComposite.getInstance(3, ImageMath.lerp(i / (this.iterations - 1), this.startAlpha, this.endAlpha)));
/* 299 */       g.translate((cx + translateX), (cy + translateY));
/* 300 */       g.scale(scale, scale);
/*     */       
/* 302 */       if (this.rotation != 0.0F)
/*     */       {
/* 304 */         g.rotate(rotate);
/*     */       }
/*     */       
/* 307 */       g.translate(-cx, -cy);
/* 308 */       g.drawImage(src, (AffineTransform)null, (ImageObserver)null);
/*     */     } 
/*     */     
/* 311 */     g.dispose();
/* 312 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 317 */     return "Effects/Feedback...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FeedbackFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */