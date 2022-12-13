/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.WritableRaster;
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
/*     */ public class KeyFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  29 */   private float hTolerance = 0.0F;
/*  30 */   private float sTolerance = 0.0F;
/*  31 */   private float bTolerance = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage destination;
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage cleanImage;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHTolerance(float hTolerance) {
/*  46 */     this.hTolerance = hTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getHTolerance() {
/*  56 */     return this.hTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSTolerance(float sTolerance) {
/*  66 */     this.sTolerance = sTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getSTolerance() {
/*  76 */     return this.sTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBTolerance(float bTolerance) {
/*  86 */     this.bTolerance = bTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getBTolerance() {
/*  96 */     return this.bTolerance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestination(BufferedImage destination) {
/* 106 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getDestination() {
/* 116 */     return this.destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCleanImage(BufferedImage cleanImage) {
/* 126 */     this.cleanImage = cleanImage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getCleanImage() {
/* 136 */     return this.cleanImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 141 */     int width = src.getWidth();
/* 142 */     int height = src.getHeight();
/* 143 */     int type = src.getType();
/* 144 */     WritableRaster srcRaster = src.getRaster();
/*     */     
/* 146 */     if (dst == null)
/*     */     {
/* 148 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 151 */     WritableRaster dstRaster = dst.getRaster();
/*     */     
/* 153 */     if (this.destination != null && this.cleanImage != null) {
/*     */       
/* 155 */       float[] hsb1 = null;
/* 156 */       float[] hsb2 = null;
/* 157 */       int[] inPixels = null;
/* 158 */       int[] outPixels = null;
/* 159 */       int[] cleanPixels = null;
/*     */       
/* 161 */       for (int y = 0; y < height; y++) {
/*     */         
/* 163 */         inPixels = getRGB(src, 0, y, width, 1, inPixels);
/* 164 */         outPixels = getRGB(this.destination, 0, y, width, 1, outPixels);
/* 165 */         cleanPixels = getRGB(this.cleanImage, 0, y, width, 1, cleanPixels);
/*     */         
/* 167 */         for (int x = 0; x < width; x++) {
/*     */           
/* 169 */           int rgb1 = inPixels[x];
/* 170 */           int out = outPixels[x];
/* 171 */           int rgb2 = cleanPixels[x];
/* 172 */           int r1 = rgb1 >> 16 & 0xFF;
/* 173 */           int g1 = rgb1 >> 8 & 0xFF;
/* 174 */           int b1 = rgb1 & 0xFF;
/* 175 */           int r2 = rgb2 >> 16 & 0xFF;
/* 176 */           int g2 = rgb2 >> 8 & 0xFF;
/* 177 */           int b2 = rgb2 & 0xFF;
/* 178 */           hsb1 = Color.RGBtoHSB(r1, b1, g1, hsb1);
/* 179 */           hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 184 */           if (Math.abs(hsb1[0] - hsb2[0]) < this.hTolerance && Math.abs(hsb1[1] - hsb2[1]) < this.sTolerance && Math.abs(hsb1[2] - hsb2[2]) < this.bTolerance) {
/*     */             
/* 186 */             inPixels[x] = out;
/*     */           }
/*     */           else {
/*     */             
/* 190 */             inPixels[x] = rgb1;
/*     */           } 
/*     */         } 
/*     */         
/* 194 */         setRGB(dst, 0, y, width, 1, inPixels);
/*     */       } 
/*     */     } 
/*     */     
/* 198 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 203 */     return "Keying/Key...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\KeyFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */