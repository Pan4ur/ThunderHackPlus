/*     */ package com.jhlabs.image;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DisplaceFilter
/*     */   extends TransformFilter
/*     */ {
/*  29 */   private float amount = 1.0F;
/*  30 */   private BufferedImage displacementMap = null;
/*     */ 
/*     */   
/*     */   private int[] xmap;
/*     */ 
/*     */   
/*     */   private int[] ymap;
/*     */ 
/*     */   
/*     */   private int dw;
/*     */   
/*     */   private int dh;
/*     */ 
/*     */   
/*     */   public void setDisplacementMap(BufferedImage displacementMap) {
/*  45 */     this.displacementMap = displacementMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getDisplacementMap() {
/*  55 */     return this.displacementMap;
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
/*  67 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  77 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  82 */     int w = src.getWidth();
/*  83 */     int h = src.getHeight();
/*  84 */     BufferedImage dm = (this.displacementMap != null) ? this.displacementMap : src;
/*  85 */     this.dw = dm.getWidth();
/*  86 */     this.dh = dm.getHeight();
/*  87 */     int[] mapPixels = new int[this.dw * this.dh];
/*  88 */     getRGB(dm, 0, 0, this.dw, this.dh, mapPixels);
/*  89 */     this.xmap = new int[this.dw * this.dh];
/*  90 */     this.ymap = new int[this.dw * this.dh];
/*  91 */     int i = 0;
/*     */     int y;
/*  93 */     for (y = 0; y < this.dh; y++) {
/*     */       
/*  95 */       for (int x = 0; x < this.dw; x++) {
/*     */         
/*  97 */         int rgb = mapPixels[i];
/*  98 */         int r = rgb >> 16 & 0xFF;
/*  99 */         int g = rgb >> 8 & 0xFF;
/* 100 */         int b = rgb & 0xFF;
/* 101 */         mapPixels[i] = (r + g + b) / 8;
/* 102 */         i++;
/*     */       } 
/*     */     } 
/*     */     
/* 106 */     i = 0;
/*     */     
/* 108 */     for (y = 0; y < this.dh; y++) {
/*     */       
/* 110 */       int j1 = (y + this.dh - 1) % this.dh * this.dw;
/* 111 */       int j2 = y * this.dw;
/* 112 */       int j3 = (y + 1) % this.dh * this.dw;
/*     */       
/* 114 */       for (int x = 0; x < this.dw; x++) {
/*     */         
/* 116 */         int k1 = (x + this.dw - 1) % this.dw;
/* 117 */         int k2 = x;
/* 118 */         int k3 = (x + 1) % this.dw;
/* 119 */         this.xmap[i] = mapPixels[k1 + j1] + mapPixels[k1 + j2] + mapPixels[k1 + j3] - mapPixels[k3 + j1] - mapPixels[k3 + j2] - mapPixels[k3 + j3];
/* 120 */         this.ymap[i] = mapPixels[k1 + j3] + mapPixels[k2 + j3] + mapPixels[k3 + j3] - mapPixels[k1 + j1] - mapPixels[k2 + j1] - mapPixels[k3 + j1];
/* 121 */         i++;
/*     */       } 
/*     */     } 
/*     */     
/* 125 */     mapPixels = null;
/* 126 */     dst = super.filter(src, dst);
/* 127 */     this.xmap = this.ymap = null;
/* 128 */     return dst;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 134 */     float nx = x;
/* 135 */     float ny = y;
/* 136 */     int i = y % this.dh * this.dw + x % this.dw;
/* 137 */     out[0] = x + this.amount * this.xmap[i];
/* 138 */     out[1] = y + this.amount * this.ymap[i];
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 143 */     return "Distort/Displace...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DisplaceFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */