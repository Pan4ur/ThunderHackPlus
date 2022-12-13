/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
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
/*     */ 
/*     */ public class WarpFilter
/*     */   extends WholeImageFilter
/*     */ {
/*     */   private WarpGrid sourceGrid;
/*     */   private WarpGrid destGrid;
/*  34 */   private int frames = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage morphImage;
/*     */ 
/*     */ 
/*     */   
/*     */   private float time;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WarpFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WarpFilter(WarpGrid sourceGrid, WarpGrid destGrid) {
/*  53 */     this.sourceGrid = sourceGrid;
/*  54 */     this.destGrid = destGrid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSourceGrid(WarpGrid sourceGrid) {
/*  64 */     this.sourceGrid = sourceGrid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WarpGrid getSourceGrid() {
/*  74 */     return this.sourceGrid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestGrid(WarpGrid destGrid) {
/*  84 */     this.destGrid = destGrid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WarpGrid getDestGrid() {
/*  94 */     return this.destGrid;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFrames(int frames) {
/*  99 */     this.frames = frames;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFrames() {
/* 104 */     return this.frames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMorphImage(BufferedImage morphImage) {
/* 112 */     this.morphImage = morphImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage getMorphImage() {
/* 117 */     return this.morphImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTime(float time) {
/* 122 */     this.time = time;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTime() {
/* 127 */     return this.time;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle r) {
/* 132 */     r.width *= this.frames;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 137 */     int[] outPixels = new int[width * height];
/*     */     
/* 139 */     if (this.morphImage != null) {
/*     */       
/* 141 */       int[] morphPixels = getRGB(this.morphImage, 0, 0, width, height, null);
/* 142 */       morph(inPixels, morphPixels, outPixels, this.sourceGrid, this.destGrid, width, height, this.time);
/*     */     }
/* 144 */     else if (this.frames <= 1) {
/*     */       
/* 146 */       this.sourceGrid.warp(inPixels, width, height, this.sourceGrid, this.destGrid, outPixels);
/*     */     }
/*     */     else {
/*     */       
/* 150 */       WarpGrid newGrid = new WarpGrid(this.sourceGrid.rows, this.sourceGrid.cols, width, height);
/*     */       
/* 152 */       for (int i = 0; i < this.frames; i++) {
/*     */         
/* 154 */         float t = i / (this.frames - 1);
/* 155 */         this.sourceGrid.lerp(t, this.destGrid, newGrid);
/* 156 */         this.sourceGrid.warp(inPixels, width, height, this.sourceGrid, newGrid, outPixels);
/*     */       } 
/*     */     } 
/*     */     
/* 160 */     return outPixels;
/*     */   }
/*     */ 
/*     */   
/*     */   public void morph(int[] srcPixels, int[] destPixels, int[] outPixels, WarpGrid srcGrid, WarpGrid destGrid, int width, int height, float t) {
/* 165 */     WarpGrid newGrid = new WarpGrid(srcGrid.rows, srcGrid.cols, width, height);
/* 166 */     srcGrid.lerp(t, destGrid, newGrid);
/* 167 */     srcGrid.warp(srcPixels, width, height, srcGrid, newGrid, outPixels);
/* 168 */     int[] destPixels2 = new int[width * height];
/* 169 */     destGrid.warp(destPixels, width, height, destGrid, newGrid, destPixels2);
/* 170 */     crossDissolve(outPixels, destPixels2, width, height, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public void crossDissolve(int[] pixels1, int[] pixels2, int width, int height, float t) {
/* 175 */     int index = 0;
/*     */     
/* 177 */     for (int y = 0; y < height; y++) {
/*     */       
/* 179 */       for (int x = 0; x < width; x++) {
/*     */         
/* 181 */         pixels1[index] = ImageMath.mixColors(t, pixels1[index], pixels2[index]);
/* 182 */         index++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 189 */     return "Distort/Mesh Warp...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WarpFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */