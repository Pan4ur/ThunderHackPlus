/*     */ package com.jhlabs.image;
/*     */ 
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
/*     */ public class GlintFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  29 */   private float threshold = 1.0F;
/*  30 */   private int length = 5;
/*  31 */   private float blur = 0.0F;
/*  32 */   private float amount = 0.1F;
/*     */   private boolean glintOnly = false;
/*  34 */   private Colormap colormap = new LinearColormap(-1, -16777216);
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
/*     */   public void setThreshold(float threshold) {
/*  47 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getThreshold() {
/*  57 */     return this.threshold;
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
/*     */   public void setAmount(float amount) {
/*  69 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  79 */     return this.amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLength(int length) {
/*  89 */     this.length = length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLength() {
/*  99 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlur(float blur) {
/* 109 */     this.blur = blur;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getBlur() {
/* 119 */     return this.blur;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGlintOnly(boolean glintOnly) {
/* 129 */     this.glintOnly = glintOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getGlintOnly() {
/* 139 */     return this.glintOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColormap(Colormap colormap) {
/* 149 */     this.colormap = colormap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Colormap getColormap() {
/* 159 */     return this.colormap;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 164 */     int dstPixels[], width = src.getWidth();
/* 165 */     int height = src.getHeight();
/* 166 */     int[] pixels = new int[width];
/* 167 */     int length2 = (int)(this.length / 1.414F);
/* 168 */     int[] colors = new int[this.length + 1];
/* 169 */     int[] colors2 = new int[length2 + 1];
/*     */     
/* 171 */     if (this.colormap != null) {
/*     */       int j;
/* 173 */       for (j = 0; j <= this.length; j++) {
/*     */         
/* 175 */         int argb = this.colormap.getColor(j / this.length);
/* 176 */         int r = argb >> 16 & 0xFF;
/* 177 */         int g = argb >> 8 & 0xFF;
/* 178 */         int b = argb & 0xFF;
/* 179 */         argb = argb & 0xFF000000 | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b);
/* 180 */         colors[j] = argb;
/*     */       } 
/*     */       
/* 183 */       for (j = 0; j <= length2; j++) {
/*     */         
/* 185 */         int argb = this.colormap.getColor(j / length2);
/* 186 */         int r = argb >> 16 & 0xFF;
/* 187 */         int g = argb >> 8 & 0xFF;
/* 188 */         int b = argb & 0xFF;
/* 189 */         argb = argb & 0xFF000000 | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b);
/* 190 */         colors2[j] = argb;
/*     */       } 
/*     */     } 
/*     */     
/* 194 */     BufferedImage mask = new BufferedImage(width, height, 2);
/* 195 */     int threshold3 = (int)(this.threshold * 3.0F * 255.0F);
/*     */     
/* 197 */     for (int y = 0; y < height; y++) {
/*     */       
/* 199 */       getRGB(src, 0, y, width, 1, pixels);
/*     */       
/* 201 */       for (int x = 0; x < width; x++) {
/*     */         
/* 203 */         int rgb = pixels[x];
/* 204 */         int a = rgb & 0xFF000000;
/* 205 */         int r = rgb >> 16 & 0xFF;
/* 206 */         int g = rgb >> 8 & 0xFF;
/* 207 */         int b = rgb & 0xFF;
/* 208 */         int l = r + g + b;
/*     */         
/* 210 */         if (l < threshold3) {
/*     */           
/* 212 */           pixels[x] = -16777216;
/*     */         }
/*     */         else {
/*     */           
/* 216 */           l /= 3;
/* 217 */           pixels[x] = a | l << 16 | l << 8 | l;
/*     */         } 
/*     */       } 
/*     */       
/* 221 */       setRGB(mask, 0, y, width, 1, pixels);
/*     */     } 
/*     */     
/* 224 */     if (this.blur != 0.0F)
/*     */     {
/* 226 */       mask = (new GaussianFilter(this.blur)).filter(mask, null);
/*     */     }
/*     */     
/* 229 */     if (dst == null)
/*     */     {
/* 231 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 236 */     if (this.glintOnly) {
/*     */       
/* 238 */       dstPixels = new int[width * height];
/*     */     }
/*     */     else {
/*     */       
/* 242 */       dstPixels = getRGB(src, 0, 0, width, height, null);
/*     */     } 
/*     */     
/* 245 */     for (int i = 0; i < height; i++) {
/*     */       
/* 247 */       int index = i * width;
/* 248 */       getRGB(mask, 0, i, width, 1, pixels);
/* 249 */       int ymin = Math.max(i - this.length, 0) - i;
/* 250 */       int ymax = Math.min(i + this.length, height - 1) - i;
/* 251 */       int ymin2 = Math.max(i - length2, 0) - i;
/* 252 */       int ymax2 = Math.min(i + length2, height - 1) - i;
/*     */       
/* 254 */       for (int x = 0; x < width; x++) {
/*     */         
/* 256 */         if ((pixels[x] & 0xFF) > this.threshold * 255.0F) {
/*     */           
/* 258 */           int xmin = Math.max(x - this.length, 0) - x;
/* 259 */           int xmax = Math.min(x + this.length, width - 1) - x;
/* 260 */           int xmin2 = Math.max(x - length2, 0) - x;
/* 261 */           int xmax2 = Math.min(x + length2, width - 1) - x;
/*     */           
/*     */           int m, k;
/* 264 */           for (m = 0, k = 0; m <= xmax; m++, k++)
/*     */           {
/* 266 */             dstPixels[index + m] = PixelUtils.combinePixels(dstPixels[index + m], colors[k], 4);
/*     */           }
/*     */           
/* 269 */           for (m = -1, k = 1; m >= xmin; m--, k++)
/*     */           {
/* 271 */             dstPixels[index + m] = PixelUtils.combinePixels(dstPixels[index + m], colors[k], 4);
/*     */           }
/*     */           
/*     */           int j, n;
/* 275 */           for (m = 1, j = index + width, n = 0; m <= ymax; m++, j += width, n++)
/*     */           {
/* 277 */             dstPixels[j] = PixelUtils.combinePixels(dstPixels[j], colors[n], 4);
/*     */           }
/*     */           
/* 280 */           for (m = -1, j = index - width, n = 0; m >= ymin; m--, j -= width, n++)
/*     */           {
/* 282 */             dstPixels[j] = PixelUtils.combinePixels(dstPixels[j], colors[n], 4);
/*     */           }
/*     */ 
/*     */           
/* 286 */           int xymin = Math.max(xmin2, ymin2);
/* 287 */           int xymax = Math.min(xmax2, ymax2);
/*     */           
/* 289 */           int count = Math.min(xmax2, ymax2);
/*     */           int i1, i2, i3;
/* 291 */           for (i1 = 1, i2 = index + width + 1, i3 = 0; i1 <= count; i1++, i2 += width + 1, i3++)
/*     */           {
/* 293 */             dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4);
/*     */           }
/*     */ 
/*     */           
/* 297 */           count = Math.min(-xmin2, -ymin2);
/*     */           
/* 299 */           for (i1 = 1, i2 = index - width - 1, i3 = 0; i1 <= count; i1++, i2 -= width + 1, i3++)
/*     */           {
/* 301 */             dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4);
/*     */           }
/*     */ 
/*     */           
/* 305 */           count = Math.min(xmax2, -ymin2);
/*     */           
/* 307 */           for (i1 = 1, i2 = index - width + 1, i3 = 0; i1 <= count; i1++, i2 += -width + 1, i3++)
/*     */           {
/* 309 */             dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4);
/*     */           }
/*     */ 
/*     */           
/* 313 */           count = Math.min(-xmin2, ymax2);
/*     */           
/* 315 */           for (i1 = 1, i2 = index + width - 1, i3 = 0; i1 <= count; i1++, i2 += width - 1, i3++)
/*     */           {
/* 317 */             dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4);
/*     */           }
/*     */         } 
/*     */         
/* 321 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 325 */     setRGB(dst, 0, 0, width, height, dstPixels);
/* 326 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 331 */     return "Effects/Glint...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GlintFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */