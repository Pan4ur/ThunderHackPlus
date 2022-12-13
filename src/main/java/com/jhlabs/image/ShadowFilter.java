/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BandCombineOp;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
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
/*     */ public class ShadowFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  28 */   private float radius = 5.0F;
/*  29 */   private float angle = 4.712389F;
/*  30 */   private float distance = 5.0F;
/*  31 */   private float opacity = 0.5F;
/*     */   private boolean addMargins = false;
/*     */   private boolean shadowOnly = false;
/*  34 */   private int shadowColor = -16777216;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ShadowFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ShadowFilter(float radius, float xOffset, float yOffset, float opacity) {
/*  52 */     this.radius = radius;
/*  53 */     this.angle = (float)Math.atan2(yOffset, xOffset);
/*  54 */     this.distance = (float)Math.sqrt((xOffset * xOffset + yOffset * yOffset));
/*  55 */     this.opacity = opacity;
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
/*  66 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  76 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  86 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  96 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/* 106 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 116 */     return this.radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOpacity(float opacity) {
/* 126 */     this.opacity = opacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOpacity() {
/* 136 */     return this.opacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShadowColor(int shadowColor) {
/* 146 */     this.shadowColor = shadowColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getShadowColor() {
/* 156 */     return this.shadowColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddMargins(boolean addMargins) {
/* 166 */     this.addMargins = addMargins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAddMargins() {
/* 176 */     return this.addMargins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShadowOnly(boolean shadowOnly) {
/* 186 */     this.shadowOnly = shadowOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getShadowOnly() {
/* 196 */     return this.shadowOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public Rectangle2D getBounds2D(BufferedImage src) {
/* 201 */     Rectangle r = new Rectangle(0, 0, src.getWidth(), src.getHeight());
/*     */     
/* 203 */     if (this.addMargins) {
/*     */       
/* 205 */       float xOffset = this.distance * (float)Math.cos(this.angle);
/* 206 */       float yOffset = -this.distance * (float)Math.sin(this.angle);
/* 207 */       r.width += (int)(Math.abs(xOffset) + 2.0F * this.radius);
/* 208 */       r.height += (int)(Math.abs(yOffset) + 2.0F * this.radius);
/*     */     } 
/*     */     
/* 211 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
/* 216 */     if (dstPt == null)
/*     */     {
/* 218 */       dstPt = new Point2D.Double();
/*     */     }
/*     */     
/* 221 */     if (this.addMargins) {
/*     */       
/* 223 */       float xOffset = this.distance * (float)Math.cos(this.angle);
/* 224 */       float yOffset = -this.distance * (float)Math.sin(this.angle);
/* 225 */       float topShadow = Math.max(0.0F, this.radius - yOffset);
/* 226 */       float leftShadow = Math.max(0.0F, this.radius - xOffset);
/* 227 */       dstPt.setLocation(srcPt.getX() + leftShadow, srcPt.getY() + topShadow);
/*     */     }
/*     */     else {
/*     */       
/* 231 */       dstPt.setLocation(srcPt.getX(), srcPt.getY());
/*     */     } 
/*     */     
/* 234 */     return dstPt;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 239 */     int width = src.getWidth();
/* 240 */     int height = src.getHeight();
/* 241 */     float xOffset = this.distance * (float)Math.cos(this.angle);
/* 242 */     float yOffset = -this.distance * (float)Math.sin(this.angle);
/*     */     
/* 244 */     if (dst == null)
/*     */     {
/* 246 */       if (this.addMargins) {
/*     */         
/* 248 */         ColorModel cm = src.getColorModel();
/* 249 */         dst = new BufferedImage(cm, cm.createCompatibleWritableRaster(src.getWidth() + (int)(Math.abs(xOffset) + this.radius), src.getHeight() + (int)(Math.abs(yOffset) + this.radius)), cm.isAlphaPremultiplied(), null);
/*     */       }
/*     */       else {
/*     */         
/* 253 */         dst = createCompatibleDestImage(src, null);
/*     */       } 
/*     */     }
/*     */     
/* 257 */     float shadowR = (this.shadowColor >> 16 & 0xFF) / 255.0F;
/* 258 */     float shadowG = (this.shadowColor >> 8 & 0xFF) / 255.0F;
/* 259 */     float shadowB = (this.shadowColor & 0xFF) / 255.0F;
/*     */     
/* 261 */     float[][] extractAlpha = { { 0.0F, 0.0F, 0.0F, shadowR }, { 0.0F, 0.0F, 0.0F, shadowG }, { 0.0F, 0.0F, 0.0F, shadowB }, { 0.0F, 0.0F, 0.0F, this.opacity } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 268 */     BufferedImage shadow = new BufferedImage(width, height, 2);
/* 269 */     (new BandCombineOp(extractAlpha, null)).filter(src.getRaster(), shadow.getRaster());
/* 270 */     shadow = (new GaussianFilter(this.radius)).filter(shadow, null);
/* 271 */     Graphics2D g = dst.createGraphics();
/* 272 */     g.setComposite(AlphaComposite.getInstance(3, this.opacity));
/*     */     
/* 274 */     if (this.addMargins) {
/*     */       
/* 276 */       float radius2 = this.radius / 2.0F;
/* 277 */       float topShadow = Math.max(0.0F, this.radius - yOffset);
/* 278 */       float leftShadow = Math.max(0.0F, this.radius - xOffset);
/* 279 */       g.translate(leftShadow, topShadow);
/*     */     } 
/*     */     
/* 282 */     g.drawRenderedImage(shadow, AffineTransform.getTranslateInstance(xOffset, yOffset));
/*     */     
/* 284 */     if (!this.shadowOnly) {
/*     */       
/* 286 */       g.setComposite(AlphaComposite.SrcOver);
/* 287 */       g.drawRenderedImage(src, (AffineTransform)null);
/*     */     } 
/*     */     
/* 290 */     g.dispose();
/* 291 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 296 */     return "Stylize/Drop Shadow...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ShadowFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */