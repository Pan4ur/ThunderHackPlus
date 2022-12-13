/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TileImageFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private int tileWidth;
/*     */   private int tileHeight;
/*     */   
/*     */   public TileImageFilter() {
/*  37 */     this(32, 32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TileImageFilter(int width, int height) {
/*  47 */     this.width = width;
/*  48 */     this.height = height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWidth(int width) {
/*  58 */     this.width = width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  68 */     return this.width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeight(int height) {
/*  78 */     this.height = height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  88 */     return this.height;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  93 */     int tileWidth = src.getWidth();
/*  94 */     int tileHeight = src.getHeight();
/*     */     
/*  96 */     if (dst == null) {
/*     */       
/*  98 */       ColorModel dstCM = src.getColorModel();
/*  99 */       dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
/*     */     } 
/*     */     
/* 102 */     Graphics2D g = dst.createGraphics();
/*     */     int y;
/* 104 */     for (y = 0; y < this.height; y += tileHeight) {
/*     */       int x;
/* 106 */       for (x = 0; x < this.width; x += tileWidth)
/*     */       {
/* 108 */         g.drawImage(src, (BufferedImageOp)null, x, y);
/*     */       }
/*     */     } 
/*     */     
/* 112 */     g.dispose();
/* 113 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return "Tile";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TileImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */