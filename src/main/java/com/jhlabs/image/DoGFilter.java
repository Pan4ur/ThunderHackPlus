/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.composite.SubtractComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Graphics2D;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DoGFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  29 */   private float radius1 = 1.0F;
/*  30 */   private float radius2 = 2.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean normalize = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean invert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius1(float radius1) {
/*  47 */     this.radius1 = radius1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius1() {
/*  57 */     return this.radius1;
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
/*     */   public void setRadius2(float radius2) {
/*  69 */     this.radius2 = radius2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius2() {
/*  79 */     return this.radius2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNormalize(boolean normalize) {
/*  84 */     this.normalize = normalize;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getNormalize() {
/*  89 */     return this.normalize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInvert(boolean invert) {
/*  94 */     this.invert = invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getInvert() {
/*  99 */     return this.invert;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 104 */     int width = src.getWidth();
/* 105 */     int height = src.getHeight();
/* 106 */     BufferedImage image1 = (new BoxBlurFilter(this.radius1, this.radius1, 3)).filter(src, null);
/* 107 */     BufferedImage image2 = (new BoxBlurFilter(this.radius2, this.radius2, 3)).filter(src, null);
/* 108 */     Graphics2D g2d = image2.createGraphics();
/* 109 */     g2d.setComposite((Composite)new SubtractComposite(1.0F));
/* 110 */     g2d.drawImage(image1, 0, 0, (ImageObserver)null);
/* 111 */     g2d.dispose();
/*     */     
/* 113 */     if (this.normalize && this.radius1 != this.radius2) {
/*     */       
/* 115 */       int[] pixels = null;
/* 116 */       int max = 0;
/*     */       int y;
/* 118 */       for (y = 0; y < height; y++) {
/*     */         
/* 120 */         pixels = getRGB(image2, 0, y, width, 1, pixels);
/*     */         
/* 122 */         for (int x = 0; x < width; x++) {
/*     */           
/* 124 */           int rgb = pixels[x];
/* 125 */           int r = rgb >> 16 & 0xFF;
/* 126 */           int g = rgb >> 8 & 0xFF;
/* 127 */           int b = rgb & 0xFF;
/*     */           
/* 129 */           if (r > max)
/*     */           {
/* 131 */             max = r;
/*     */           }
/*     */           
/* 134 */           if (g > max)
/*     */           {
/* 136 */             max = g;
/*     */           }
/*     */           
/* 139 */           if (b > max)
/*     */           {
/* 141 */             max = b;
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 146 */       for (y = 0; y < height; y++) {
/*     */         
/* 148 */         pixels = getRGB(image2, 0, y, width, 1, pixels);
/*     */         
/* 150 */         for (int x = 0; x < width; x++) {
/*     */           
/* 152 */           int rgb = pixels[x];
/* 153 */           int r = rgb >> 16 & 0xFF;
/* 154 */           int g = rgb >> 8 & 0xFF;
/* 155 */           int b = rgb & 0xFF;
/* 156 */           r = r * 255 / max;
/* 157 */           g = g * 255 / max;
/* 158 */           b = b * 255 / max;
/* 159 */           pixels[x] = rgb & 0xFF000000 | r << 16 | g << 8 | b;
/*     */         } 
/*     */         
/* 162 */         setRGB(image2, 0, y, width, 1, pixels);
/*     */       } 
/*     */     } 
/*     */     
/* 166 */     if (this.invert)
/*     */     {
/* 168 */       image2 = (new InvertFilter()).filter(image2, image2);
/*     */     }
/*     */     
/* 171 */     return image2;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 176 */     return "Edges/Difference of Gaussians...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DoGFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */