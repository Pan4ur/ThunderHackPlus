/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.WritableRaster;
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
/*     */ public abstract class TransformFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   public static final int ZERO = 0;
/*     */   public static final int CLAMP = 1;
/*     */   public static final int WRAP = 2;
/*     */   public static final int RGB_CLAMP = 3;
/*     */   public static final int NEAREST_NEIGHBOUR = 0;
/*     */   public static final int BILINEAR = 1;
/*  61 */   protected int edgeAction = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   protected int interpolation = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Rectangle transformedSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Rectangle originalSpace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEdgeAction(int edgeAction) {
/*  85 */     this.edgeAction = edgeAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEdgeAction() {
/*  95 */     return this.edgeAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterpolation(int interpolation) {
/* 105 */     this.interpolation = interpolation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getInterpolation() {
/* 115 */     return this.interpolation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void transformInverse(int paramInt1, int paramInt2, float[] paramArrayOffloat);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle rect) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 136 */     int width = src.getWidth();
/* 137 */     int height = src.getHeight();
/* 138 */     int type = src.getType();
/* 139 */     WritableRaster srcRaster = src.getRaster();
/* 140 */     this.originalSpace = new Rectangle(0, 0, width, height);
/* 141 */     this.transformedSpace = new Rectangle(0, 0, width, height);
/* 142 */     transformSpace(this.transformedSpace);
/*     */     
/* 144 */     if (dst == null) {
/*     */       
/* 146 */       ColorModel dstCM = src.getColorModel();
/* 147 */       dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.transformedSpace.width, this.transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
/*     */     } 
/*     */     
/* 150 */     WritableRaster dstRaster = dst.getRaster();
/* 151 */     int[] inPixels = getRGB(src, 0, 0, width, height, null);
/*     */     
/* 153 */     if (this.interpolation == 0)
/*     */     {
/* 155 */       return filterPixelsNN(dst, width, height, inPixels, this.transformedSpace);
/*     */     }
/*     */     
/* 158 */     int srcWidth = width;
/* 159 */     int srcHeight = height;
/* 160 */     int srcWidth1 = width - 1;
/* 161 */     int srcHeight1 = height - 1;
/* 162 */     int outWidth = this.transformedSpace.width;
/* 163 */     int outHeight = this.transformedSpace.height;
/*     */     
/* 165 */     int index = 0;
/* 166 */     int[] outPixels = new int[outWidth];
/* 167 */     int outX = this.transformedSpace.x;
/* 168 */     int outY = this.transformedSpace.y;
/* 169 */     float[] out = new float[2];
/*     */     
/* 171 */     for (int y = 0; y < outHeight; y++) {
/*     */       
/* 173 */       for (int x = 0; x < outWidth; x++) {
/*     */         int nw, ne, sw, se;
/* 175 */         transformInverse(outX + x, outY + y, out);
/* 176 */         int srcX = (int)Math.floor(out[0]);
/* 177 */         int srcY = (int)Math.floor(out[1]);
/* 178 */         float xWeight = out[0] - srcX;
/* 179 */         float yWeight = out[1] - srcY;
/*     */ 
/*     */         
/* 182 */         if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
/*     */ 
/*     */           
/* 185 */           int i = srcWidth * srcY + srcX;
/* 186 */           nw = inPixels[i];
/* 187 */           ne = inPixels[i + 1];
/* 188 */           sw = inPixels[i + srcWidth];
/* 189 */           se = inPixels[i + srcWidth + 1];
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 194 */           nw = getPixel(inPixels, srcX, srcY, srcWidth, srcHeight);
/* 195 */           ne = getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
/* 196 */           sw = getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
/* 197 */           se = getPixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
/*     */         } 
/*     */         
/* 200 */         outPixels[x] = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
/*     */       } 
/*     */       
/* 203 */       setRGB(dst, 0, y, this.transformedSpace.width, 1, outPixels);
/*     */     } 
/*     */     
/* 206 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   private final int getPixel(int[] pixels, int x, int y, int width, int height) {
/* 211 */     if (x < 0 || x >= width || y < 0 || y >= height) {
/*     */       
/* 213 */       switch (this.edgeAction) {
/*     */ 
/*     */         
/*     */         default:
/* 217 */           return 0;
/*     */         
/*     */         case 2:
/* 220 */           return pixels[ImageMath.mod(y, height) * width + ImageMath.mod(x, width)];
/*     */         
/*     */         case 1:
/* 223 */           return pixels[ImageMath.clamp(y, 0, height - 1) * width + ImageMath.clamp(x, 0, width - 1)];
/*     */         case 3:
/*     */           break;
/* 226 */       }  return pixels[ImageMath.clamp(y, 0, height - 1) * width + ImageMath.clamp(x, 0, width - 1)] & 0xFFFFFF;
/*     */     } 
/*     */ 
/*     */     
/* 230 */     return pixels[y * width + x];
/*     */   }
/*     */ 
/*     */   
/*     */   protected BufferedImage filterPixelsNN(BufferedImage dst, int width, int height, int[] inPixels, Rectangle transformedSpace) {
/* 235 */     int srcWidth = width;
/* 236 */     int srcHeight = height;
/* 237 */     int outWidth = transformedSpace.width;
/* 238 */     int outHeight = transformedSpace.height;
/*     */     
/* 240 */     int[] outPixels = new int[outWidth];
/* 241 */     int outX = transformedSpace.x;
/* 242 */     int outY = transformedSpace.y;
/* 243 */     int[] rgb = new int[4];
/* 244 */     float[] out = new float[2];
/*     */     
/* 246 */     for (int y = 0; y < outHeight; y++) {
/*     */       
/* 248 */       for (int x = 0; x < outWidth; x++) {
/*     */         
/* 250 */         transformInverse(outX + x, outY + y, out);
/* 251 */         int srcX = (int)out[0];
/* 252 */         int srcY = (int)out[1];
/*     */ 
/*     */         
/* 255 */         if (out[0] < 0.0F || srcX >= srcWidth || out[1] < 0.0F || srcY >= srcHeight) {
/*     */           int p;
/*     */ 
/*     */           
/* 259 */           switch (this.edgeAction) {
/*     */ 
/*     */             
/*     */             default:
/* 263 */               p = 0;
/*     */               break;
/*     */             
/*     */             case 2:
/* 267 */               p = inPixels[ImageMath.mod(srcY, srcHeight) * srcWidth + ImageMath.mod(srcX, srcWidth)];
/*     */               break;
/*     */             
/*     */             case 1:
/* 271 */               p = inPixels[ImageMath.clamp(srcY, 0, srcHeight - 1) * srcWidth + ImageMath.clamp(srcX, 0, srcWidth - 1)];
/*     */               break;
/*     */             
/*     */             case 3:
/* 275 */               p = inPixels[ImageMath.clamp(srcY, 0, srcHeight - 1) * srcWidth + ImageMath.clamp(srcX, 0, srcWidth - 1)] & 0xFFFFFF;
/*     */               break;
/*     */           } 
/* 278 */           outPixels[x] = p;
/*     */         }
/*     */         else {
/*     */           
/* 282 */           int i = srcWidth * srcY + srcX;
/* 283 */           rgb[0] = inPixels[i];
/* 284 */           outPixels[x] = inPixels[i];
/*     */         } 
/*     */       } 
/*     */       
/* 288 */       setRGB(dst, 0, y, transformedSpace.width, 1, outPixels);
/*     */     } 
/*     */     
/* 291 */     return dst;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TransformFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */