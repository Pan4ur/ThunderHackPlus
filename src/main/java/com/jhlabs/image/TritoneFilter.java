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
/*     */ public class TritoneFilter
/*     */   extends PointFilter
/*     */ {
/*  28 */   private int shadowColor = -16777216;
/*  29 */   private int midColor = -7829368;
/*  30 */   private int highColor = -1;
/*     */   
/*     */   private int[] lut;
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  35 */     this.lut = new int[256];
/*     */     int i;
/*  37 */     for (i = 0; i < 128; i++) {
/*     */       
/*  39 */       float t = i / 127.0F;
/*  40 */       this.lut[i] = ImageMath.mixColors(t, this.shadowColor, this.midColor);
/*     */     } 
/*     */     
/*  43 */     for (i = 128; i < 256; i++) {
/*     */       
/*  45 */       float t = (i - 127) / 128.0F;
/*  46 */       this.lut[i] = ImageMath.mixColors(t, this.midColor, this.highColor);
/*     */     } 
/*     */     
/*  49 */     dst = super.filter(src, dst);
/*  50 */     this.lut = null;
/*  51 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*  56 */     return this.lut[PixelUtils.brightness(rgb)];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShadowColor(int shadowColor) {
/*  66 */     this.shadowColor = shadowColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getShadowColor() {
/*  76 */     return this.shadowColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMidColor(int midColor) {
/*  86 */     this.midColor = midColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMidColor() {
/*  96 */     return this.midColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHighColor(int highColor) {
/* 106 */     this.highColor = highColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHighColor() {
/* 116 */     return this.highColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return "Colors/Tritone...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TritoneFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */