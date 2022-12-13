/*     */ package com.jhlabs.math;
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
/*     */ public class FFT
/*     */ {
/*     */   protected float[] w1;
/*     */   protected float[] w2;
/*     */   protected float[] w3;
/*     */   
/*     */   public FFT(int logN) {
/*  29 */     this.w1 = new float[logN];
/*  30 */     this.w2 = new float[logN];
/*  31 */     this.w3 = new float[logN];
/*  32 */     int N = 1;
/*     */     
/*  34 */     for (int k = 0; k < logN; k++) {
/*     */       
/*  36 */       N <<= 1;
/*  37 */       double angle = -6.283185307179586D / N;
/*  38 */       this.w1[k] = (float)Math.sin(0.5D * angle);
/*  39 */       this.w2[k] = -2.0F * this.w1[k] * this.w1[k];
/*  40 */       this.w3[k] = (float)Math.sin(angle);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void scramble(int n, float[] real, float[] imag) {
/*  46 */     int j = 0;
/*     */     
/*  48 */     for (int i = 0; i < n; i++) {
/*     */       
/*  50 */       if (i > j) {
/*     */ 
/*     */         
/*  53 */         float t = real[j];
/*  54 */         real[j] = real[i];
/*  55 */         real[i] = t;
/*  56 */         t = imag[j];
/*  57 */         imag[j] = imag[i];
/*  58 */         imag[i] = t;
/*     */       } 
/*     */       
/*  61 */       int m = n >> 1;
/*     */       
/*  63 */       while (j >= m && m >= 2) {
/*     */         
/*  65 */         j -= m;
/*  66 */         m >>= 1;
/*     */       } 
/*     */       
/*  69 */       j += m;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void butterflies(int n, int logN, int direction, float[] real, float[] imag) {
/*  75 */     int N = 1;
/*     */     
/*  77 */     for (int k = 0; k < logN; k++) {
/*     */ 
/*     */       
/*  80 */       int half_N = N;
/*  81 */       N <<= 1;
/*  82 */       float wt = direction * this.w1[k];
/*  83 */       float wp_re = this.w2[k];
/*  84 */       float wp_im = direction * this.w3[k];
/*  85 */       float w_re = 1.0F;
/*  86 */       float w_im = 0.0F;
/*     */       
/*  88 */       for (int offset = 0; offset < half_N; offset++) {
/*     */         int i;
/*  90 */         for (i = offset; i < n; i += N) {
/*     */           
/*  92 */           int j = i + half_N;
/*  93 */           float re = real[j];
/*  94 */           float im = imag[j];
/*  95 */           float temp_re = w_re * re - w_im * im;
/*  96 */           float temp_im = w_im * re + w_re * im;
/*  97 */           real[j] = real[i] - temp_re;
/*  98 */           real[i] = real[i] + temp_re;
/*  99 */           imag[j] = imag[i] - temp_im;
/* 100 */           imag[i] = imag[i] + temp_im;
/*     */         } 
/*     */         
/* 103 */         wt = w_re;
/* 104 */         w_re = wt * wp_re - w_im * wp_im + w_re;
/* 105 */         w_im = w_im * wp_re + wt * wp_im + w_im;
/*     */       } 
/*     */     } 
/*     */     
/* 109 */     if (direction == -1) {
/*     */       
/* 111 */       float nr = 1.0F / n;
/*     */       
/* 113 */       for (int i = 0; i < n; i++) {
/*     */         
/* 115 */         real[i] = real[i] * nr;
/* 116 */         imag[i] = imag[i] * nr;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void transform1D(float[] real, float[] imag, int logN, int n, boolean forward) {
/* 123 */     scramble(n, real, imag);
/* 124 */     butterflies(n, logN, forward ? 1 : -1, real, imag);
/*     */   }
/*     */ 
/*     */   
/*     */   public void transform2D(float[] real, float[] imag, int cols, int rows, boolean forward) {
/* 129 */     int log2cols = log2(cols);
/* 130 */     int log2rows = log2(rows);
/* 131 */     int n = Math.max(rows, cols);
/* 132 */     float[] rtemp = new float[n];
/* 133 */     float[] itemp = new float[n];
/*     */ 
/*     */     
/* 136 */     for (int y = 0; y < rows; y++) {
/*     */       
/* 138 */       int offset = y * cols;
/* 139 */       System.arraycopy(real, offset, rtemp, 0, cols);
/* 140 */       System.arraycopy(imag, offset, itemp, 0, cols);
/* 141 */       transform1D(rtemp, itemp, log2cols, cols, forward);
/* 142 */       System.arraycopy(rtemp, 0, real, offset, cols);
/* 143 */       System.arraycopy(itemp, 0, imag, offset, cols);
/*     */     } 
/*     */ 
/*     */     
/* 147 */     for (int x = 0; x < cols; x++) {
/*     */       
/* 149 */       int index = x;
/*     */       int i;
/* 151 */       for (i = 0; i < rows; i++) {
/*     */         
/* 153 */         rtemp[i] = real[index];
/* 154 */         itemp[i] = imag[index];
/* 155 */         index += cols;
/*     */       } 
/*     */       
/* 158 */       transform1D(rtemp, itemp, log2rows, rows, forward);
/* 159 */       index = x;
/*     */       
/* 161 */       for (i = 0; i < rows; i++) {
/*     */         
/* 163 */         real[index] = rtemp[i];
/* 164 */         imag[index] = itemp[i];
/* 165 */         index += cols;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private int log2(int n) {
/* 172 */     int m = 1;
/* 173 */     int log2n = 0;
/*     */     
/* 175 */     while (m < n) {
/*     */       
/* 177 */       m *= 2;
/* 178 */       log2n++;
/*     */     } 
/*     */     
/* 181 */     return (m == n) ? log2n : -1;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\FFT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */