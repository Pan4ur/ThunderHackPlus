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
/*     */ 
/*     */ public class ArrayColormap
/*     */   implements Colormap, Cloneable
/*     */ {
/*     */   protected int[] map;
/*     */   
/*     */   public ArrayColormap() {
/*  34 */     this.map = new int[256];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayColormap(int[] map) {
/*  43 */     this.map = map;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/*  50 */       ArrayColormap g = (ArrayColormap)super.clone();
/*  51 */       g.map = (int[])this.map.clone();
/*  52 */       return g;
/*     */     }
/*  54 */     catch (CloneNotSupportedException cloneNotSupportedException) {
/*     */ 
/*     */ 
/*     */       
/*  58 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMap(int[] map) {
/*  68 */     this.map = map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getMap() {
/*  78 */     return this.map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColor(float v) {
/*  99 */     int n = (int)(v * 255.0F);
/*     */     
/* 101 */     if (n < 0) {
/*     */       
/* 103 */       n = 0;
/*     */     }
/* 105 */     else if (n > 255) {
/*     */       
/* 107 */       n = 255;
/*     */     } 
/*     */     
/* 110 */     return this.map[n];
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
/*     */ 
/*     */   
/*     */   public void setColorInterpolated(int index, int firstIndex, int lastIndex, int color) {
/* 124 */     int firstColor = this.map[firstIndex];
/* 125 */     int lastColor = this.map[lastIndex];
/*     */     int i;
/* 127 */     for (i = firstIndex; i <= index; i++)
/*     */     {
/* 129 */       this.map[i] = ImageMath.mixColors((i - firstIndex) / (index - firstIndex), firstColor, color);
/*     */     }
/*     */     
/* 132 */     for (i = index; i < lastIndex; i++)
/*     */     {
/* 134 */       this.map[i] = ImageMath.mixColors((i - index) / (lastIndex - index), color, lastColor);
/*     */     }
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
/*     */   public void setColorRange(int firstIndex, int lastIndex, int color1, int color2) {
/* 147 */     for (int i = firstIndex; i <= lastIndex; i++)
/*     */     {
/* 149 */       this.map[i] = ImageMath.mixColors((i - firstIndex) / (lastIndex - firstIndex), color1, color2);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColorRange(int firstIndex, int lastIndex, int color) {
/* 161 */     for (int i = firstIndex; i <= lastIndex; i++)
/*     */     {
/* 163 */       this.map[i] = color;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColor(int index, int color) {
/* 175 */     this.map[index] = color;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ArrayColormap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */