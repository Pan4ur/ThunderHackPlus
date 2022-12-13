/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.FBM;
/*     */ import com.jhlabs.math.Function2D;
/*     */ import com.jhlabs.math.Noise;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public class SkyFilter
/*     */   extends PointFilter
/*     */ {
/*  26 */   private float scale = 0.1F;
/*  27 */   private float stretch = 1.0F;
/*  28 */   private float angle = 0.0F;
/*  29 */   private float amount = 1.0F;
/*  30 */   private float H = 1.0F;
/*  31 */   private float octaves = 8.0F;
/*  32 */   private float lacunarity = 2.0F;
/*  33 */   private float gain = 1.0F;
/*  34 */   private float bias = 0.6F;
/*     */   private int operation;
/*     */   private float min;
/*     */   private float max;
/*     */   private boolean ridged;
/*     */   private FBM fBm;
/*  40 */   protected Random random = new Random();
/*     */   
/*     */   private Function2D basis;
/*  43 */   private float cloudCover = 0.5F;
/*  44 */   private float cloudSharpness = 0.5F;
/*  45 */   private float time = 0.3F;
/*  46 */   private float glow = 0.5F;
/*  47 */   private float glowFalloff = 0.5F;
/*  48 */   private float haziness = 0.96F;
/*  49 */   private float t = 0.0F;
/*  50 */   private float sunRadius = 10.0F;
/*  51 */   private int sunColor = -1;
/*     */   private float sunR;
/*  53 */   private float sunAzimuth = 0.5F; private float sunG; private float sunB;
/*  54 */   private float sunElevation = 0.5F;
/*  55 */   private float windSpeed = 0.0F;
/*     */   
/*  57 */   private float cameraAzimuth = 0.0F;
/*  58 */   private float cameraElevation = 0.0F;
/*  59 */   private float fov = 1.0F;
/*     */   
/*     */   private float[] exponents;
/*     */   private float[] tan;
/*     */   private BufferedImage skyColors;
/*     */   private int[] skyPixels;
/*     */   private static final float r255 = 0.003921569F;
/*     */   private float width;
/*     */   private float height;
/*     */   float mn;
/*     */   float mx;
/*     */   
/*     */   public SkyFilter() {
/*  72 */     if (this.skyColors == null)
/*     */     {
/*  74 */       this.skyColors = ImageUtils.createImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("SkyColors.png")).getSource());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAmount(float amount) {
/*  80 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  85 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOperation(int operation) {
/*  90 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOperation() {
/*  95 */     return this.operation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setScale(float scale) {
/* 100 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 105 */     return this.scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStretch(float stretch) {
/* 110 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 115 */     return this.stretch;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setT(float t) {
/* 120 */     this.t = t;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getT() {
/* 125 */     return this.t;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFOV(float fov) {
/* 130 */     this.fov = fov;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFOV() {
/* 135 */     return this.fov;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCloudCover(float cloudCover) {
/* 140 */     this.cloudCover = cloudCover;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCloudCover() {
/* 145 */     return this.cloudCover;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCloudSharpness(float cloudSharpness) {
/* 150 */     this.cloudSharpness = cloudSharpness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCloudSharpness() {
/* 155 */     return this.cloudSharpness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(float time) {
/* 160 */     this.time = time;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTime() {
/* 165 */     return this.time;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGlow(float glow) {
/* 170 */     this.glow = glow;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGlow() {
/* 175 */     return this.glow;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGlowFalloff(float glowFalloff) {
/* 180 */     this.glowFalloff = glowFalloff;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGlowFalloff() {
/* 185 */     return this.glowFalloff;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/* 190 */     this.angle = angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 195 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOctaves(float octaves) {
/* 200 */     this.octaves = octaves;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOctaves() {
/* 205 */     return this.octaves;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setH(float H) {
/* 210 */     this.H = H;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getH() {
/* 215 */     return this.H;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLacunarity(float lacunarity) {
/* 220 */     this.lacunarity = lacunarity;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLacunarity() {
/* 225 */     return this.lacunarity;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGain(float gain) {
/* 230 */     this.gain = gain;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGain() {
/* 235 */     return this.gain;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBias(float bias) {
/* 240 */     this.bias = bias;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBias() {
/* 245 */     return this.bias;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHaziness(float haziness) {
/* 250 */     this.haziness = haziness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHaziness() {
/* 255 */     return this.haziness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSunElevation(float sunElevation) {
/* 260 */     this.sunElevation = sunElevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSunElevation() {
/* 265 */     return this.sunElevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSunAzimuth(float sunAzimuth) {
/* 270 */     this.sunAzimuth = sunAzimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSunAzimuth() {
/* 275 */     return this.sunAzimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSunColor(int sunColor) {
/* 280 */     this.sunColor = sunColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSunColor() {
/* 285 */     return this.sunColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCameraElevation(float cameraElevation) {
/* 290 */     this.cameraElevation = cameraElevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCameraElevation() {
/* 295 */     return this.cameraElevation;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCameraAzimuth(float cameraAzimuth) {
/* 300 */     this.cameraAzimuth = cameraAzimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getCameraAzimuth() {
/* 305 */     return this.cameraAzimuth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWindSpeed(float windSpeed) {
/* 310 */     this.windSpeed = windSpeed;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWindSpeed() {
/* 315 */     return this.windSpeed;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 321 */     long start = System.currentTimeMillis();
/* 322 */     this.sunR = (this.sunColor >> 16 & 0xFF) * 0.003921569F;
/* 323 */     this.sunG = (this.sunColor >> 8 & 0xFF) * 0.003921569F;
/* 324 */     this.sunB = (this.sunColor & 0xFF) * 0.003921569F;
/* 325 */     this.mn = 10000.0F;
/* 326 */     this.mx = -10000.0F;
/* 327 */     this.exponents = new float[(int)this.octaves + 1];
/* 328 */     float frequency = 1.0F;
/*     */     
/* 330 */     for (int i = 0; i <= (int)this.octaves; i++) {
/*     */       
/* 332 */       this.exponents[i] = (float)Math.pow(2.0D, -i);
/* 333 */       frequency *= this.lacunarity;
/*     */     } 
/*     */     
/* 336 */     this.min = -1.0F;
/* 337 */     this.max = 1.0F;
/*     */ 
/*     */     
/* 340 */     this.width = src.getWidth();
/* 341 */     this.height = src.getHeight();
/* 342 */     int h = src.getHeight();
/* 343 */     this.tan = new float[h];
/*     */     
/* 345 */     for (int j = 0; j < h; j++)
/*     */     {
/* 347 */       this.tan[j] = (float)Math.tan((this.fov * j / h) * Math.PI * 0.5D);
/*     */     }
/*     */     
/* 350 */     if (dst == null)
/*     */     {
/* 352 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 355 */     int t = (int)(63.0F * this.time);
/*     */     
/* 357 */     Graphics2D g = dst.createGraphics();
/* 358 */     g.drawImage(this.skyColors, 0, 0, dst.getWidth(), dst.getHeight(), t, 0, t + 1, 64, null);
/* 359 */     g.dispose();
/* 360 */     BufferedImage clouds = super.filter(dst, dst);
/*     */ 
/*     */     
/* 363 */     long finish = System.currentTimeMillis();
/* 364 */     System.out.println(this.mn + " " + this.mx + " " + ((float)(finish - start) * 0.001F));
/* 365 */     this.exponents = null;
/* 366 */     this.tan = null;
/* 367 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public float evaluate(float x, float y) {
/* 372 */     float value = 0.0F;
/*     */ 
/*     */ 
/*     */     
/* 376 */     x += 371.0F;
/* 377 */     y += 529.0F;
/*     */     int i;
/* 379 */     for (i = 0; i < (int)this.octaves; i++) {
/*     */       
/* 381 */       value += Noise.noise3(x, y, this.t) * this.exponents[i];
/* 382 */       x *= this.lacunarity;
/* 383 */       y *= this.lacunarity;
/*     */     } 
/*     */     
/* 386 */     float remainder = this.octaves - (int)this.octaves;
/*     */     
/* 388 */     if (remainder != 0.0F)
/*     */     {
/* 390 */       value += remainder * Noise.noise3(x, y, this.t) * this.exponents[i];
/*     */     }
/*     */     
/* 393 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/* 399 */     float fx = x / this.width;
/*     */     
/* 401 */     float fy = y / this.height;
/* 402 */     float haze = (float)Math.pow(this.haziness, (100.0F * fy * fy));
/*     */     
/* 404 */     float r = (rgb >> 16 & 0xFF) * 0.003921569F;
/* 405 */     float g = (rgb >> 8 & 0xFF) * 0.003921569F;
/* 406 */     float b = (rgb & 0xFF) * 0.003921569F;
/* 407 */     float cx = this.width * 0.5F;
/* 408 */     float nx = x - cx;
/* 409 */     float ny = y;
/*     */ 
/*     */     
/* 412 */     ny = this.tan[y];
/* 413 */     nx = (fx - 0.5F) * (1.0F + ny);
/* 414 */     ny += this.t * this.windSpeed;
/*     */     
/* 416 */     nx /= this.scale;
/* 417 */     ny /= this.scale * this.stretch;
/* 418 */     float f = evaluate(nx, ny);
/* 419 */     float fg = f;
/*     */ 
/*     */     
/* 422 */     f = (f + 1.23F) / 2.46F;
/*     */     
/* 424 */     int a = rgb & 0xFF000000;
/*     */ 
/*     */     
/* 427 */     float c = f - this.cloudCover;
/*     */     
/* 429 */     if (c < 0.0F)
/*     */     {
/* 431 */       c = 0.0F;
/*     */     }
/*     */     
/* 434 */     float cloudAlpha = 1.0F - (float)Math.pow(this.cloudSharpness, c);
/*     */ 
/*     */ 
/*     */     
/* 438 */     this.mn = Math.min(this.mn, cloudAlpha);
/* 439 */     this.mx = Math.max(this.mx, cloudAlpha);
/*     */     
/* 441 */     float centreX = this.width * this.sunAzimuth;
/* 442 */     float centreY = this.height * this.sunElevation;
/* 443 */     float dx = x - centreX;
/* 444 */     float dy = y - centreY;
/* 445 */     float distance2 = dx * dx + dy * dy;
/*     */ 
/*     */     
/* 448 */     distance2 = (float)Math.pow(distance2, this.glowFalloff);
/* 449 */     float sun = 10.0F * (float)Math.exp((-distance2 * this.glow * 0.1F));
/*     */ 
/*     */     
/* 452 */     r += sun * this.sunR;
/* 453 */     g += sun * this.sunG;
/* 454 */     b += sun * this.sunB;
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
/* 474 */     float ca = (1.0F - cloudAlpha * cloudAlpha * cloudAlpha * cloudAlpha) * this.amount;
/* 475 */     float cloudR = this.sunR * ca;
/* 476 */     float cloudG = this.sunG * ca;
/* 477 */     float cloudB = this.sunB * ca;
/*     */     
/* 479 */     cloudAlpha *= haze;
/*     */     
/* 481 */     float iCloudAlpha = 1.0F - cloudAlpha;
/* 482 */     r = iCloudAlpha * r + cloudAlpha * cloudR;
/* 483 */     g = iCloudAlpha * g + cloudAlpha * cloudG;
/* 484 */     b = iCloudAlpha * b + cloudAlpha * cloudB;
/*     */     
/* 486 */     float exposure = this.gain;
/* 487 */     r = 1.0F - (float)Math.exp((-r * exposure));
/* 488 */     g = 1.0F - (float)Math.exp((-g * exposure));
/* 489 */     b = 1.0F - (float)Math.exp((-b * exposure));
/* 490 */     int ir = (int)(255.0F * r) << 16;
/* 491 */     int ig = (int)(255.0F * g) << 8;
/* 492 */     int ib = (int)(255.0F * b);
/* 493 */     int v = 0xFF000000 | ir | ig | ib;
/* 494 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 499 */     return "Texture/Sky...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SkyFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */