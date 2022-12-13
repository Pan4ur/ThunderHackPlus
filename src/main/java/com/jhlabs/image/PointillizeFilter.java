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
/*     */ public class PointillizeFilter
/*     */   extends CellularFilter
/*     */ {
/*  25 */   private float edgeThickness = 0.4F;
/*     */   private boolean fadeEdges = false;
/*  27 */   private int edgeColor = -16777216;
/*  28 */   private float fuzziness = 0.1F;
/*     */ 
/*     */   
/*     */   public PointillizeFilter() {
/*  32 */     setScale(16.0F);
/*  33 */     setRandomness(0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEdgeThickness(float edgeThickness) {
/*  38 */     this.edgeThickness = edgeThickness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getEdgeThickness() {
/*  43 */     return this.edgeThickness;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFadeEdges(boolean fadeEdges) {
/*  48 */     this.fadeEdges = fadeEdges;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getFadeEdges() {
/*  53 */     return this.fadeEdges;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEdgeColor(int edgeColor) {
/*  58 */     this.edgeColor = edgeColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEdgeColor() {
/*  63 */     return this.edgeColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFuzziness(float fuzziness) {
/*  68 */     this.fuzziness = fuzziness;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFuzziness() {
/*  73 */     return this.fuzziness;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPixel(int x, int y, int[] inPixels, int width, int height) {
/*  78 */     float nx = this.m00 * x + this.m01 * y;
/*  79 */     float ny = this.m10 * x + this.m11 * y;
/*  80 */     nx /= this.scale;
/*  81 */     ny /= this.scale * this.stretch;
/*  82 */     nx += 1000.0F;
/*  83 */     ny += 1000.0F;
/*  84 */     float f = evaluate(nx, ny);
/*  85 */     float f1 = (this.results[0]).distance;
/*  86 */     int srcx = ImageMath.clamp((int)(((this.results[0]).x - 1000.0F) * this.scale), 0, width - 1);
/*  87 */     int srcy = ImageMath.clamp((int)(((this.results[0]).y - 1000.0F) * this.scale), 0, height - 1);
/*  88 */     int v = inPixels[srcy * width + srcx];
/*     */     
/*  90 */     if (this.fadeEdges) {
/*     */       
/*  92 */       float f2 = (this.results[1]).distance;
/*  93 */       srcx = ImageMath.clamp((int)(((this.results[1]).x - 1000.0F) * this.scale), 0, width - 1);
/*  94 */       srcy = ImageMath.clamp((int)(((this.results[1]).y - 1000.0F) * this.scale), 0, height - 1);
/*  95 */       int v2 = inPixels[srcy * width + srcx];
/*  96 */       v = ImageMath.mixColors(0.5F * f1 / f2, v, v2);
/*     */     }
/*     */     else {
/*     */       
/* 100 */       f = 1.0F - ImageMath.smoothStep(this.edgeThickness, this.edgeThickness + this.fuzziness, f1);
/* 101 */       v = ImageMath.mixColors(f, this.edgeColor, v);
/*     */     } 
/*     */     
/* 104 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return "Pixellate/Pointillize...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PointillizeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */