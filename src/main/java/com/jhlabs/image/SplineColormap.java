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
/*     */ public class SplineColormap
/*     */   extends ArrayColormap
/*     */ {
/*  30 */   private int numKnots = 4;
/*  31 */   private int[] xKnots = new int[] { 0, 0, 255, 255 };
/*     */ 
/*     */ 
/*     */   
/*  35 */   private int[] yKnots = new int[] { -16777216, -16777216, -1, -1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplineColormap() {
/*  45 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplineColormap(int[] xKnots, int[] yKnots) {
/*  55 */     this.xKnots = xKnots;
/*  56 */     this.yKnots = yKnots;
/*  57 */     this.numKnots = xKnots.length;
/*  58 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKnot(int n, int color) {
/*  69 */     this.yKnots[n] = color;
/*  70 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getKnot(int n) {
/*  81 */     return this.yKnots[n];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addKnot(int x, int color) {
/*  92 */     int[] nx = new int[this.numKnots + 1];
/*  93 */     int[] ny = new int[this.numKnots + 1];
/*  94 */     System.arraycopy(this.xKnots, 0, nx, 0, this.numKnots);
/*  95 */     System.arraycopy(this.yKnots, 0, ny, 0, this.numKnots);
/*  96 */     this.xKnots = nx;
/*  97 */     this.yKnots = ny;
/*  98 */     this.xKnots[this.numKnots] = x;
/*  99 */     this.yKnots[this.numKnots] = color;
/* 100 */     this.numKnots++;
/* 101 */     sortKnots();
/* 102 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeKnot(int n) {
/* 112 */     if (this.numKnots <= 4) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 117 */     if (n < this.numKnots - 1) {
/*     */       
/* 119 */       System.arraycopy(this.xKnots, n + 1, this.xKnots, n, this.numKnots - n - 1);
/* 120 */       System.arraycopy(this.yKnots, n + 1, this.yKnots, n, this.numKnots - n - 1);
/*     */     } 
/*     */     
/* 123 */     this.numKnots--;
/* 124 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKnotPosition(int n, int x) {
/* 134 */     this.xKnots[n] = PixelUtils.clamp(x);
/* 135 */     sortKnots();
/* 136 */     rebuildGradient();
/*     */   }
/*     */ 
/*     */   
/*     */   private void rebuildGradient() {
/* 141 */     this.xKnots[0] = -1;
/* 142 */     this.xKnots[this.numKnots - 1] = 256;
/* 143 */     this.yKnots[0] = this.yKnots[1];
/* 144 */     this.yKnots[this.numKnots - 1] = this.yKnots[this.numKnots - 2];
/*     */     
/* 146 */     for (int i = 0; i < 256; i++)
/*     */     {
/* 148 */       this.map[i] = ImageMath.colorSpline(i, this.numKnots, this.xKnots, this.yKnots);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void sortKnots() {
/* 154 */     for (int i = 1; i < this.numKnots; i++) {
/*     */       
/* 156 */       for (int j = 1; j < i; j++) {
/*     */         
/* 158 */         if (this.xKnots[i] < this.xKnots[j]) {
/*     */           
/* 160 */           int t = this.xKnots[i];
/* 161 */           this.xKnots[i] = this.xKnots[j];
/* 162 */           this.xKnots[j] = t;
/* 163 */           t = this.yKnots[i];
/* 164 */           this.yKnots[i] = this.yKnots[j];
/* 165 */           this.yKnots[j] = t;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SplineColormap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */