/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ public class MotionBlurFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  27 */   private float angle = 0.0F;
/*  28 */   private float falloff = 1.0F;
/*  29 */   private float distance = 1.0F;
/*  30 */   private float zoom = 0.0F;
/*  31 */   private float rotation = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean wrapEdges = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean premultiplyAlpha = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MotionBlurFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MotionBlurFilter(float distance, float angle, float rotation, float zoom) {
/*  51 */     this.distance = distance;
/*  52 */     this.angle = angle;
/*  53 */     this.rotation = rotation;
/*  54 */     this.zoom = zoom;
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
/*  65 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  75 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  85 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  95 */     return this.distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRotation(float rotation) {
/* 105 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 115 */     return this.rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setZoom(float zoom) {
/* 125 */     this.zoom = zoom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getZoom() {
/* 135 */     return this.zoom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWrapEdges(boolean wrapEdges) {
/* 145 */     this.wrapEdges = wrapEdges;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getWrapEdges() {
/* 155 */     return this.wrapEdges;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPremultiplyAlpha(boolean premultiplyAlpha) {
/* 165 */     this.premultiplyAlpha = premultiplyAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getPremultiplyAlpha() {
/* 175 */     return this.premultiplyAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 180 */     int width = src.getWidth();
/* 181 */     int height = src.getHeight();
/*     */     
/* 183 */     if (dst == null)
/*     */     {
/* 185 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 188 */     int[] inPixels = new int[width * height];
/* 189 */     int[] outPixels = new int[width * height];
/* 190 */     getRGB(src, 0, 0, width, height, inPixels);
/* 191 */     float sinAngle = (float)Math.sin(this.angle);
/* 192 */     float cosAngle = (float)Math.cos(this.angle);
/*     */     
/* 194 */     int cx = width / 2;
/* 195 */     int cy = height / 2;
/* 196 */     int index = 0;
/* 197 */     float imageRadius = (float)Math.sqrt((cx * cx + cy * cy));
/* 198 */     float translateX = (float)(this.distance * Math.cos(this.angle));
/* 199 */     float translateY = (float)(this.distance * -Math.sin(this.angle));
/* 200 */     float maxDistance = this.distance + Math.abs(this.rotation * imageRadius) + this.zoom * imageRadius;
/* 201 */     int repetitions = (int)maxDistance;
/* 202 */     AffineTransform t = new AffineTransform();
/* 203 */     Point2D.Float p = new Point2D.Float();
/*     */     
/* 205 */     if (this.premultiplyAlpha)
/*     */     {
/* 207 */       ImageMath.premultiply(inPixels, 0, inPixels.length);
/*     */     }
/*     */     
/* 210 */     for (int y = 0; y < height; y++) {
/*     */       
/* 212 */       for (int x = 0; x < width; x++) {
/*     */         
/* 214 */         int a = 0, r = 0, g = 0, b = 0;
/* 215 */         int count = 0;
/*     */         
/* 217 */         for (int i = 0; i < repetitions; i++) {
/*     */           
/* 219 */           int newX = x, newY = y;
/* 220 */           float f = i / repetitions;
/* 221 */           p.x = x;
/* 222 */           p.y = y;
/* 223 */           t.setToIdentity();
/* 224 */           t.translate((cx + f * translateX), (cy + f * translateY));
/* 225 */           float s = 1.0F - this.zoom * f;
/* 226 */           t.scale(s, s);
/*     */           
/* 228 */           if (this.rotation != 0.0F)
/*     */           {
/* 230 */             t.rotate((-this.rotation * f));
/*     */           }
/*     */           
/* 233 */           t.translate(-cx, -cy);
/* 234 */           t.transform(p, p);
/* 235 */           newX = (int)p.x;
/* 236 */           newY = (int)p.y;
/*     */           
/* 238 */           if (newX < 0 || newX >= width)
/*     */           {
/* 240 */             if (this.wrapEdges) {
/*     */               
/* 242 */               newX = ImageMath.mod(newX, width);
/*     */             } else {
/*     */               break;
/*     */             } 
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 250 */           if (newY < 0 || newY >= height)
/*     */           {
/* 252 */             if (this.wrapEdges) {
/*     */               
/* 254 */               newY = ImageMath.mod(newY, height);
/*     */             } else {
/*     */               break;
/*     */             } 
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 262 */           count++;
/* 263 */           int rgb = inPixels[newY * width + newX];
/* 264 */           a += rgb >> 24 & 0xFF;
/* 265 */           r += rgb >> 16 & 0xFF;
/* 266 */           g += rgb >> 8 & 0xFF;
/* 267 */           b += rgb & 0xFF;
/*     */         } 
/*     */         
/* 270 */         if (count == 0) {
/*     */           
/* 272 */           outPixels[index] = inPixels[index];
/*     */         }
/*     */         else {
/*     */           
/* 276 */           a = PixelUtils.clamp(a / count);
/* 277 */           r = PixelUtils.clamp(r / count);
/* 278 */           g = PixelUtils.clamp(g / count);
/* 279 */           b = PixelUtils.clamp(b / count);
/* 280 */           outPixels[index] = a << 24 | r << 16 | g << 8 | b;
/*     */         } 
/*     */         
/* 283 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 287 */     if (this.premultiplyAlpha)
/*     */     {
/* 289 */       ImageMath.unpremultiply(outPixels, 0, inPixels.length);
/*     */     }
/*     */     
/* 292 */     setRGB(dst, 0, 0, width, height, outPixels);
/* 293 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 298 */     return "Blur/Motion Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MotionBlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */