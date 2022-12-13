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
/*     */ public class WarpGrid
/*     */ {
/*  28 */   public float[] xGrid = null; public int rows; public int cols; private static final float m00 = -0.5F;
/*  29 */   public float[] yGrid = null; private static final float m01 = 1.5F;
/*     */   private static final float m02 = -1.5F;
/*     */   private static final float m03 = 0.5F;
/*     */   
/*     */   public WarpGrid(int rows, int cols, int w, int h) {
/*  34 */     this.rows = rows;
/*  35 */     this.cols = cols;
/*  36 */     this.xGrid = new float[rows * cols];
/*  37 */     this.yGrid = new float[rows * cols];
/*  38 */     int index = 0;
/*     */     
/*  40 */     for (int row = 0; row < rows; row++) {
/*     */       
/*  42 */       for (int col = 0; col < cols; col++) {
/*     */         
/*  44 */         this.xGrid[index] = col * (w - 1) / (cols - 1);
/*  45 */         this.yGrid[index] = row * (h - 1) / (rows - 1);
/*  46 */         index++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private static final float m10 = 1.0F; private static final float m11 = -2.5F; private static final float m12 = 2.0F; private static final float m13 = -0.5F;
/*     */   private static final float m20 = -0.5F;
/*     */   private static final float m22 = 0.5F;
/*     */   private static final float m31 = 1.0F;
/*     */   
/*     */   public void addRow(int before) {
/*  56 */     int size = (this.rows + 1) * this.cols;
/*  57 */     float[] x = new float[size];
/*  58 */     float[] y = new float[size];
/*  59 */     this.rows++;
/*  60 */     int i = 0;
/*  61 */     int j = 0;
/*     */     
/*  63 */     for (int row = 0; row < this.rows; row++) {
/*     */       
/*  65 */       for (int col = 0; col < this.cols; col++) {
/*     */         
/*  67 */         int k = j + col;
/*  68 */         int l = i + col;
/*     */         
/*  70 */         if (row == before) {
/*     */           
/*  72 */           x[k] = (this.xGrid[l] + this.xGrid[k]) / 2.0F;
/*  73 */           y[k] = (this.yGrid[l] + this.yGrid[k]) / 2.0F;
/*     */         }
/*     */         else {
/*     */           
/*  77 */           x[k] = this.xGrid[l];
/*  78 */           y[k] = this.yGrid[l];
/*     */         } 
/*     */       } 
/*     */       
/*  82 */       if (row != before - 1)
/*     */       {
/*  84 */         i += this.cols;
/*     */       }
/*     */       
/*  87 */       j += this.cols;
/*     */     } 
/*     */     
/*  90 */     this.xGrid = x;
/*  91 */     this.yGrid = y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCol(int before) {
/*  99 */     int size = this.rows * (this.cols + 1);
/* 100 */     float[] x = new float[size];
/* 101 */     float[] y = new float[size];
/* 102 */     this.cols++;
/* 103 */     int i = 0;
/* 104 */     int j = 0;
/*     */     
/* 106 */     for (int row = 0; row < this.rows; row++) {
/*     */ 
/*     */ 
/*     */       
/* 110 */       for (int col = 0; col < this.cols; col++) {
/*     */         
/* 112 */         if (col == before) {
/*     */           
/* 114 */           x[j] = (this.xGrid[i] + this.xGrid[i - 1]) / 2.0F;
/* 115 */           y[j] = (this.yGrid[i] + this.yGrid[i - 1]) / 2.0F;
/*     */         }
/*     */         else {
/*     */           
/* 119 */           x[j] = this.xGrid[i];
/* 120 */           y[j] = this.yGrid[i];
/* 121 */           i++;
/*     */         } 
/*     */         
/* 124 */         j++;
/*     */       } 
/*     */     } 
/*     */     
/* 128 */     this.xGrid = x;
/* 129 */     this.yGrid = y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeRow(int r) {
/* 137 */     int size = (this.rows - 1) * this.cols;
/* 138 */     float[] x = new float[size];
/* 139 */     float[] y = new float[size];
/* 140 */     this.rows--;
/* 141 */     int i = 0;
/* 142 */     int j = 0;
/*     */     
/* 144 */     for (int row = 0; row < this.rows; row++) {
/*     */       
/* 146 */       for (int col = 0; col < this.cols; col++) {
/*     */         
/* 148 */         int k = j + col;
/* 149 */         int l = i + col;
/* 150 */         x[k] = this.xGrid[l];
/* 151 */         y[k] = this.yGrid[l];
/*     */       } 
/*     */       
/* 154 */       if (row == r - 1)
/*     */       {
/* 156 */         i += this.cols;
/*     */       }
/*     */       
/* 159 */       i += this.cols;
/* 160 */       j += this.cols;
/*     */     } 
/*     */     
/* 163 */     this.xGrid = x;
/* 164 */     this.yGrid = y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeCol(int r) {
/* 172 */     int size = this.rows * (this.cols + 1);
/* 173 */     float[] x = new float[size];
/* 174 */     float[] y = new float[size];
/* 175 */     this.cols--;
/*     */     
/* 177 */     for (int row = 0; row < this.rows; row++) {
/*     */       
/* 179 */       int i = row * (this.cols + 1);
/* 180 */       int j = row * this.cols;
/*     */       
/* 182 */       for (int col = 0; col < this.cols; col++) {
/*     */         
/* 184 */         x[j] = this.xGrid[i];
/* 185 */         y[j] = this.yGrid[i];
/*     */         
/* 187 */         if (col == r - 1)
/*     */         {
/* 189 */           i++;
/*     */         }
/*     */         
/* 192 */         i++;
/* 193 */         j++;
/*     */       } 
/*     */     } 
/*     */     
/* 197 */     this.xGrid = x;
/* 198 */     this.yGrid = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public void lerp(float t, WarpGrid destination, WarpGrid intermediate) {
/* 203 */     if (this.rows != destination.rows || this.cols != destination.cols)
/*     */     {
/* 205 */       throw new IllegalArgumentException("source and destination are different sizes");
/*     */     }
/*     */     
/* 208 */     if (this.rows != intermediate.rows || this.cols != intermediate.cols)
/*     */     {
/* 210 */       throw new IllegalArgumentException("source and intermediate are different sizes");
/*     */     }
/*     */     
/* 213 */     int index = 0;
/*     */     
/* 215 */     for (int row = 0; row < this.rows; row++) {
/*     */       
/* 217 */       for (int col = 0; col < this.cols; col++) {
/*     */         
/* 219 */         intermediate.xGrid[index] = ImageMath.lerp(t, this.xGrid[index], destination.xGrid[index]);
/* 220 */         intermediate.yGrid[index] = ImageMath.lerp(t, this.yGrid[index], destination.yGrid[index]);
/* 221 */         index++;
/*     */       } 
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
/*     */   public void warp(int[] inPixels, int cols, int rows, WarpGrid sourceGrid, WarpGrid destGrid, int[] outPixels) {
/*     */     try {
/* 235 */       if (sourceGrid.rows != destGrid.rows || sourceGrid.cols != destGrid.cols)
/*     */       {
/* 237 */         throw new IllegalArgumentException("source and destination grids are different sizes");
/*     */       }
/*     */       
/* 240 */       int size = Math.max(cols, rows);
/* 241 */       float[] xrow = new float[size];
/* 242 */       float[] yrow = new float[size];
/* 243 */       float[] scale = new float[size + 1];
/* 244 */       float[] interpolated = new float[size + 1];
/* 245 */       int gridCols = sourceGrid.cols;
/* 246 */       int gridRows = sourceGrid.rows;
/* 247 */       WarpGrid splines = new WarpGrid(rows, gridCols, 1, 1);
/*     */       int u;
/* 249 */       for (u = 0; u < gridCols; u++) {
/*     */         
/* 251 */         int i = u;
/*     */         
/* 253 */         for (int k = 0; k < gridRows; k++) {
/*     */           
/* 255 */           xrow[k] = sourceGrid.xGrid[i];
/* 256 */           yrow[k] = sourceGrid.yGrid[i];
/* 257 */           i += gridCols;
/*     */         } 
/*     */         
/* 260 */         interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
/* 261 */         i = u;
/*     */         
/* 263 */         for (int j = 0; j < rows; j++) {
/*     */           
/* 265 */           splines.xGrid[i] = interpolated[j];
/* 266 */           i += gridCols;
/*     */         } 
/*     */       } 
/*     */       
/* 270 */       for (u = 0; u < gridCols; u++) {
/*     */         
/* 272 */         int i = u;
/*     */         
/* 274 */         for (int k = 0; k < gridRows; k++) {
/*     */           
/* 276 */           xrow[k] = destGrid.xGrid[i];
/* 277 */           yrow[k] = destGrid.yGrid[i];
/* 278 */           i += gridCols;
/*     */         } 
/*     */         
/* 281 */         interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
/* 282 */         i = u;
/*     */         
/* 284 */         for (int j = 0; j < rows; j++) {
/*     */           
/* 286 */           splines.yGrid[i] = interpolated[j];
/* 287 */           i += gridCols;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 292 */       int[] intermediate = new int[rows * cols];
/* 293 */       int offset = 0;
/*     */       
/* 295 */       for (int y = 0; y < rows; y++) {
/*     */ 
/*     */         
/* 298 */         interpolateSpline(splines.xGrid, splines.yGrid, offset, gridCols, scale, 0, cols);
/* 299 */         scale[cols] = cols;
/* 300 */         ImageMath.resample(inPixels, intermediate, cols, y * cols, 1, scale);
/* 301 */         offset += gridCols;
/*     */       } 
/*     */ 
/*     */       
/* 305 */       splines = new WarpGrid(gridRows, cols, 1, 1);
/* 306 */       offset = 0;
/* 307 */       int offset2 = 0;
/*     */       int v;
/* 309 */       for (v = 0; v < gridRows; v++) {
/*     */         
/* 311 */         interpolateSpline(sourceGrid.xGrid, sourceGrid.yGrid, offset, gridCols, splines.xGrid, offset2, cols);
/* 312 */         offset += gridCols;
/* 313 */         offset2 += cols;
/*     */       } 
/*     */       
/* 316 */       offset = 0;
/* 317 */       offset2 = 0;
/*     */       
/* 319 */       for (v = 0; v < gridRows; v++) {
/*     */         
/* 321 */         interpolateSpline(destGrid.xGrid, destGrid.yGrid, offset, gridCols, splines.yGrid, offset2, cols);
/* 322 */         offset += gridCols;
/* 323 */         offset2 += cols;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 328 */       for (int x = 0; x < cols; x++)
/*     */       {
/* 330 */         int i = x;
/*     */         
/* 332 */         for (v = 0; v < gridRows; v++) {
/*     */           
/* 334 */           xrow[v] = splines.xGrid[i];
/* 335 */           yrow[v] = splines.yGrid[i];
/* 336 */           i += cols;
/*     */         } 
/*     */         
/* 339 */         interpolateSpline(xrow, yrow, 0, gridRows, scale, 0, rows);
/* 340 */         scale[rows] = rows;
/* 341 */         ImageMath.resample(intermediate, outPixels, rows, x, cols, scale);
/*     */       }
/*     */     
/* 344 */     } catch (Exception e) {
/*     */       
/* 346 */       e.printStackTrace();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void interpolateSpline(float[] xKnots, float[] yKnots, int offset, int length, float[] splineY, int splineOffset, int splineLength) {
/* 364 */     int index = offset;
/* 365 */     int end = offset + length - 1;
/*     */ 
/*     */ 
/*     */     
/* 369 */     float x0 = xKnots[index];
/* 370 */     float k2 = yKnots[index], k1 = k2, k0 = k1;
/* 371 */     float x1 = xKnots[index + 1];
/* 372 */     float k3 = yKnots[index + 1];
/*     */     
/* 374 */     for (int i = 0; i < splineLength; i++) {
/*     */       
/* 376 */       if (index <= end && i > xKnots[index]) {
/*     */         
/* 378 */         k0 = k1;
/* 379 */         k1 = k2;
/* 380 */         k2 = k3;
/* 381 */         x0 = xKnots[index];
/* 382 */         index++;
/*     */         
/* 384 */         if (index <= end)
/*     */         {
/* 386 */           x1 = xKnots[index];
/*     */         }
/*     */         
/* 389 */         if (index < end) {
/*     */           
/* 391 */           k3 = yKnots[index + 1];
/*     */         }
/*     */         else {
/*     */           
/* 395 */           k3 = k2;
/*     */         } 
/*     */       } 
/*     */       
/* 399 */       float t = (i - x0) / (x1 - x0);
/* 400 */       float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
/* 401 */       float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
/* 402 */       float c1 = -0.5F * k0 + 0.5F * k2;
/* 403 */       float c0 = 1.0F * k1;
/* 404 */       splineY[splineOffset + i] = ((c3 * t + c2) * t + c1) * t + c0;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void interpolateSpline2(float[] xKnots, float[] yKnots, int offset, float[] splineY, int splineOffset, int splineLength) {
/* 410 */     int index = offset;
/*     */ 
/*     */     
/* 413 */     float leftX = xKnots[index];
/* 414 */     float leftY = yKnots[index];
/* 415 */     float rightX = xKnots[index + 1];
/* 416 */     float rightY = yKnots[index + 1];
/*     */     
/* 418 */     for (int i = 0; i < splineLength; i++) {
/*     */       
/* 420 */       if (i > xKnots[index]) {
/*     */         
/* 422 */         leftX = xKnots[index];
/* 423 */         leftY = yKnots[index];
/* 424 */         index++;
/* 425 */         rightX = xKnots[index];
/* 426 */         rightY = yKnots[index];
/*     */       } 
/*     */       
/* 429 */       float f = (i - leftX) / (rightX - leftX);
/* 430 */       splineY[splineOffset + i] = leftY + f * (rightY - leftY);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WarpGrid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */