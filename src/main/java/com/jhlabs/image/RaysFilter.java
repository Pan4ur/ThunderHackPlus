/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.composite.MiscComposite;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ public class RaysFilter
/*     */   extends MotionBlurOp
/*     */ {
/*  29 */   private float opacity = 1.0F;
/*  30 */   private float threshold = 0.0F;
/*  31 */   private float strength = 0.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean raysOnly = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private Colormap colormap;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOpacity(float opacity) {
/*  46 */     this.opacity = opacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOpacity() {
/*  56 */     return this.opacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreshold(float threshold) {
/*  66 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getThreshold() {
/*  76 */     return this.threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStrength(float strength) {
/*  86 */     this.strength = strength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStrength() {
/*  96 */     return this.strength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRaysOnly(boolean raysOnly) {
/* 106 */     this.raysOnly = raysOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getRaysOnly() {
/* 116 */     return this.raysOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 126 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 136 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 141 */     int width = src.getWidth();
/* 142 */     int height = src.getHeight();
/* 143 */     int[] pixels = new int[width];
/* 144 */     int[] srcPixels = new int[width];
/* 145 */     BufferedImage rays = new BufferedImage(width, height, 2);
/* 146 */     int threshold3 = (int)(this.threshold * 3.0F * 255.0F);
/*     */     int y;
/* 148 */     for (y = 0; y < height; y++) {
/*     */       
/* 150 */       getRGB(src, 0, y, width, 1, pixels);
/*     */       
/* 152 */       for (int x = 0; x < width; x++) {
/*     */         
/* 154 */         int rgb = pixels[x];
/* 155 */         int a = rgb & 0xFF000000;
/* 156 */         int r = rgb >> 16 & 0xFF;
/* 157 */         int i = rgb >> 8 & 0xFF;
/* 158 */         int b = rgb & 0xFF;
/* 159 */         int l = r + i + b;
/*     */         
/* 161 */         if (l < threshold3) {
/*     */           
/* 163 */           pixels[x] = -16777216;
/*     */         }
/*     */         else {
/*     */           
/* 167 */           l /= 3;
/* 168 */           pixels[x] = a | l << 16 | l << 8 | l;
/*     */         } 
/*     */       } 
/*     */       
/* 172 */       setRGB(rays, 0, y, width, 1, pixels);
/*     */     } 
/*     */     
/* 175 */     rays = super.filter(rays, (BufferedImage)null);
/*     */     
/* 177 */     for (y = 0; y < height; y++) {
/*     */       
/* 179 */       getRGB(rays, 0, y, width, 1, pixels);
/* 180 */       getRGB(src, 0, y, width, 1, srcPixels);
/*     */       
/* 182 */       for (int x = 0; x < width; x++) {
/*     */         
/* 184 */         int rgb = pixels[x];
/* 185 */         int a = rgb & 0xFF000000;
/* 186 */         int r = rgb >> 16 & 0xFF;
/* 187 */         int i = rgb >> 8 & 0xFF;
/* 188 */         int b = rgb & 0xFF;
/*     */         
/* 190 */         if (this.colormap != null) {
/*     */           
/* 192 */           int l = r + i + b;
/* 193 */           rgb = this.colormap.getColor(l * this.strength * 0.33333334F);
/*     */         }
/*     */         else {
/*     */           
/* 197 */           r = PixelUtils.clamp((int)(r * this.strength));
/* 198 */           i = PixelUtils.clamp((int)(i * this.strength));
/* 199 */           b = PixelUtils.clamp((int)(b * this.strength));
/* 200 */           rgb = a | r << 16 | i << 8 | b;
/*     */         } 
/*     */         
/* 203 */         pixels[x] = rgb;
/*     */       } 
/*     */       
/* 206 */       setRGB(rays, 0, y, width, 1, pixels);
/*     */     } 
/*     */     
/* 209 */     if (dst == null)
/*     */     {
/* 211 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 214 */     Graphics2D g = dst.createGraphics();
/*     */     
/* 216 */     if (!this.raysOnly) {
/*     */       
/* 218 */       g.setComposite(AlphaComposite.SrcOver);
/* 219 */       g.drawRenderedImage(src, (AffineTransform)null);
/*     */     } 
/*     */     
/* 222 */     g.setComposite(MiscComposite.getInstance(1, this.opacity));
/* 223 */     g.drawRenderedImage(rays, (AffineTransform)null);
/* 224 */     g.dispose();
/* 225 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 230 */     return "Stylize/Rays...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RaysFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */