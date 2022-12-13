/*    */ package com.jhlabs.image;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PosterizeFilter
/*    */   extends PointFilter
/*    */ {
/*    */   private int numLevels;
/*    */   private int[] levels;
/*    */   private boolean initialized = false;
/*    */   
/*    */   public PosterizeFilter() {
/* 33 */     setNumLevels(6);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setNumLevels(int numLevels) {
/* 43 */     this.numLevels = numLevels;
/* 44 */     this.initialized = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumLevels() {
/* 54 */     return this.numLevels;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void initialize() {
/* 62 */     this.levels = new int[256];
/*    */     
/* 64 */     if (this.numLevels != 1) {
/* 65 */       for (int i = 0; i < 256; i++)
/*    */       {
/* 67 */         this.levels[i] = 255 * this.numLevels * i / 256 / (this.numLevels - 1);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 73 */     if (!this.initialized) {
/*    */       
/* 75 */       this.initialized = true;
/* 76 */       initialize();
/*    */     } 
/*    */     
/* 79 */     int a = rgb & 0xFF000000;
/* 80 */     int r = rgb >> 16 & 0xFF;
/* 81 */     int g = rgb >> 8 & 0xFF;
/* 82 */     int b = rgb & 0xFF;
/* 83 */     r = this.levels[r];
/* 84 */     g = this.levels[g];
/* 85 */     b = this.levels[b];
/* 86 */     return a | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 91 */     return "Colors/Posterize...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PosterizeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */