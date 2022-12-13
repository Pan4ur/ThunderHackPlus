/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import com.jhlabs.math.CellularFunction2D;
/*     */ import com.jhlabs.math.FBM;
/*     */ import com.jhlabs.math.Function2D;
/*     */ import com.jhlabs.math.Noise;
/*     */ import com.jhlabs.math.RidgedFBM;
/*     */ import com.jhlabs.math.SCNoise;
/*     */ import com.jhlabs.math.VLNoise;
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
/*     */ public class FBMFilter
/*     */   extends PointFilter
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int NOISE = 0;
/*     */   public static final int RIDGED = 1;
/*     */   public static final int VLNOISE = 2;
/*     */   public static final int SCNOISE = 3;
/*     */   public static final int CELLULAR = 4;
/*  34 */   private float scale = 32.0F;
/*  35 */   private float stretch = 1.0F;
/*  36 */   private float angle = 0.0F;
/*  37 */   private float amount = 1.0F;
/*  38 */   private float H = 1.0F;
/*  39 */   private float octaves = 4.0F;
/*  40 */   private float lacunarity = 2.0F;
/*  41 */   private float gain = 0.5F;
/*  42 */   private float bias = 0.5F;
/*     */   private int operation;
/*  44 */   private float m00 = 1.0F;
/*  45 */   private float m01 = 0.0F;
/*  46 */   private float m10 = 0.0F;
/*  47 */   private float m11 = 1.0F;
/*     */   private float min;
/*     */   private float max;
/*  50 */   private Colormap colormap = new Gradient();
/*     */   private boolean ridged;
/*     */   private FBM fBm;
/*  53 */   protected Random random = new Random();
/*  54 */   private int basisType = 0;
/*     */   
/*     */   private Function2D basis;
/*     */   
/*     */   public FBMFilter() {
/*  59 */     setBasisType(0);
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
/*     */   public void setAmount(float amount) {
/*  71 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  81 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOperation(int operation) {
/*  86 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOperation() {
/*  91 */     return this.operation;
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
/*     */   public void setScale(float scale) {
/* 103 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getScale() {
/* 113 */     return this.scale;
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
/*     */   public void setStretch(float stretch) {
/* 125 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getStretch() {
/* 135 */     return this.stretch;
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
/* 146 */     this.angle = angle;
/* 147 */     float cos = (float)Math.cos(this.angle);
/* 148 */     float sin = (float)Math.sin(this.angle);
/* 149 */     this.m00 = cos;
/* 150 */     this.m01 = sin;
/* 151 */     this.m10 = -sin;
/* 152 */     this.m11 = cos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/* 162 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOctaves(float octaves) {
/* 167 */     this.octaves = octaves;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOctaves() {
/* 172 */     return this.octaves;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setH(float H) {
/* 177 */     this.H = H;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getH() {
/* 182 */     return this.H;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLacunarity(float lacunarity) {
/* 187 */     this.lacunarity = lacunarity;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLacunarity() {
/* 192 */     return this.lacunarity;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGain(float gain) {
/* 197 */     this.gain = gain;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getGain() {
/* 202 */     return this.gain;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBias(float bias) {
/* 207 */     this.bias = bias;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getBias() {
/* 212 */     return this.bias;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 222 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 232 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBasisType(int basisType) {
/* 237 */     this.basisType = basisType;
/*     */     
/* 239 */     switch (basisType) {
/*     */ 
/*     */       
/*     */       default:
/* 243 */         this.basis = (Function2D)new Noise();
/*     */         return;
/*     */       
/*     */       case 1:
/* 247 */         this.basis = (Function2D)new RidgedFBM();
/*     */         return;
/*     */       
/*     */       case 2:
/* 251 */         this.basis = (Function2D)new VLNoise();
/*     */         return;
/*     */       
/*     */       case 3:
/* 255 */         this.basis = (Function2D)new SCNoise(); return;
/*     */       case 4:
/*     */         break;
/*     */     } 
/* 259 */     this.basis = (Function2D)new CellularFunction2D();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBasisType() {
/* 266 */     return this.basisType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBasis(Function2D basis) {
/* 271 */     this.basis = basis;
/*     */   }
/*     */ 
/*     */   
/*     */   public Function2D getBasis() {
/* 276 */     return this.basis;
/*     */   }
/*     */ 
/*     */   
/*     */   protected FBM makeFBM(float H, float lacunarity, float octaves) {
/* 281 */     FBM fbm = new FBM(H, lacunarity, octaves, this.basis);
/* 282 */     float[] minmax = Noise.findRange((Function2D)fbm, null);
/* 283 */     this.min = minmax[0];
/* 284 */     this.max = minmax[1];
/* 285 */     return fbm;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 290 */     this.fBm = makeFBM(this.H, this.lacunarity, this.octaves);
/* 291 */     return super.filter(src, dst);
/*     */   }
/*     */   
/*     */   public int filterRGB(int x, int y, int rgb) {
/*     */     int v;
/* 296 */     float nx = this.m00 * x + this.m01 * y;
/* 297 */     float ny = this.m10 * x + this.m11 * y;
/* 298 */     nx /= this.scale;
/* 299 */     ny /= this.scale * this.stretch;
/* 300 */     float f = this.fBm.evaluate(nx, ny);
/*     */     
/* 302 */     f = (f - this.min) / (this.max - this.min);
/* 303 */     f = ImageMath.gain(f, this.gain);
/* 304 */     f = ImageMath.bias(f, this.bias);
/* 305 */     f *= this.amount;
/* 306 */     int a = rgb & 0xFF000000;
/*     */ 
/*     */     
/* 309 */     if (this.colormap != null) {
/*     */       
/* 311 */       v = this.colormap.getColor(f);
/*     */     }
/*     */     else {
/*     */       
/* 315 */       v = PixelUtils.clamp((int)(f * 255.0F));
/* 316 */       int r = v << 16;
/* 317 */       int g = v << 8;
/* 318 */       int b = v;
/* 319 */       v = a | r | g | b;
/*     */     } 
/*     */     
/* 322 */     if (this.operation != 0)
/*     */     {
/* 324 */       v = PixelUtils.combinePixels(rgb, v, this.operation);
/*     */     }
/*     */     
/* 327 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 332 */     return "Texture/Fractal Brownian Motion...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FBMFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */