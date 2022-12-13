/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.FFT;
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
/*     */ public class LensBlurFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  30 */   private float radius = 10.0F;
/*  31 */   private float bloom = 2.0F;
/*  32 */   private float bloomThreshold = 255.0F;
/*  33 */   private float angle = 0.0F;
/*  34 */   private int sides = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(float radius) {
/*  43 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getRadius() {
/*  53 */     return this.radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSides(int sides) {
/*  63 */     this.sides = sides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSides() {
/*  73 */     return this.sides;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBloom(float bloom) {
/*  83 */     this.bloom = bloom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getBloom() {
/*  93 */     return this.bloom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBloomThreshold(float bloomThreshold) {
/* 103 */     this.bloomThreshold = bloomThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getBloomThreshold() {
/* 113 */     return this.bloomThreshold;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 118 */     int width = src.getWidth();
/* 119 */     int height = src.getHeight();
/* 120 */     int rows = 1, cols = 1;
/* 121 */     int log2rows = 0, log2cols = 0;
/* 122 */     int iradius = (int)Math.ceil(this.radius);
/* 123 */     int tileWidth = 128;
/* 124 */     int tileHeight = tileWidth;
/* 125 */     int adjustedWidth = width + iradius * 2;
/* 126 */     int adjustedHeight = height + iradius * 2;
/* 127 */     tileWidth = (iradius < 32) ? Math.min(128, width + 2 * iradius) : Math.min(256, width + 2 * iradius);
/* 128 */     tileHeight = (iradius < 32) ? Math.min(128, height + 2 * iradius) : Math.min(256, height + 2 * iradius);
/*     */     
/* 130 */     if (dst == null)
/*     */     {
/* 132 */       dst = new BufferedImage(width, height, 2);
/*     */     }
/*     */     
/* 135 */     while (rows < tileHeight) {
/*     */       
/* 137 */       rows *= 2;
/* 138 */       log2rows++;
/*     */     } 
/*     */     
/* 141 */     while (cols < tileWidth) {
/*     */       
/* 143 */       cols *= 2;
/* 144 */       log2cols++;
/*     */     } 
/*     */     
/* 147 */     int w = cols;
/* 148 */     int h = rows;
/* 149 */     tileWidth = w;
/* 150 */     tileHeight = h;
/* 151 */     FFT fft = new FFT(Math.max(log2rows, log2cols));
/* 152 */     int[] rgb = new int[w * h];
/* 153 */     float[][] mask = new float[2][w * h];
/* 154 */     float[][] gb = new float[2][w * h];
/* 155 */     float[][] ar = new float[2][w * h];
/*     */     
/* 157 */     double polyAngle = Math.PI / this.sides;
/* 158 */     double polyScale = 1.0D / Math.cos(polyAngle);
/* 159 */     double r2 = (this.radius * this.radius);
/* 160 */     double rangle = Math.toRadians(this.angle);
/* 161 */     float total = 0.0F;
/* 162 */     int i = 0;
/*     */     int y;
/* 164 */     for (y = 0; y < h; y++) {
/*     */       
/* 166 */       for (int x = 0; x < w; x++) {
/*     */         
/* 168 */         double dx = (x - w / 2.0F);
/* 169 */         double dy = (y - h / 2.0F);
/* 170 */         double r = dx * dx + dy * dy;
/* 171 */         double f = (r < r2) ? 1.0D : 0.0D;
/*     */         
/* 173 */         if (f != 0.0D) {
/*     */           
/* 175 */           r = Math.sqrt(r);
/*     */           
/* 177 */           if (this.sides != 0) {
/*     */             
/* 179 */             double a = Math.atan2(dy, dx) + rangle;
/* 180 */             a = ImageMath.mod(a, polyAngle * 2.0D) - polyAngle;
/* 181 */             f = Math.cos(a) * polyScale;
/*     */           }
/*     */           else {
/*     */             
/* 185 */             f = 1.0D;
/*     */           } 
/*     */           
/* 188 */           f = (f * r < this.radius) ? 1.0D : 0.0D;
/*     */         } 
/*     */         
/* 191 */         total += (float)f;
/* 192 */         mask[0][i] = (float)f;
/* 193 */         mask[1][i] = 0.0F;
/* 194 */         i++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 199 */     i = 0;
/*     */     
/* 201 */     for (y = 0; y < h; y++) {
/*     */       
/* 203 */       for (int x = 0; x < w; x++) {
/*     */         
/* 205 */         mask[0][i] = mask[0][i] / total;
/* 206 */         i++;
/*     */       } 
/*     */     } 
/*     */     
/* 210 */     fft.transform2D(mask[0], mask[1], w, h, true);
/*     */     int tileY;
/* 212 */     for (tileY = -iradius; tileY < height; tileY += tileHeight - 2 * iradius) {
/*     */       int tileX;
/* 214 */       for (tileX = -iradius; tileX < width; tileX += tileWidth - 2 * iradius) {
/*     */ 
/*     */ 
/*     */         
/* 218 */         int tx = tileX, ty = tileY, tw = tileWidth, th = tileHeight;
/* 219 */         int fx = 0, fy = 0;
/*     */         
/* 221 */         if (tx < 0) {
/*     */           
/* 223 */           tw += tx;
/* 224 */           fx -= tx;
/* 225 */           tx = 0;
/*     */         } 
/*     */         
/* 228 */         if (ty < 0) {
/*     */           
/* 230 */           th += ty;
/* 231 */           fy -= ty;
/* 232 */           ty = 0;
/*     */         } 
/*     */         
/* 235 */         if (tx + tw > width)
/*     */         {
/* 237 */           tw = width - tx;
/*     */         }
/*     */         
/* 240 */         if (ty + th > height)
/*     */         {
/* 242 */           th = height - ty;
/*     */         }
/*     */         
/* 245 */         src.getRGB(tx, ty, tw, th, rgb, fy * w + fx, w);
/*     */         
/* 247 */         i = 0;
/*     */         int j;
/* 249 */         for (j = 0; j < h; j++) {
/*     */           
/* 251 */           int m, imageY = j + tileY;
/*     */ 
/*     */           
/* 254 */           if (imageY < 0) {
/*     */             
/* 256 */             m = fy;
/*     */           }
/* 258 */           else if (imageY > height) {
/*     */             
/* 260 */             m = fy + th - 1;
/*     */           }
/*     */           else {
/*     */             
/* 264 */             m = j;
/*     */           } 
/*     */           
/* 267 */           m *= w;
/*     */           
/* 269 */           for (int x = 0; x < w; x++) {
/*     */             
/* 271 */             int n, imageX = x + tileX;
/*     */ 
/*     */             
/* 274 */             if (imageX < 0) {
/*     */               
/* 276 */               n = fx;
/*     */             }
/* 278 */             else if (imageX > width) {
/*     */               
/* 280 */               n = fx + tw - 1;
/*     */             }
/*     */             else {
/*     */               
/* 284 */               n = x;
/*     */             } 
/*     */             
/* 287 */             n += m;
/* 288 */             ar[0][i] = (rgb[n] >> 24 & 0xFF);
/* 289 */             float r = (rgb[n] >> 16 & 0xFF);
/* 290 */             float g = (rgb[n] >> 8 & 0xFF);
/* 291 */             float b = (rgb[n] & 0xFF);
/*     */ 
/*     */             
/* 294 */             if (r > this.bloomThreshold)
/*     */             {
/* 296 */               r *= this.bloom;
/*     */             }
/*     */ 
/*     */             
/* 300 */             if (g > this.bloomThreshold)
/*     */             {
/* 302 */               g *= this.bloom;
/*     */             }
/*     */ 
/*     */             
/* 306 */             if (b > this.bloomThreshold)
/*     */             {
/* 308 */               b *= this.bloom;
/*     */             }
/*     */ 
/*     */             
/* 312 */             ar[1][i] = r;
/* 313 */             gb[0][i] = g;
/* 314 */             gb[1][i] = b;
/* 315 */             i++;
/* 316 */             n++;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 321 */         fft.transform2D(ar[0], ar[1], cols, rows, true);
/* 322 */         fft.transform2D(gb[0], gb[1], cols, rows, true);
/*     */         
/* 324 */         i = 0;
/*     */         
/* 326 */         for (j = 0; j < h; j++) {
/*     */           
/* 328 */           for (int x = 0; x < w; x++) {
/*     */             
/* 330 */             float re = ar[0][i];
/* 331 */             float im = ar[1][i];
/* 332 */             float rem = mask[0][i];
/* 333 */             float imm = mask[1][i];
/* 334 */             ar[0][i] = re * rem - im * imm;
/* 335 */             ar[1][i] = re * imm + im * rem;
/* 336 */             re = gb[0][i];
/* 337 */             im = gb[1][i];
/* 338 */             gb[0][i] = re * rem - im * imm;
/* 339 */             gb[1][i] = re * imm + im * rem;
/* 340 */             i++;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 345 */         fft.transform2D(ar[0], ar[1], cols, rows, false);
/* 346 */         fft.transform2D(gb[0], gb[1], cols, rows, false);
/*     */         
/* 348 */         int row_flip = w >> 1;
/* 349 */         int col_flip = h >> 1;
/* 350 */         int index = 0;
/*     */ 
/*     */         
/* 353 */         for (int k = 0; k < w; k++) {
/*     */           
/* 355 */           int ym = k ^ row_flip;
/* 356 */           int yi = ym * cols;
/*     */           
/* 358 */           for (int x = 0; x < w; x++) {
/*     */             
/* 360 */             int xm = yi + (x ^ col_flip);
/* 361 */             int a = (int)ar[0][xm];
/* 362 */             int r = (int)ar[1][xm];
/* 363 */             int g = (int)gb[0][xm];
/* 364 */             int b = (int)gb[1][xm];
/*     */ 
/*     */             
/* 367 */             if (r > 255)
/*     */             {
/* 369 */               r = 255;
/*     */             }
/*     */             
/* 372 */             if (g > 255)
/*     */             {
/* 374 */               g = 255;
/*     */             }
/*     */             
/* 377 */             if (b > 255)
/*     */             {
/* 379 */               b = 255;
/*     */             }
/*     */             
/* 382 */             int argb = a << 24 | r << 16 | g << 8 | b;
/* 383 */             rgb[index++] = argb;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 388 */         tx = tileX + iradius;
/* 389 */         ty = tileY + iradius;
/* 390 */         tw = tileWidth - 2 * iradius;
/* 391 */         th = tileHeight - 2 * iradius;
/*     */         
/* 393 */         if (tx + tw > width)
/*     */         {
/* 395 */           tw = width - tx;
/*     */         }
/*     */         
/* 398 */         if (ty + th > height)
/*     */         {
/* 400 */           th = height - ty;
/*     */         }
/*     */         
/* 403 */         dst.setRGB(tx, ty, tw, th, rgb, iradius * w + iradius, w);
/*     */       } 
/*     */     } 
/*     */     
/* 407 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 412 */     return "Blur/Lens Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LensBlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */