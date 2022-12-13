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
/*     */ public class WeaveFilter
/*     */   extends PointFilter
/*     */ {
/*  24 */   private float xWidth = 16.0F;
/*  25 */   private float yWidth = 16.0F;
/*  26 */   private float xGap = 6.0F;
/*  27 */   private float yGap = 6.0F;
/*  28 */   private int rows = 4;
/*  29 */   private int cols = 4;
/*  30 */   private int rgbX = -32640;
/*  31 */   private int rgbY = -8355585;
/*     */   
/*     */   private boolean useImageColors = true;
/*     */   private boolean roundThreads = false;
/*     */   private boolean shadeCrossings = true;
/*  36 */   public int[][] matrix = new int[][] { { 0, 1, 0, 1 }, { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 1, 0, 1, 0 } };
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
/*     */   public void setXGap(float xGap) {
/*  50 */     this.xGap = xGap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setXWidth(float xWidth) {
/*  55 */     this.xWidth = xWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getXWidth() {
/*  60 */     return this.xWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setYWidth(float yWidth) {
/*  65 */     this.yWidth = yWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getYWidth() {
/*  70 */     return this.yWidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getXGap() {
/*  75 */     return this.xGap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setYGap(float yGap) {
/*  80 */     this.yGap = yGap;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getYGap() {
/*  85 */     return this.yGap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCrossings(int[][] matrix) {
/*  90 */     this.matrix = matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[][] getCrossings() {
/*  95 */     return this.matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUseImageColors(boolean useImageColors) {
/* 100 */     this.useImageColors = useImageColors;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getUseImageColors() {
/* 105 */     return this.useImageColors;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRoundThreads(boolean roundThreads) {
/* 110 */     this.roundThreads = roundThreads;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getRoundThreads() {
/* 115 */     return this.roundThreads;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShadeCrossings(boolean shadeCrossings) {
/* 120 */     this.shadeCrossings = shadeCrossings;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getShadeCrossings() {
/* 125 */     return this.shadeCrossings;
/*     */   }
/*     */   public int filterRGB(int x, int y, int rgb) {
/*     */     float dX, dY, cX, cY;
/*     */     int lrgbX, lrgbY, v;
/* 130 */     x = (int)(x + this.xWidth + this.xGap / 2.0F);
/* 131 */     y = (int)(y + this.yWidth + this.yGap / 2.0F);
/* 132 */     float nx = ImageMath.mod(x, this.xWidth + this.xGap);
/* 133 */     float ny = ImageMath.mod(y, this.yWidth + this.yGap);
/* 134 */     int ix = (int)(x / (this.xWidth + this.xGap));
/* 135 */     int iy = (int)(y / (this.yWidth + this.yGap));
/* 136 */     boolean inX = (nx < this.xWidth);
/* 137 */     boolean inY = (ny < this.yWidth);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     if (this.roundThreads) {
/*     */       
/* 144 */       dX = Math.abs(this.xWidth / 2.0F - nx) / this.xWidth / 2.0F;
/* 145 */       dY = Math.abs(this.yWidth / 2.0F - ny) / this.yWidth / 2.0F;
/*     */     }
/*     */     else {
/*     */       
/* 149 */       dX = dY = 0.0F;
/*     */     } 
/*     */     
/* 152 */     if (this.shadeCrossings) {
/*     */       
/* 154 */       cX = ImageMath.smoothStep(this.xWidth / 2.0F, this.xWidth / 2.0F + this.xGap, Math.abs(this.xWidth / 2.0F - nx));
/* 155 */       cY = ImageMath.smoothStep(this.yWidth / 2.0F, this.yWidth / 2.0F + this.yGap, Math.abs(this.yWidth / 2.0F - ny));
/*     */     }
/*     */     else {
/*     */       
/* 159 */       cX = cY = 0.0F;
/*     */     } 
/*     */     
/* 162 */     if (this.useImageColors) {
/*     */       
/* 164 */       lrgbX = lrgbY = rgb;
/*     */     }
/*     */     else {
/*     */       
/* 168 */       lrgbX = this.rgbX;
/* 169 */       lrgbY = this.rgbY;
/*     */     } 
/*     */ 
/*     */     
/* 173 */     int ixc = ix % this.cols;
/* 174 */     int iyr = iy % this.rows;
/* 175 */     int m = this.matrix[iyr][ixc];
/*     */     
/* 177 */     if (inX) {
/*     */       
/* 179 */       if (inY)
/*     */       {
/* 181 */         v = (m == 1) ? lrgbX : lrgbY;
/* 182 */         v = ImageMath.mixColors(2.0F * ((m == 1) ? dX : dY), v, -16777216);
/*     */       }
/*     */       else
/*     */       {
/* 186 */         if (this.shadeCrossings)
/*     */         {
/* 188 */           if (m != this.matrix[(iy + 1) % this.rows][ixc]) {
/*     */             
/* 190 */             if (m == 0)
/*     */             {
/* 192 */               cY = 1.0F - cY;
/*     */             }
/*     */             
/* 195 */             cY *= 0.5F;
/* 196 */             lrgbX = ImageMath.mixColors(cY, lrgbX, -16777216);
/*     */           }
/* 198 */           else if (m == 0) {
/*     */             
/* 200 */             lrgbX = ImageMath.mixColors(0.5F, lrgbX, -16777216);
/*     */           } 
/*     */         }
/*     */         
/* 204 */         v = ImageMath.mixColors(2.0F * dX, lrgbX, -16777216);
/*     */       }
/*     */     
/* 207 */     } else if (inY) {
/*     */       
/* 209 */       if (this.shadeCrossings)
/*     */       {
/* 211 */         if (m != this.matrix[iyr][(ix + 1) % this.cols]) {
/*     */           
/* 213 */           if (m == 1)
/*     */           {
/* 215 */             cX = 1.0F - cX;
/*     */           }
/*     */           
/* 218 */           cX *= 0.5F;
/* 219 */           lrgbY = ImageMath.mixColors(cX, lrgbY, -16777216);
/*     */         }
/* 221 */         else if (m == 1) {
/*     */           
/* 223 */           lrgbY = ImageMath.mixColors(0.5F, lrgbY, -16777216);
/*     */         } 
/*     */       }
/*     */       
/* 227 */       v = ImageMath.mixColors(2.0F * dY, lrgbY, -16777216);
/*     */     }
/*     */     else {
/*     */       
/* 231 */       v = 0;
/*     */     } 
/*     */     
/* 234 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 239 */     return "Texture/Weave...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WeaveFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */