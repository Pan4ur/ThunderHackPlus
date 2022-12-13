/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Point;
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
/*     */ 
/*     */ public class GradientFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   public static final int LINEAR = 0;
/*     */   public static final int BILINEAR = 1;
/*     */   public static final int RADIAL = 2;
/*     */   public static final int CONICAL = 3;
/*     */   public static final int BICONICAL = 4;
/*     */   public static final int SQUARE = 5;
/*     */   public static final int INT_LINEAR = 0;
/*     */   public static final int INT_CIRCLE_UP = 1;
/*     */   public static final int INT_CIRCLE_DOWN = 2;
/*     */   public static final int INT_SMOOTH = 3;
/*  41 */   private float angle = 0.0F;
/*  42 */   private int color1 = -16777216;
/*  43 */   private int color2 = -1;
/*  44 */   private Point p1 = new Point(0, 0); private Point p2 = new Point(64, 64);
/*     */   private boolean repeat = false;
/*     */   private float x1;
/*     */   private float y1;
/*     */   private float dx;
/*     */   private float dy;
/*  50 */   private Colormap colormap = null;
/*     */   private int type;
/*  52 */   private int interpolation = 0;
/*  53 */   private int paintMode = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public GradientFilter() {}
/*     */ 
/*     */   
/*     */   public GradientFilter(Point p1, Point p2, int color1, int color2, boolean repeat, int type, int interpolation) {
/*  61 */     this.p1 = p1;
/*  62 */     this.p2 = p2;
/*  63 */     this.color1 = color1;
/*  64 */     this.color2 = color2;
/*  65 */     this.repeat = repeat;
/*  66 */     this.type = type;
/*  67 */     this.interpolation = interpolation;
/*  68 */     this.colormap = new LinearColormap(color1, color2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPoint1(Point point1) {
/*  73 */     this.p1 = point1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Point getPoint1() {
/*  78 */     return this.p1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPoint2(Point point2) {
/*  83 */     this.p2 = point2;
/*     */   }
/*     */ 
/*     */   
/*     */   public Point getPoint2() {
/*  88 */     return this.p2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setType(int type) {
/*  93 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  98 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInterpolation(int interpolation) {
/* 103 */     this.interpolation = interpolation;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInterpolation() {
/* 108 */     return this.interpolation;
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
/* 119 */     this.angle = angle;
/* 120 */     this.p2 = new Point((int)(64.0D * Math.cos(angle)), (int)(64.0D * Math.sin(angle)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 130 */     return this.angle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 140 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 150 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPaintMode(int paintMode) {
/* 155 */     this.paintMode = paintMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPaintMode() {
/* 160 */     return this.paintMode;
/*     */   }
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*     */     float y1, y2;
/* 165 */     int width = src.getWidth();
/* 166 */     int height = src.getHeight();
/*     */     
/* 168 */     if (dst == null)
/*     */     {
/* 170 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 175 */     float x1 = this.p1.x;
/* 176 */     float x2 = this.p2.x;
/*     */     
/* 178 */     if (x1 > x2 && this.type != 2) {
/*     */       
/* 180 */       y1 = x1;
/* 181 */       x1 = x2;
/* 182 */       x2 = y1;
/* 183 */       y1 = this.p2.y;
/* 184 */       y2 = this.p1.y;
/* 185 */       int rgb1 = this.color2;
/* 186 */       int rgb2 = this.color1;
/*     */     }
/*     */     else {
/*     */       
/* 190 */       y1 = this.p1.y;
/* 191 */       y2 = this.p2.y;
/* 192 */       int rgb1 = this.color1;
/* 193 */       int rgb2 = this.color2;
/*     */     } 
/*     */     
/* 196 */     float dx = x2 - x1;
/* 197 */     float dy = y2 - y1;
/* 198 */     float lenSq = dx * dx + dy * dy;
/* 199 */     this.x1 = x1;
/* 200 */     this.y1 = y1;
/*     */     
/* 202 */     if (lenSq >= Float.MIN_VALUE) {
/*     */       
/* 204 */       dx /= lenSq;
/* 205 */       dy /= lenSq;
/*     */       
/* 207 */       if (this.repeat) {
/*     */         
/* 209 */         dx %= 1.0F;
/* 210 */         dy %= 1.0F;
/*     */       } 
/*     */     } 
/*     */     
/* 214 */     this.dx = dx;
/* 215 */     this.dy = dy;
/* 216 */     int[] pixels = new int[width];
/*     */     
/* 218 */     for (int y = 0; y < height; y++) {
/*     */       
/* 220 */       getRGB(src, 0, y, width, 1, pixels);
/*     */       
/* 222 */       switch (this.type) {
/*     */         
/*     */         case 0:
/*     */         case 1:
/* 226 */           linearGradient(pixels, y, width, 1);
/*     */           break;
/*     */         
/*     */         case 2:
/* 230 */           radialGradient(pixels, y, width, 1);
/*     */           break;
/*     */         
/*     */         case 3:
/*     */         case 4:
/* 235 */           conicalGradient(pixels, y, width, 1);
/*     */           break;
/*     */         
/*     */         case 5:
/* 239 */           squareGradient(pixels, y, width, 1);
/*     */           break;
/*     */       } 
/*     */       
/* 243 */       setRGB(dst, 0, y, width, 1, pixels);
/*     */     } 
/*     */     
/* 246 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   private void repeatGradient(int[] pixels, int w, int h, float rowrel, float dx, float dy) {
/* 251 */     int off = 0;
/*     */     
/* 253 */     for (int y = 0; y < h; y++) {
/*     */       
/* 255 */       float colrel = rowrel;
/* 256 */       int j = w;
/*     */ 
/*     */       
/* 259 */       while (--j >= 0) {
/*     */         int rgb;
/* 261 */         if (this.type == 1) {
/*     */           
/* 263 */           rgb = this.colormap.getColor(map(ImageMath.triangle(colrel)));
/*     */         }
/*     */         else {
/*     */           
/* 267 */           rgb = this.colormap.getColor(map(ImageMath.mod(colrel, 1.0F)));
/*     */         } 
/*     */         
/* 270 */         pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 271 */         off++;
/* 272 */         colrel += dx;
/*     */       } 
/*     */       
/* 275 */       rowrel += dy;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void singleGradient(int[] pixels, int w, int h, float rowrel, float dx, float dy) {
/* 281 */     int off = 0;
/*     */     
/* 283 */     for (int y = 0; y < h; y++) {
/*     */       
/* 285 */       float colrel = rowrel;
/* 286 */       int j = w;
/*     */ 
/*     */       
/* 289 */       if (colrel <= 0.0D) {
/*     */         
/* 291 */         int rgb = this.colormap.getColor(0.0F);
/*     */ 
/*     */         
/*     */         do {
/* 295 */           pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 296 */           off++;
/* 297 */           colrel += dx;
/*     */         }
/* 299 */         while (--j > 0 && colrel <= 0.0D);
/*     */       } 
/*     */       
/* 302 */       while (colrel < 1.0D && --j >= 0) {
/*     */         int rgb;
/* 304 */         if (this.type == 1) {
/*     */           
/* 306 */           rgb = this.colormap.getColor(map(ImageMath.triangle(colrel)));
/*     */         }
/*     */         else {
/*     */           
/* 310 */           rgb = this.colormap.getColor(map(colrel));
/*     */         } 
/*     */         
/* 313 */         pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 314 */         off++;
/* 315 */         colrel += dx;
/*     */       } 
/*     */       
/* 318 */       if (j > 0) {
/*     */         int rgb;
/* 320 */         if (this.type == 1) {
/*     */           
/* 322 */           rgb = this.colormap.getColor(0.0F);
/*     */         }
/*     */         else {
/*     */           
/* 326 */           rgb = this.colormap.getColor(1.0F);
/*     */         } 
/*     */ 
/*     */         
/*     */         do {
/* 331 */           pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 332 */           off++;
/*     */         }
/* 334 */         while (--j > 0);
/*     */       } 
/*     */       
/* 337 */       rowrel += dy;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void linearGradient(int[] pixels, int y, int w, int h) {
/* 343 */     int x = 0;
/* 344 */     float rowrel = (x - this.x1) * this.dx + (y - this.y1) * this.dy;
/*     */     
/* 346 */     if (this.repeat) {
/*     */       
/* 348 */       repeatGradient(pixels, w, h, rowrel, this.dx, this.dy);
/*     */     }
/*     */     else {
/*     */       
/* 352 */       singleGradient(pixels, w, h, rowrel, this.dx, this.dy);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void radialGradient(int[] pixels, int y, int w, int h) {
/* 358 */     int off = 0;
/* 359 */     float radius = distance((this.p2.x - this.p1.x), (this.p2.y - this.p1.y));
/*     */     
/* 361 */     for (int x = 0; x < w; x++) {
/*     */       
/* 363 */       float distance = distance((x - this.p1.x), (y - this.p1.y));
/* 364 */       float ratio = distance / radius;
/*     */       
/* 366 */       if (this.repeat) {
/*     */         
/* 368 */         ratio %= 2.0F;
/*     */       }
/* 370 */       else if (ratio > 1.0D) {
/*     */         
/* 372 */         ratio = 1.0F;
/*     */       } 
/*     */       
/* 375 */       int rgb = this.colormap.getColor(map(ratio));
/* 376 */       pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 377 */       off++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void squareGradient(int[] pixels, int y, int w, int h) {
/* 383 */     int off = 0;
/* 384 */     float radius = Math.max(Math.abs(this.p2.x - this.p1.x), Math.abs(this.p2.y - this.p1.y));
/*     */     
/* 386 */     for (int x = 0; x < w; x++) {
/*     */       
/* 388 */       float distance = Math.max(Math.abs(x - this.p1.x), Math.abs(y - this.p1.y));
/* 389 */       float ratio = distance / radius;
/*     */       
/* 391 */       if (this.repeat) {
/*     */         
/* 393 */         ratio %= 2.0F;
/*     */       }
/* 395 */       else if (ratio > 1.0D) {
/*     */         
/* 397 */         ratio = 1.0F;
/*     */       } 
/*     */       
/* 400 */       int rgb = this.colormap.getColor(map(ratio));
/* 401 */       pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 402 */       off++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void conicalGradient(int[] pixels, int y, int w, int h) {
/* 408 */     int off = 0;
/* 409 */     float angle0 = (float)Math.atan2((this.p2.x - this.p1.x), (this.p2.y - this.p1.y));
/*     */     
/* 411 */     for (int x = 0; x < w; x++) {
/*     */       
/* 413 */       float angle = (float)(Math.atan2((x - this.p1.x), (y - this.p1.y)) - angle0) / 6.2831855F;
/* 414 */       angle++;
/* 415 */       angle %= 1.0F;
/*     */       
/* 417 */       if (this.type == 4)
/*     */       {
/* 419 */         angle = ImageMath.triangle(angle);
/*     */       }
/*     */       
/* 422 */       int rgb = this.colormap.getColor(map(angle));
/* 423 */       pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
/* 424 */       off++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private float map(float v) {
/* 430 */     if (this.repeat)
/*     */     {
/* 432 */       v = (v > 1.0D) ? (2.0F - v) : v;
/*     */     }
/*     */     
/* 435 */     switch (this.interpolation) {
/*     */       
/*     */       case 1:
/* 438 */         v = ImageMath.circleUp(ImageMath.clamp(v, 0.0F, 1.0F));
/*     */         break;
/*     */       
/*     */       case 2:
/* 442 */         v = ImageMath.circleDown(ImageMath.clamp(v, 0.0F, 1.0F));
/*     */         break;
/*     */       
/*     */       case 3:
/* 446 */         v = ImageMath.smoothStep(0.0F, 1.0F, v);
/*     */         break;
/*     */     } 
/*     */     
/* 450 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   private float distance(float a, float b) {
/* 455 */     return (float)Math.sqrt((a * a + b * b));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 460 */     return "Other/Gradient Fill...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GradientFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */