/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.Noise;
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
/*     */ public class MarbleTexFilter
/*     */   extends PointFilter
/*     */ {
/*  25 */   private float scale = 32.0F;
/*  26 */   private float stretch = 1.0F;
/*  27 */   private float angle = 0.0F;
/*  28 */   private float turbulence = 1.0F;
/*  29 */   private float turbulenceFactor = 0.5F;
/*     */   private Colormap colormap;
/*  31 */   private float m00 = 1.0F;
/*  32 */   private float m01 = 0.0F;
/*  33 */   private float m10 = 0.0F;
/*  34 */   private float m11 = 1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setScale(float scale) {
/*  42 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScale() {
/*  47 */     return this.scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStretch(float stretch) {
/*  52 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStretch() {
/*  57 */     return this.stretch;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  62 */     this.angle = angle;
/*  63 */     float cos = (float)Math.cos(angle);
/*  64 */     float sin = (float)Math.sin(angle);
/*  65 */     this.m00 = cos;
/*  66 */     this.m01 = sin;
/*  67 */     this.m10 = -sin;
/*  68 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  73 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTurbulence(float turbulence) {
/*  78 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/*  83 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTurbulenceFactor(float turbulenceFactor) {
/*  88 */     this.turbulenceFactor = turbulenceFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTurbulenceFactor() {
/*  93 */     return this.turbulenceFactor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/*  98 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 103 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 108 */     float nx = this.m00 * x + this.m01 * y;
/* 109 */     float ny = this.m10 * x + this.m11 * y;
/* 110 */     nx /= this.scale * this.stretch;
/* 111 */     ny /= this.scale;
/* 112 */     int a = rgb & 0xFF000000;
/*     */     
/* 114 */     if (this.colormap != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 119 */       float f1 = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
/*     */       
/* 121 */       float f = 3.0F * this.turbulenceFactor * f1 + ny;
/* 122 */       f = (float)Math.sin(f * Math.PI);
/* 123 */       float f2 = (float)Math.sin(40.0D * f1);
/* 124 */       f = (float)(f + 0.2D * f2);
/* 125 */       return this.colormap.getColor(f);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     float chaos = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
/* 134 */     float t = (float)Math.sin(Math.sin(8.0D * chaos + (7.0F * nx) + 3.0D * ny));
/* 135 */     float brownLayer = Math.abs(t), greenLayer = brownLayer;
/* 136 */     float perturb = (float)Math.sin(40.0D * chaos);
/* 137 */     perturb = Math.abs(perturb);
/* 138 */     float brownPerturb = 0.6F * perturb + 0.3F;
/* 139 */     float greenPerturb = 0.2F * perturb + 0.8F;
/* 140 */     float grnPerturb = 0.15F * perturb + 0.85F;
/* 141 */     float grn = 0.5F * (float)Math.pow(Math.abs(brownLayer), 0.3D);
/* 142 */     brownLayer = (float)Math.pow(0.5D * (brownLayer + 1.0D), 0.6D) * brownPerturb;
/* 143 */     greenLayer = (float)Math.pow(0.5D * (greenLayer + 1.0D), 0.6D) * greenPerturb;
/* 144 */     float red = (0.5F * brownLayer + 0.35F * greenLayer) * 2.0F * grn;
/* 145 */     float blu = (0.25F * brownLayer + 0.35F * greenLayer) * 2.0F * grn;
/* 146 */     grn *= Math.max(brownLayer, greenLayer) * grnPerturb;
/* 147 */     int r = rgb >> 16 & 0xFF;
/* 148 */     int g = rgb >> 8 & 0xFF;
/* 149 */     int b = rgb & 0xFF;
/* 150 */     r = PixelUtils.clamp((int)(r * red));
/* 151 */     g = PixelUtils.clamp((int)(g * grn));
/* 152 */     b = PixelUtils.clamp((int)(b * blu));
/* 153 */     return rgb & 0xFF000000 | r << 16 | g << 8 | b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 159 */     return "Texture/Marble Texture...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MarbleTexFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */