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
/*     */ public class GammaFilter
/*     */   extends TransferFilter
/*     */ {
/*     */   private float rGamma;
/*     */   private float gGamma;
/*     */   private float bGamma;
/*     */   
/*     */   public GammaFilter() {
/*  34 */     this(1.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GammaFilter(float gamma) {
/*  43 */     this(gamma, gamma, gamma);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GammaFilter(float rGamma, float gGamma, float bGamma) {
/*  54 */     setGamma(rGamma, gGamma, bGamma);
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
/*     */   public void setGamma(float rGamma, float gGamma, float bGamma) {
/*  66 */     this.rGamma = rGamma;
/*  67 */     this.gGamma = gGamma;
/*  68 */     this.bGamma = bGamma;
/*  69 */     this.initialized = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGamma(float gamma) {
/*  79 */     setGamma(gamma, gamma, gamma);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getGamma() {
/*  89 */     return this.rGamma;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initialize() {
/*  94 */     this.rTable = makeTable(this.rGamma);
/*     */     
/*  96 */     if (this.gGamma == this.rGamma) {
/*     */       
/*  98 */       this.gTable = this.rTable;
/*     */     }
/*     */     else {
/*     */       
/* 102 */       this.gTable = makeTable(this.gGamma);
/*     */     } 
/*     */     
/* 105 */     if (this.bGamma == this.rGamma) {
/*     */       
/* 107 */       this.bTable = this.rTable;
/*     */     }
/* 109 */     else if (this.bGamma == this.gGamma) {
/*     */       
/* 111 */       this.bTable = this.gTable;
/*     */     }
/*     */     else {
/*     */       
/* 115 */       this.bTable = makeTable(this.bGamma);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private int[] makeTable(float gamma) {
/* 121 */     int[] table = new int[256];
/*     */     
/* 123 */     for (int i = 0; i < 256; i++) {
/*     */       
/* 125 */       int v = (int)(255.0D * Math.pow(i / 255.0D, 1.0D / gamma) + 0.5D);
/*     */       
/* 127 */       if (v > 255)
/*     */       {
/* 129 */         v = 255;
/*     */       }
/*     */       
/* 132 */       table[i] = v;
/*     */     } 
/*     */     
/* 135 */     return table;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return "Colors/Gamma...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GammaFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */