/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
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
/*     */ public class VariableBlurFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  29 */   private int hRadius = 1;
/*  30 */   private int vRadius = 1;
/*  31 */   private int iterations = 1;
/*     */ 
/*     */   
/*     */   private BufferedImage blurMask;
/*     */ 
/*     */   
/*     */   private boolean premultiplyAlpha = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPremultiplyAlpha(boolean premultiplyAlpha) {
/*  42 */     this.premultiplyAlpha = premultiplyAlpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getPremultiplyAlpha() {
/*  52 */     return this.premultiplyAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  57 */     int width = src.getWidth();
/*  58 */     int height = src.getHeight();
/*     */     
/*  60 */     if (dst == null)
/*     */     {
/*  62 */       dst = new BufferedImage(width, height, 2);
/*     */     }
/*     */     
/*  65 */     int[] inPixels = new int[width * height];
/*  66 */     int[] outPixels = new int[width * height];
/*  67 */     getRGB(src, 0, 0, width, height, inPixels);
/*     */     
/*  69 */     if (this.premultiplyAlpha)
/*     */     {
/*  71 */       ImageMath.premultiply(inPixels, 0, inPixels.length);
/*     */     }
/*     */     
/*  74 */     for (int i = 0; i < this.iterations; i++) {
/*     */       
/*  76 */       blur(inPixels, outPixels, width, height, this.hRadius, 1);
/*  77 */       blur(outPixels, inPixels, height, width, this.vRadius, 2);
/*     */     } 
/*     */     
/*  80 */     if (this.premultiplyAlpha)
/*     */     {
/*  82 */       ImageMath.unpremultiply(inPixels, 0, inPixels.length);
/*     */     }
/*     */     
/*  85 */     setRGB(dst, 0, 0, width, height, inPixels);
/*  86 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
/*  91 */     if (dstCM == null)
/*     */     {
/*  93 */       dstCM = src.getColorModel();
/*     */     }
/*     */     
/*  96 */     return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Rectangle2D getBounds2D(BufferedImage src) {
/* 101 */     return new Rectangle(0, 0, src.getWidth(), src.getHeight());
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
/* 106 */     if (dstPt == null)
/*     */     {
/* 108 */       dstPt = new Point2D.Double();
/*     */     }
/*     */     
/* 111 */     dstPt.setLocation(srcPt.getX(), srcPt.getY());
/* 112 */     return dstPt;
/*     */   }
/*     */ 
/*     */   
/*     */   public RenderingHints getRenderingHints() {
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void blur(int[] in, int[] out, int width, int height, int radius, int pass) {
/* 122 */     int widthMinus1 = width - 1;
/* 123 */     int[] r = new int[width];
/* 124 */     int[] g = new int[width];
/* 125 */     int[] b = new int[width];
/* 126 */     int[] a = new int[width];
/* 127 */     int[] mask = new int[width];
/* 128 */     int inIndex = 0;
/*     */     
/* 130 */     for (int y = 0; y < height; y++) {
/*     */       
/* 132 */       int outIndex = y;
/*     */       
/* 134 */       if (this.blurMask != null)
/*     */       {
/* 136 */         if (pass == 1) {
/*     */           
/* 138 */           getRGB(this.blurMask, 0, y, width, 1, mask);
/*     */         }
/*     */         else {
/*     */           
/* 142 */           getRGB(this.blurMask, y, 0, 1, width, mask);
/*     */         } 
/*     */       }
/*     */       int x;
/* 146 */       for (x = 0; x < width; x++) {
/*     */         
/* 148 */         int argb = in[inIndex + x];
/* 149 */         a[x] = argb >> 24 & 0xFF;
/* 150 */         r[x] = argb >> 16 & 0xFF;
/* 151 */         g[x] = argb >> 8 & 0xFF;
/* 152 */         b[x] = argb & 0xFF;
/*     */         
/* 154 */         if (x != 0) {
/*     */           
/* 156 */           a[x] = a[x] + a[x - 1];
/* 157 */           r[x] = r[x] + r[x - 1];
/* 158 */           g[x] = g[x] + g[x - 1];
/* 159 */           b[x] = b[x] + b[x - 1];
/*     */         } 
/*     */       } 
/*     */       
/* 163 */       for (x = 0; x < width; x++) {
/*     */         int ra;
/*     */ 
/*     */ 
/*     */         
/* 168 */         if (this.blurMask != null) {
/*     */           
/* 170 */           if (pass == 1)
/*     */           {
/* 172 */             ra = (int)(((mask[x] & 0xFF) * this.hRadius) / 255.0F);
/*     */           }
/*     */           else
/*     */           {
/* 176 */             ra = (int)(((mask[x] & 0xFF) * this.vRadius) / 255.0F);
/*     */           
/*     */           }
/*     */         
/*     */         }
/* 181 */         else if (pass == 1) {
/*     */           
/* 183 */           ra = (int)(blurRadiusAt(x, y, width, height) * this.hRadius);
/*     */         }
/*     */         else {
/*     */           
/* 187 */           ra = (int)(blurRadiusAt(y, x, height, width) * this.vRadius);
/*     */         } 
/*     */ 
/*     */         
/* 191 */         int divisor = 2 * ra + 1;
/* 192 */         int ta = 0, tr = 0, tg = 0, tb = 0;
/* 193 */         int i1 = x + ra;
/*     */         
/* 195 */         if (i1 > widthMinus1) {
/*     */           
/* 197 */           int f = i1 - widthMinus1;
/* 198 */           int l = widthMinus1;
/* 199 */           ta += (a[l] - a[l - 1]) * f;
/* 200 */           tr += (r[l] - r[l - 1]) * f;
/* 201 */           tg += (g[l] - g[l - 1]) * f;
/* 202 */           tb += (b[l] - b[l - 1]) * f;
/* 203 */           i1 = widthMinus1;
/*     */         } 
/*     */         
/* 206 */         int i2 = x - ra - 1;
/*     */         
/* 208 */         if (i2 < 0) {
/*     */           
/* 210 */           ta -= a[0] * i2;
/* 211 */           tr -= r[0] * i2;
/* 212 */           tg -= g[0] * i2;
/* 213 */           tb -= b[0] * i2;
/* 214 */           i2 = 0;
/*     */         } 
/*     */         
/* 217 */         ta += a[i1] - a[i2];
/* 218 */         tr += r[i1] - r[i2];
/* 219 */         tg += g[i1] - g[i2];
/* 220 */         tb += b[i1] - b[i2];
/* 221 */         out[outIndex] = ta / divisor << 24 | tr / divisor << 16 | tg / divisor << 8 | tb / divisor;
/* 222 */         outIndex += height;
/*     */       } 
/*     */       
/* 225 */       inIndex += width;
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
/*     */   protected float blurRadiusAt(int x, int y, int width, int height) {
/* 239 */     return x / width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHRadius(int hRadius) {
/* 250 */     this.hRadius = hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHRadius() {
/* 260 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVRadius(int vRadius) {
/* 271 */     this.vRadius = vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVRadius() {
/* 281 */     return this.vRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRadius(int radius) {
/* 292 */     this.hRadius = this.vRadius = radius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRadius() {
/* 302 */     return this.hRadius;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIterations(int iterations) {
/* 313 */     this.iterations = iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIterations() {
/* 323 */     return this.iterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlurMask(BufferedImage blurMask) {
/* 333 */     this.blurMask = blurMask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getBlurMask() {
/* 343 */     return this.blurMask;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 348 */     return "Blur/Variable Blur...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\VariableBlurFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */