/*     */ package com.jhlabs.image;
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
/*     */ 
/*     */ 
/*     */ public class Histogram
/*     */ {
/*     */   public static final int RED = 0;
/*     */   public static final int GREEN = 1;
/*     */   public static final int BLUE = 2;
/*     */   public static final int GRAY = 3;
/*     */   protected int[][] histogram;
/*     */   protected int numSamples;
/*     */   protected int[] minValue;
/*     */   protected int[] maxValue;
/*     */   protected int[] minFrequency;
/*     */   protected int[] maxFrequency;
/*     */   protected float[] mean;
/*     */   protected boolean isGray;
/*     */   
/*     */   public Histogram() {
/*  43 */     this.histogram = (int[][])null;
/*  44 */     this.numSamples = 0;
/*  45 */     this.isGray = true;
/*  46 */     this.minValue = null;
/*  47 */     this.maxValue = null;
/*  48 */     this.minFrequency = null;
/*  49 */     this.maxFrequency = null;
/*  50 */     this.mean = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Histogram(int[] pixels, int w, int h, int offset, int stride) {
/*  55 */     this.histogram = new int[3][256];
/*  56 */     this.minValue = new int[4];
/*  57 */     this.maxValue = new int[4];
/*  58 */     this.minFrequency = new int[3];
/*  59 */     this.maxFrequency = new int[3];
/*  60 */     this.mean = new float[3];
/*  61 */     this.numSamples = w * h;
/*  62 */     this.isGray = true;
/*  63 */     int index = 0;
/*     */     
/*  65 */     for (int y = 0; y < h; y++) {
/*     */       
/*  67 */       index = offset + y * stride;
/*     */       
/*  69 */       for (int x = 0; x < w; x++) {
/*     */         
/*  71 */         int rgb = pixels[index++];
/*  72 */         int r = rgb >> 16 & 0xFF;
/*  73 */         int g = rgb >> 8 & 0xFF;
/*  74 */         int b = rgb & 0xFF;
/*  75 */         this.histogram[0][r] = this.histogram[0][r] + 1;
/*  76 */         this.histogram[1][g] = this.histogram[1][g] + 1;
/*  77 */         this.histogram[2][b] = this.histogram[2][b] + 1;
/*     */       } 
/*     */     } 
/*     */     int i;
/*  81 */     for (i = 0; i < 256; i++) {
/*     */       
/*  83 */       if (this.histogram[0][i] != this.histogram[1][i] || this.histogram[1][i] != this.histogram[2][i]) {
/*     */         
/*  85 */         this.isGray = false;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  90 */     for (i = 0; i < 3; i++) {
/*     */       int j;
/*  92 */       for (j = 0; j < 256; j++) {
/*     */         
/*  94 */         if (this.histogram[i][j] > 0) {
/*     */           
/*  96 */           this.minValue[i] = j;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 101 */       for (j = 255; j >= 0; j--) {
/*     */         
/* 103 */         if (this.histogram[i][j] > 0) {
/*     */           
/* 105 */           this.maxValue[i] = j;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 110 */       this.minFrequency[i] = Integer.MAX_VALUE;
/* 111 */       this.maxFrequency[i] = 0;
/*     */       
/* 113 */       for (j = 0; j < 256; j++) {
/*     */         
/* 115 */         this.minFrequency[i] = Math.min(this.minFrequency[i], this.histogram[i][j]);
/* 116 */         this.maxFrequency[i] = Math.max(this.maxFrequency[i], this.histogram[i][j]);
/* 117 */         this.mean[i] = this.mean[i] + (j * this.histogram[i][j]);
/*     */       } 
/*     */       
/* 120 */       this.mean[i] = this.mean[i] / this.numSamples;
/*     */     } 
/*     */     
/* 123 */     this.minValue[3] = Math.min(Math.min(this.minValue[0], this.minValue[1]), this.minValue[2]);
/* 124 */     this.maxValue[3] = Math.max(Math.max(this.maxValue[0], this.maxValue[1]), this.maxValue[2]);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isGray() {
/* 129 */     return this.isGray;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumSamples() {
/* 134 */     return this.numSamples;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFrequency(int value) {
/* 139 */     if (this.numSamples > 0 && this.isGray && value >= 0 && value <= 255)
/*     */     {
/* 141 */       return this.histogram[0][value];
/*     */     }
/*     */     
/* 144 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFrequency(int channel, int value) {
/* 149 */     if (this.numSamples < 1 || channel < 0 || channel > 2 || value < 0 || value > 255)
/*     */     {
/*     */       
/* 152 */       return -1;
/*     */     }
/*     */     
/* 155 */     return this.histogram[channel][value];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinFrequency() {
/* 160 */     if (this.numSamples > 0 && this.isGray)
/*     */     {
/* 162 */       return this.minFrequency[0];
/*     */     }
/*     */     
/* 165 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinFrequency(int channel) {
/* 170 */     if (this.numSamples < 1 || channel < 0 || channel > 2)
/*     */     {
/* 172 */       return -1;
/*     */     }
/*     */     
/* 175 */     return this.minFrequency[channel];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxFrequency() {
/* 180 */     if (this.numSamples > 0 && this.isGray)
/*     */     {
/* 182 */       return this.maxFrequency[0];
/*     */     }
/*     */     
/* 185 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxFrequency(int channel) {
/* 190 */     if (this.numSamples < 1 || channel < 0 || channel > 2)
/*     */     {
/* 192 */       return -1;
/*     */     }
/*     */     
/* 195 */     return this.maxFrequency[channel];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinValue() {
/* 200 */     if (this.numSamples > 0 && this.isGray)
/*     */     {
/* 202 */       return this.minValue[0];
/*     */     }
/*     */     
/* 205 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMinValue(int channel) {
/* 210 */     return this.minValue[channel];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxValue() {
/* 215 */     if (this.numSamples > 0 && this.isGray)
/*     */     {
/* 217 */       return this.maxValue[0];
/*     */     }
/*     */     
/* 220 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxValue(int channel) {
/* 225 */     return this.maxValue[channel];
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMeanValue() {
/* 230 */     if (this.numSamples > 0 && this.isGray)
/*     */     {
/* 232 */       return this.mean[0];
/*     */     }
/*     */     
/* 235 */     return -1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMeanValue(int channel) {
/* 240 */     if (this.numSamples > 0 && 0 <= channel && channel <= 2)
/*     */     {
/* 242 */       return this.mean[channel];
/*     */     }
/*     */     
/* 245 */     return -1.0F;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\Histogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */