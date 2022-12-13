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
/*     */ public class BoxBlurFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private float hRadius;
/*     */   private float vRadius;
/*  31 */   private int iterations = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean premultiplyAlpha = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BoxBlurFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BoxBlurFilter(float hRadius, float vRadius, int iterations) {
/*  49 */     this.hRadius = hRadius;
/*  50 */     this.vRadius = vRadius;
/*  51 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPremultiplyAlpha(boolean premultiplyAlpha) {
/*  61 */     this.premultiplyAlpha = premultiplyAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getPremultiplyAlpha() {
/*  71 */     return this.premultiplyAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  76 */     int width = src.getWidth();
/*  77 */     int height = src.getHeight();
/*     */     
/*  79 */     if (dst == null)
/*     */     {
/*  81 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/*  84 */     int[] inPixels = new int[width * height];
/*  85 */     int[] outPixels = new int[width * height];
/*  86 */     getRGB(src, 0, 0, width, height, inPixels);
/*     */     
/*  88 */     if (this.premultiplyAlpha)
/*     */     {
/*  90 */       ImageMath.premultiply(inPixels, 0, inPixels.length);
/*     */     }
/*     */     
/*  93 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/*  95 */       blur(inPixels, outPixels, width, height, this.hRadius);
/*  96 */       blur(outPixels, inPixels, height, width, this.vRadius);
/*     */     } 
/*     */     
/*  99 */     blurFractional(inPixels, outPixels, width, height, this.hRadius);
/* 100 */     blurFractional(outPixels, inPixels, height, width, this.vRadius);
/*     */     
/* 102 */     if (this.premultiplyAlpha)
/*     */     {
/* 104 */       ImageMath.unpremultiply(inPixels, 0, inPixels.length);
/*     */     }
/*     */     
/* 107 */     setRGB(dst, 0, 0, width, height, inPixels);
/* 108 */     return dst;
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
/*     */   
/*     */   public static void blur(int[] in, int[] out, int width, int height, float radius) {
/* 121 */     int widthMinus1 = width - 1;
/* 122 */     int r = (int)radius;
/* 123 */     int tableSize = 2 * r + 1;
/* 124 */     int[] divide = new int[256 * tableSize];
/*     */     
/* 126 */     for (int i = 0; i < 256 * tableSize; i++)
/*     */     {
/* 128 */       divide[i] = i / tableSize;
/*     */     }
/*     */     
/* 131 */     int inIndex = 0;
/*     */     
/* 133 */     for (int y = 0; y < height; y++) {
/*     */       
/* 135 */       int outIndex = y;
/* 136 */       int ta = 0, tr = 0, tg = 0, tb = 0;
/*     */       
/* 138 */       for (int j = -r; j <= r; j++) {
/*     */         
/* 140 */         int rgb = in[inIndex + ImageMath.clamp(j, 0, width - 1)];
/* 141 */         ta += rgb >> 24 & 0xFF;
/* 142 */         tr += rgb >> 16 & 0xFF;
/* 143 */         tg += rgb >> 8 & 0xFF;
/* 144 */         tb += rgb & 0xFF;
/*     */       } 
/*     */       
/* 147 */       for (int x = 0; x < width; x++) {
/*     */         
/* 149 */         out[outIndex] = divide[ta] << 24 | divide[tr] << 16 | divide[tg] << 8 | divide[tb];
/* 150 */         int i1 = x + r + 1;
/*     */         
/* 152 */         if (i1 > widthMinus1)
/*     */         {
/* 154 */           i1 = widthMinus1;
/*     */         }
/*     */         
/* 157 */         int i2 = x - r;
/*     */         
/* 159 */         if (i2 < 0)
/*     */         {
/* 161 */           i2 = 0;
/*     */         }
/*     */         
/* 164 */         int rgb1 = in[inIndex + i1];
/* 165 */         int rgb2 = in[inIndex + i2];
/* 166 */         ta += (rgb1 >> 24 & 0xFF) - (rgb2 >> 24 & 0xFF);
/* 167 */         tr += (rgb1 & 0xFF0000) - (rgb2 & 0xFF0000) >> 16;
/* 168 */         tg += (rgb1 & 0xFF00) - (rgb2 & 0xFF00) >> 8;
/* 169 */         tb += (rgb1 & 0xFF) - (rgb2 & 0xFF);
/* 170 */         outIndex += height;
/*     */       } 
/*     */       
/* 173 */       inIndex += width;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
/* 179 */     radius -= (int)radius;
/* 180 */     float f = 1.0F / (1.0F + 2.0F * radius);
/* 181 */     int inIndex = 0;
/*     */     
/* 183 */     for (int y = 0; y < height; y++) {
/*     */       
/* 185 */       int outIndex = y;
/* 186 */       out[outIndex] = in[0];
/* 187 */       outIndex += height;
/*     */       
/* 189 */       for (int x = 1; x < width - 1; x++) {
/*     */         
/* 191 */         int i = inIndex + x;
/* 192 */         int rgb1 = in[i - 1];
/* 193 */         int rgb2 = in[i];
/* 194 */         int rgb3 = in[i + 1];
/* 195 */         int a1 = rgb1 >> 24 & 0xFF;
/* 196 */         int r1 = rgb1 >> 16 & 0xFF;
/* 197 */         int g1 = rgb1 >> 8 & 0xFF;
/* 198 */         int b1 = rgb1 & 0xFF;
/* 199 */         int a2 = rgb2 >> 24 & 0xFF;
/* 200 */         int r2 = rgb2 >> 16 & 0xFF;
/* 201 */         int g2 = rgb2 >> 8 & 0xFF;
/* 202 */         int b2 = rgb2 & 0xFF;
/* 203 */         int a3 = rgb3 >> 24 & 0xFF;
/* 204 */         int r3 = rgb3 >> 16 & 0xFF;
/* 205 */         int g3 = rgb3 >> 8 & 0xFF;
/* 206 */         int b3 = rgb3 & 0xFF;
/* 207 */         a1 = a2 + (int)((a1 + a3) * radius);
/* 208 */         r1 = r2 + (int)((r1 + r3) * radius);
/* 209 */         g1 = g2 + (int)((g1 + g3) * radius);
/* 210 */         b1 = b2 + (int)((b1 + b3) * radius);
/* 211 */         a1 = (int)(a1 * f);
/* 212 */         r1 = (int)(r1 * f);
/* 213 */         g1 = (int)(g1 * f);
/* 214 */         b1 = (int)(b1 * f);
/* 215 */         out[outIndex] = a1 << 24 | r1 << 16 | g1 << 8 | b1;
/* 216 */         outIndex += height;
/*     */       } 
/*     */       
/* 219 */       out[outIndex] = in[width - 1];
/* 220 */       inIndex += width;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHRadius(float hRadius) {
/* 232 */     this.hRadius = hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getHRadius() {
/* 242 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVRadius(float vRadius) {
/* 253 */     this.vRadius = vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getVRadius() {
/* 263 */     return this.vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/* 274 */     this.hRadius = this.vRadius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/* 284 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIterations(int iterations) {
/* 295 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIterations() {
/* 305 */     return this.iterations;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 310 */     return "Blur/Box Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\BoxBlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */