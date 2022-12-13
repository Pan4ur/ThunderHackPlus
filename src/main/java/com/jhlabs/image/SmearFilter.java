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
/*     */ 
/*     */ public class SmearFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   public static final int CROSSES = 0;
/*     */   public static final int LINES = 1;
/*     */   public static final int CIRCLES = 2;
/*     */   public static final int SQUARES = 3;
/*  31 */   private Colormap colormap = new LinearColormap();
/*  32 */   private float angle = 0.0F;
/*  33 */   private float density = 0.5F;
/*  34 */   private float scatter = 0.0F;
/*  35 */   private int distance = 8;
/*     */   private Random randomGenerator;
/*  37 */   private long seed = 567L;
/*  38 */   private int shape = 1;
/*  39 */   private float mix = 0.5F;
/*  40 */   private int fadeout = 0;
/*     */   
/*     */   private boolean background = false;
/*     */   
/*     */   public SmearFilter() {
/*  45 */     this.randomGenerator = new Random();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShape(int shape) {
/*  50 */     this.shape = shape;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getShape() {
/*  55 */     return this.shape;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDistance(int distance) {
/*  60 */     this.distance = distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDistance() {
/*  65 */     return this.distance;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDensity(float density) {
/*  70 */     this.density = density;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDensity() {
/*  75 */     return this.density;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setScatter(float scatter) {
/*  80 */     this.scatter = scatter;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScatter() {
/*  85 */     return this.scatter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  96 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 106 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMix(float mix) {
/* 111 */     this.mix = mix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMix() {
/* 116 */     return this.mix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFadeout(int fadeout) {
/* 121 */     this.fadeout = fadeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFadeout() {
/* 126 */     return this.fadeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBackground(boolean background) {
/* 131 */     this.background = background;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBackground() {
/* 136 */     return this.background;
/*     */   }
/*     */ 
/*     */   
/*     */   public void randomize() {
/* 141 */     this.seed = (new Date()).getTime();
/*     */   }
/*     */ 
/*     */   
/*     */   private float random(float low, float high) {
/* 146 */     return low + (high - low) * this.randomGenerator.nextFloat();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 151 */     int numShapes, radius, radius2, outPixels[] = new int[width * height];
/* 152 */     this.randomGenerator.setSeed(this.seed);
/* 153 */     float sinAngle = (float)Math.sin(this.angle);
/* 154 */     float cosAngle = (float)Math.cos(this.angle);
/* 155 */     int i = 0;
/*     */ 
/*     */     
/* 158 */     for (int y = 0; y < height; y++) {
/* 159 */       for (int x = 0; x < width; x++) {
/*     */         
/* 161 */         outPixels[i] = this.background ? -1 : inPixels[i];
/* 162 */         i++;
/*     */       } 
/*     */     } 
/* 165 */     switch (this.shape) {
/*     */ 
/*     */       
/*     */       case 0:
/* 169 */         numShapes = (int)(2.0F * this.density * width * height / (this.distance + 1));
/*     */         
/* 171 */         for (i = 0; i < numShapes; i++) {
/*     */           
/* 173 */           int x = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
/* 174 */           int j = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
/* 175 */           int length = this.randomGenerator.nextInt() % this.distance + 1;
/* 176 */           int rgb = inPixels[j * width + x];
/*     */           
/* 178 */           for (int x1 = x - length; x1 < x + length + 1; x1++) {
/*     */             
/* 180 */             if (x1 >= 0 && x1 < width) {
/*     */               
/* 182 */               int rgb2 = this.background ? -1 : outPixels[j * width + x1];
/* 183 */               outPixels[j * width + x1] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */             } 
/*     */           } 
/*     */           
/* 187 */           for (int y1 = j - length; y1 < j + length + 1; y1++) {
/*     */             
/* 189 */             if (y1 >= 0 && y1 < height) {
/*     */               
/* 191 */               int rgb2 = this.background ? -1 : outPixels[y1 * width + x];
/* 192 */               outPixels[y1 * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case 1:
/* 200 */         numShapes = (int)(2.0F * this.density * width * height / 2.0F);
/*     */         
/* 202 */         for (i = 0; i < numShapes; i++) {
/*     */           
/* 204 */           int ddx, ddy, sx = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
/* 205 */           int sy = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
/* 206 */           int rgb = inPixels[sy * width + sx];
/* 207 */           int length = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % this.distance;
/* 208 */           int dx = (int)(length * cosAngle);
/* 209 */           int dy = (int)(length * sinAngle);
/* 210 */           int x0 = sx - dx;
/* 211 */           int y0 = sy - dy;
/* 212 */           int x1 = sx + dx;
/* 213 */           int y1 = sy + dy;
/*     */ 
/*     */           
/* 216 */           if (x1 < x0) {
/*     */             
/* 218 */             ddx = -1;
/*     */           }
/*     */           else {
/*     */             
/* 222 */             ddx = 1;
/*     */           } 
/*     */           
/* 225 */           if (y1 < y0) {
/*     */             
/* 227 */             ddy = -1;
/*     */           }
/*     */           else {
/*     */             
/* 231 */             ddy = 1;
/*     */           } 
/*     */           
/* 234 */           dx = x1 - x0;
/* 235 */           dy = y1 - y0;
/* 236 */           dx = Math.abs(dx);
/* 237 */           dy = Math.abs(dy);
/* 238 */           int x = x0;
/* 239 */           int j = y0;
/*     */           
/* 241 */           if (x < width && x >= 0 && j < height && j >= 0) {
/*     */             
/* 243 */             int rgb2 = this.background ? -1 : outPixels[j * width + x];
/* 244 */             outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */           } 
/*     */           
/* 247 */           if (Math.abs(dx) > Math.abs(dy)) {
/*     */             
/* 249 */             int d = 2 * dy - dx;
/* 250 */             int incrE = 2 * dy;
/* 251 */             int incrNE = 2 * (dy - dx);
/*     */             
/* 253 */             while (x != x1) {
/*     */               
/* 255 */               if (d <= 0) {
/*     */                 
/* 257 */                 d += incrE;
/*     */               }
/*     */               else {
/*     */                 
/* 261 */                 d += incrNE;
/* 262 */                 j += ddy;
/*     */               } 
/*     */               
/* 265 */               x += ddx;
/*     */               
/* 267 */               if (x < width && x >= 0 && j < height && j >= 0)
/*     */               {
/* 269 */                 int rgb2 = this.background ? -1 : outPixels[j * width + x];
/* 270 */                 outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */               }
/*     */             
/*     */             } 
/*     */           } else {
/*     */             
/* 276 */             int d = 2 * dx - dy;
/* 277 */             int incrE = 2 * dx;
/* 278 */             int incrNE = 2 * (dx - dy);
/*     */             
/* 280 */             while (j != y1) {
/*     */               
/* 282 */               if (d <= 0) {
/*     */                 
/* 284 */                 d += incrE;
/*     */               }
/*     */               else {
/*     */                 
/* 288 */                 d += incrNE;
/* 289 */                 x += ddx;
/*     */               } 
/*     */               
/* 292 */               j += ddy;
/*     */               
/* 294 */               if (x < width && x >= 0 && j < height && j >= 0) {
/*     */                 
/* 296 */                 int rgb2 = this.background ? -1 : outPixels[j * width + x];
/* 297 */                 outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case 2:
/*     */       case 3:
/* 307 */         radius = this.distance + 1;
/* 308 */         radius2 = radius * radius;
/* 309 */         numShapes = (int)(2.0F * this.density * width * height / radius);
/*     */         
/* 311 */         for (i = 0; i < numShapes; i++) {
/*     */           
/* 313 */           int sx = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
/* 314 */           int sy = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
/* 315 */           int rgb = inPixels[sy * width + sx];
/*     */           
/* 317 */           for (int x = sx - radius; x < sx + radius + 1; x++) {
/*     */             
/* 319 */             for (int j = sy - radius; j < sy + radius + 1; j++) {
/*     */               int f;
/*     */ 
/*     */               
/* 323 */               if (this.shape == 2) {
/*     */                 
/* 325 */                 f = (x - sx) * (x - sx) + (j - sy) * (j - sy);
/*     */               }
/*     */               else {
/*     */                 
/* 329 */                 f = 0;
/*     */               } 
/*     */               
/* 332 */               if (x >= 0 && x < width && j >= 0 && j < height && f <= radius2) {
/*     */                 
/* 334 */                 int rgb2 = this.background ? -1 : outPixels[j * width + x];
/* 335 */                 outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         break;
/*     */     } 
/* 342 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 347 */     return "Effects/Smear...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SmearFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */