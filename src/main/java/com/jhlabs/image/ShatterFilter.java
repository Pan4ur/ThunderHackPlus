/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
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
/*     */ public class ShatterFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  26 */   private float centreX = 0.5F, centreY = 0.5F;
/*     */   private float distance;
/*     */   private float transition;
/*     */   private float rotation;
/*     */   private float zoom;
/*  31 */   private float startAlpha = 1.0F;
/*  32 */   private float endAlpha = 1.0F;
/*  33 */   private int iterations = 5;
/*     */ 
/*     */ 
/*     */   
/*     */   private int tile;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTransition(float transition) {
/*  42 */     this.transition = transition;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTransition() {
/*  47 */     return this.transition;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDistance(float distance) {
/*  52 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDistance() {
/*  57 */     return this.distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRotation(float rotation) {
/*  62 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotation() {
/*  67 */     return this.rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setZoom(float zoom) {
/*  72 */     this.zoom = zoom;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getZoom() {
/*  77 */     return this.zoom;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStartAlpha(float startAlpha) {
/*  82 */     this.startAlpha = startAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStartAlpha() {
/*  87 */     return this.startAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEndAlpha(float endAlpha) {
/*  92 */     this.endAlpha = endAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getEndAlpha() {
/*  97 */     return this.endAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCentreX(float centreX) {
/* 102 */     this.centreX = centreX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCentreX() {
/* 107 */     return this.centreX;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCentreY(float centreY) {
/* 112 */     this.centreY = centreY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCentreY() {
/* 117 */     return this.centreY;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCentre(Point2D centre) {
/* 122 */     this.centreX = (float)centre.getX();
/* 123 */     this.centreY = (float)centre.getY();
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getCentre() {
/* 128 */     return new Point2D.Float(this.centreX, this.centreY);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIterations(int iterations) {
/* 133 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIterations() {
/* 138 */     return this.iterations;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTile(int tile) {
/* 143 */     this.tile = tile;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTile() {
/* 148 */     return this.tile;
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
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 160 */     if (dst == null)
/*     */     {
/* 162 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 165 */     float width = src.getWidth();
/* 166 */     float height = src.getHeight();
/* 167 */     float cx = src.getWidth() * this.centreX;
/* 168 */     float cy = src.getHeight() * this.centreY;
/* 169 */     float imageRadius = (float)Math.sqrt((cx * cx + cy * cy));
/*     */     
/* 171 */     int numTiles = this.iterations * this.iterations;
/* 172 */     Tile[] shapes = new Tile[numTiles];
/* 173 */     float[] rx = new float[numTiles];
/* 174 */     float[] ry = new float[numTiles];
/* 175 */     float[] rz = new float[numTiles];
/* 176 */     Graphics2D g = dst.createGraphics();
/*     */     
/* 178 */     Random random = new Random(0L);
/* 179 */     float lastx = 0.0F, lasty = 0.0F;
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
/* 205 */     for (int y = 0; y < this.iterations; y++) {
/*     */       
/* 207 */       int y1 = (int)height * y / this.iterations;
/* 208 */       int y2 = (int)height * (y + 1) / this.iterations;
/*     */       
/* 210 */       for (int x = 0; x < this.iterations; x++) {
/*     */         
/* 212 */         int j = y * this.iterations + x;
/* 213 */         int x1 = (int)width * x / this.iterations;
/* 214 */         int x2 = (int)width * (x + 1) / this.iterations;
/* 215 */         rx[j] = this.tile * random.nextFloat();
/* 216 */         ry[j] = this.tile * random.nextFloat();
/* 217 */         rx[j] = 0.0F;
/* 218 */         ry[j] = 0.0F;
/* 219 */         rz[j] = this.tile * (2.0F * random.nextFloat() - 1.0F);
/* 220 */         Shape p = new Rectangle(x1, y1, x2 - x1, y2 - y1);
/* 221 */         shapes[j] = new Tile();
/* 222 */         (shapes[j]).shape = p;
/* 223 */         (shapes[j]).x = (x1 + x2) * 0.5F;
/* 224 */         (shapes[j]).y = (y1 + y2) * 0.5F;
/* 225 */         (shapes[j]).vx = width - cx - x;
/* 226 */         (shapes[j]).vy = height - cy - y;
/* 227 */         (shapes[j]).w = (x2 - x1);
/* 228 */         (shapes[j]).h = (y2 - y1);
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     for (int i = 0; i < numTiles; i++) {
/*     */       
/* 234 */       float h = i / numTiles;
/* 235 */       double angle = (h * 2.0F) * Math.PI;
/* 236 */       float x = this.transition * width * (float)Math.cos(angle);
/* 237 */       float f1 = this.transition * height * (float)Math.sin(angle);
/* 238 */       Tile tile = shapes[i];
/* 239 */       Rectangle r = tile.shape.getBounds();
/* 240 */       AffineTransform t = g.getTransform();
/* 241 */       x = tile.x + this.transition * tile.vx;
/* 242 */       f1 = tile.y + this.transition * tile.vy;
/* 243 */       g.translate(x, f1);
/*     */       
/* 245 */       g.rotate((this.transition * rz[i]));
/*     */ 
/*     */       
/* 248 */       g.setColor(Color.getHSBColor(h, 1.0F, 1.0F));
/* 249 */       Shape clip = g.getClip();
/* 250 */       g.clip(tile.shape);
/* 251 */       g.drawImage(src, 0, 0, (ImageObserver)null);
/* 252 */       g.setClip(clip);
/* 253 */       g.setTransform(t);
/*     */     } 
/*     */     
/* 256 */     g.dispose();
/* 257 */     return dst;
/*     */   }
/*     */   static class Tile {
/*     */     float x; float y; float vx; float vy; float w; float h; float rotation; Shape shape; }
/*     */   public String toString() {
/* 262 */     return "Transition/Shatter...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ShatterFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */