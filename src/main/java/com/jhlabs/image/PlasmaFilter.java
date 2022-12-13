/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Date;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlasmaFilter
/*     */   extends WholeImageFilter
/*     */ {
/*  26 */   public float turbulence = 1.0F;
/*  27 */   private float scaling = 0.0F;
/*  28 */   private Colormap colormap = new LinearColormap();
/*     */   private Random randomGenerator;
/*  30 */   private long seed = 567L;
/*     */   
/*     */   private boolean useColormap = false;
/*     */   private boolean useImageColors = false;
/*     */   
/*     */   public PlasmaFilter() {
/*  36 */     this.randomGenerator = new Random();
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
/*     */   public void setTurbulence(float turbulence) {
/*  48 */     this.turbulence = turbulence;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTurbulence() {
/*  58 */     return this.turbulence;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setScaling(float scaling) {
/*  63 */     this.scaling = scaling;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaling() {
/*  68 */     return this.scaling;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/*  78 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/*  88 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseColormap(boolean useColormap) {
/*  93 */     this.useColormap = useColormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseColormap() {
/*  98 */     return this.useColormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseImageColors(boolean useImageColors) {
/* 103 */     this.useImageColors = useImageColors;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseImageColors() {
/* 108 */     return this.useImageColors;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSeed(int seed) {
/* 113 */     this.seed = seed;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSeed() {
/* 118 */     return (int)this.seed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void randomize() {
/* 123 */     this.seed = (new Date()).getTime();
/*     */   }
/*     */ 
/*     */   
/*     */   private int randomRGB(int[] inPixels, int x, int y) {
/* 128 */     if (this.useImageColors)
/*     */     {
/* 130 */       return inPixels[y * this.originalSpace.width + x];
/*     */     }
/*     */ 
/*     */     
/* 134 */     int r = (int)(255.0F * this.randomGenerator.nextFloat());
/* 135 */     int g = (int)(255.0F * this.randomGenerator.nextFloat());
/* 136 */     int b = (int)(255.0F * this.randomGenerator.nextFloat());
/* 137 */     return 0xFF000000 | r << 16 | g << 8 | b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int displace(int rgb, float amount) {
/* 143 */     int r = rgb >> 16 & 0xFF;
/* 144 */     int g = rgb >> 8 & 0xFF;
/* 145 */     int b = rgb & 0xFF;
/* 146 */     r = PixelUtils.clamp(r + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
/* 147 */     g = PixelUtils.clamp(g + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
/* 148 */     b = PixelUtils.clamp(b + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
/* 149 */     return 0xFF000000 | r << 16 | g << 8 | b;
/*     */   }
/*     */ 
/*     */   
/*     */   private int average(int rgb1, int rgb2) {
/* 154 */     return PixelUtils.combinePixels(rgb1, rgb2, 13);
/*     */   }
/*     */ 
/*     */   
/*     */   private int getPixel(int x, int y, int[] pixels, int stride) {
/* 159 */     return pixels[y * stride + x];
/*     */   }
/*     */ 
/*     */   
/*     */   private void putPixel(int x, int y, int rgb, int[] pixels, int stride) {
/* 164 */     pixels[y * stride + x] = rgb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean doPixel(int x1, int y1, int x2, int y2, int[] pixels, int stride, int depth, int scale) {
/* 171 */     if (depth == 0) {
/*     */ 
/*     */       
/* 174 */       int tl = getPixel(x1, y1, pixels, stride);
/* 175 */       int bl = getPixel(x1, y2, pixels, stride);
/* 176 */       int tr = getPixel(x2, y1, pixels, stride);
/* 177 */       int br = getPixel(x2, y2, pixels, stride);
/* 178 */       float amount = 256.0F / 2.0F * scale * this.turbulence;
/* 179 */       int i = (x1 + x2) / 2;
/* 180 */       int j = (y1 + y2) / 2;
/*     */       
/* 182 */       if (i == x1 && i == x2 && j == y1 && j == y2)
/*     */       {
/* 184 */         return true;
/*     */       }
/*     */       
/* 187 */       if (i != x1 || i != x2) {
/*     */         
/* 189 */         int ml = average(tl, bl);
/* 190 */         ml = displace(ml, amount);
/* 191 */         putPixel(x1, j, ml, pixels, stride);
/*     */         
/* 193 */         if (x1 != x2) {
/*     */           
/* 195 */           int mr = average(tr, br);
/* 196 */           mr = displace(mr, amount);
/* 197 */           putPixel(x2, j, mr, pixels, stride);
/*     */         } 
/*     */       } 
/*     */       
/* 201 */       if (j != y1 || j != y2) {
/*     */         
/* 203 */         if (x1 != i || j != y2) {
/*     */           
/* 205 */           int mb = average(bl, br);
/* 206 */           mb = displace(mb, amount);
/* 207 */           putPixel(i, y2, mb, pixels, stride);
/*     */         } 
/*     */         
/* 210 */         if (y1 != y2) {
/*     */           
/* 212 */           int mt = average(tl, tr);
/* 213 */           mt = displace(mt, amount);
/* 214 */           putPixel(i, y1, mt, pixels, stride);
/*     */         } 
/*     */       } 
/*     */       
/* 218 */       if (y1 != y2 || x1 != x2) {
/*     */         
/* 220 */         int mm = average(tl, br);
/* 221 */         int t = average(bl, tr);
/* 222 */         mm = average(mm, t);
/* 223 */         mm = displace(mm, amount);
/* 224 */         putPixel(i, j, mm, pixels, stride);
/*     */       } 
/*     */       
/* 227 */       if (x2 - x1 < 3 && y2 - y1 < 3)
/*     */       {
/* 229 */         return false;
/*     */       }
/*     */       
/* 232 */       return true;
/*     */     } 
/*     */     
/* 235 */     int mx = (x1 + x2) / 2;
/* 236 */     int my = (y1 + y2) / 2;
/* 237 */     doPixel(x1, y1, mx, my, pixels, stride, depth - 1, scale + 1);
/* 238 */     doPixel(x1, my, mx, y2, pixels, stride, depth - 1, scale + 1);
/* 239 */     doPixel(mx, y1, x2, my, pixels, stride, depth - 1, scale + 1);
/* 240 */     return doPixel(mx, my, x2, y2, pixels, stride, depth - 1, scale + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 245 */     int[] outPixels = new int[width * height];
/* 246 */     this.randomGenerator.setSeed(this.seed);
/* 247 */     int w1 = width - 1;
/* 248 */     int h1 = height - 1;
/* 249 */     putPixel(0, 0, randomRGB(inPixels, 0, 0), outPixels, width);
/* 250 */     putPixel(w1, 0, randomRGB(inPixels, w1, 0), outPixels, width);
/* 251 */     putPixel(0, h1, randomRGB(inPixels, 0, h1), outPixels, width);
/* 252 */     putPixel(w1, h1, randomRGB(inPixels, w1, h1), outPixels, width);
/* 253 */     putPixel(w1 / 2, h1 / 2, randomRGB(inPixels, w1 / 2, h1 / 2), outPixels, width);
/* 254 */     putPixel(0, h1 / 2, randomRGB(inPixels, 0, h1 / 2), outPixels, width);
/* 255 */     putPixel(w1, h1 / 2, randomRGB(inPixels, w1, h1 / 2), outPixels, width);
/* 256 */     putPixel(w1 / 2, 0, randomRGB(inPixels, w1 / 2, 0), outPixels, width);
/* 257 */     putPixel(w1 / 2, h1, randomRGB(inPixels, w1 / 2, h1), outPixels, width);
/* 258 */     int depth = 1;
/*     */     
/* 260 */     while (doPixel(0, 0, width - 1, height - 1, outPixels, width, depth, 0))
/*     */     {
/* 262 */       depth++;
/*     */     }
/*     */     
/* 265 */     if (this.useColormap && this.colormap != null) {
/*     */       
/* 267 */       int index = 0;
/*     */       
/* 269 */       for (int y = 0; y < height; y++) {
/*     */         
/* 271 */         for (int x = 0; x < width; x++) {
/*     */           
/* 273 */           outPixels[index] = this.colormap.getColor((outPixels[index] & 0xFF) / 255.0F);
/* 274 */           index++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 279 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 284 */     return "Texture/Plasma...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PlasmaFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */