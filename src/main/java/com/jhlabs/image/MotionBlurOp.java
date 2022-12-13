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
/*     */ public class MotionBlurOp
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  28 */   private float centreX = 0.5F, centreY = 0.5F;
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
/*     */   private float zoom;
/*     */ 
/*     */ 
/*     */   
/*     */   public MotionBlurOp() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public MotionBlurOp(float distance, float angle, float rotation, float zoom) {
/*  50 */     this.distance = distance;
/*  51 */     this.angle = angle;
/*  52 */     this.rotation = rotation;
/*  53 */     this.zoom = zoom;
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
/*  64 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  74 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  84 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  94 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRotation(float rotation) {
/* 104 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 114 */     return this.rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setZoom(float zoom) {
/* 124 */     this.zoom = zoom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getZoom() {
/* 134 */     return this.zoom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/* 144 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/* 154 */     return this.centreX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 164 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 174 */     return this.centreY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 184 */     this.centreX = (float)centre.getX();
/* 185 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 195 */     return new Point2D.Float(this.centreX, this.centreY);
/*     */   }
/*     */ 
/*     */   
/*     */   private int log2(int n) {
/* 200 */     int m = 1;
/* 201 */     int log2n = 0;
/*     */     
/* 203 */     while (m < n) {
/*     */       
/* 205 */       m *= 2;
/* 206 */       log2n++;
/*     */     } 
/*     */     
/* 209 */     return log2n;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 214 */     if (dst == null)
/*     */     {
/* 216 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 219 */     BufferedImage tsrc = src;
/* 220 */     float cx = src.getWidth() * this.centreX;
/* 221 */     float cy = src.getHeight() * this.centreY;
/* 222 */     float imageRadius = (float)Math.sqrt((cx * cx + cy * cy));
/* 223 */     float translateX = (float)(this.distance * Math.cos(this.angle));
/* 224 */     float translateY = (float)(this.distance * -Math.sin(this.angle));
/* 225 */     float scale = this.zoom;
/* 226 */     float rotate = this.rotation;
/* 227 */     float maxDistance = this.distance + Math.abs(this.rotation * imageRadius) + this.zoom * imageRadius;
/* 228 */     int steps = log2((int)maxDistance);
/* 229 */     translateX /= maxDistance;
/* 230 */     translateY /= maxDistance;
/* 231 */     scale /= maxDistance;
/* 232 */     rotate /= maxDistance;
/*     */     
/* 234 */     if (steps == 0) {
/*     */       
/* 236 */       Graphics2D g = dst.createGraphics();
/* 237 */       g.drawRenderedImage(src, (AffineTransform)null);
/* 238 */       g.dispose();
/* 239 */       return dst;
/*     */     } 
/*     */     
/* 242 */     BufferedImage tmp = createCompatibleDestImage(src, null);
/*     */     
/* 244 */     for (int i = 0; i < steps; i++) {
/*     */       
/* 246 */       Graphics2D g = tmp.createGraphics();
/* 247 */       g.drawImage(tsrc, (AffineTransform)null, (ImageObserver)null);
/* 248 */       g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 249 */       g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/* 250 */       g.setComposite(AlphaComposite.getInstance(3, 0.5F));
/* 251 */       g.translate((cx + translateX), (cy + translateY));
/* 252 */       g.scale(1.0001D + scale, 1.0001D + scale);
/*     */       
/* 254 */       if (this.rotation != 0.0F)
/*     */       {
/* 256 */         g.rotate(rotate);
/*     */       }
/*     */       
/* 259 */       g.translate(-cx, -cy);
/* 260 */       g.drawImage(dst, (AffineTransform)null, (ImageObserver)null);
/* 261 */       g.dispose();
/* 262 */       BufferedImage ti = dst;
/* 263 */       dst = tmp;
/* 264 */       tmp = ti;
/* 265 */       tsrc = dst;
/* 266 */       translateX *= 2.0F;
/* 267 */       translateY *= 2.0F;
/* 268 */       scale *= 2.0F;
/* 269 */       rotate *= 2.0F;
/*     */     } 
/*     */     
/* 272 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 277 */     return "Blur/Faster Motion Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MotionBlurOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */