/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.awt.image.PixelGrabber;
/*     */ import java.awt.image.Raster;
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
/*     */ public abstract class ImageUtils
/*     */ {
/*  30 */   private static BufferedImage backgroundImage = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage createImage(ImageProducer producer) {
/*  39 */     PixelGrabber pg = new PixelGrabber(producer, 0, 0, -1, -1, null, 0, 0);
/*     */ 
/*     */     
/*     */     try {
/*  43 */       pg.grabPixels();
/*     */     }
/*  45 */     catch (InterruptedException e) {
/*     */       
/*  47 */       throw new RuntimeException("Image fetch interrupted");
/*     */     } 
/*     */     
/*  50 */     if ((pg.status() & 0x80) != 0)
/*     */     {
/*  52 */       throw new RuntimeException("Image fetch aborted");
/*     */     }
/*     */     
/*  55 */     if ((pg.status() & 0x40) != 0)
/*     */     {
/*  57 */       throw new RuntimeException("Image fetch error");
/*     */     }
/*     */     
/*  60 */     BufferedImage p = new BufferedImage(pg.getWidth(), pg.getHeight(), 2);
/*  61 */     p.setRGB(0, 0, pg.getWidth(), pg.getHeight(), (int[])pg.getPixels(), 0, pg.getWidth());
/*  62 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage convertImageToARGB(Image image) {
/*  72 */     if (image instanceof BufferedImage && ((BufferedImage)image).getType() == 2)
/*     */     {
/*  74 */       return (BufferedImage)image;
/*     */     }
/*     */     
/*  77 */     BufferedImage p = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
/*  78 */     Graphics2D g = p.createGraphics();
/*  79 */     g.drawImage(image, 0, 0, (ImageObserver)null);
/*  80 */     g.dispose();
/*  81 */     return p;
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
/*     */   public static BufferedImage getSubimage(BufferedImage image, int x, int y, int w, int h) {
/*  95 */     BufferedImage newImage = new BufferedImage(w, h, 2);
/*  96 */     Graphics2D g = newImage.createGraphics();
/*  97 */     g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
/*  98 */     g.dispose();
/*  99 */     return newImage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage cloneImage(BufferedImage image) {
/* 109 */     BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
/* 110 */     Graphics2D g = newImage.createGraphics();
/* 111 */     g.drawRenderedImage(image, (AffineTransform)null);
/* 112 */     g.dispose();
/* 113 */     return newImage;
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
/*     */   public static void paintCheckedBackground(Component c, Graphics g, int x, int y, int width, int height) {
/* 127 */     if (backgroundImage == null) {
/*     */       
/* 129 */       backgroundImage = new BufferedImage(64, 64, 2);
/* 130 */       Graphics bg = backgroundImage.createGraphics();
/*     */       
/* 132 */       for (int by = 0; by < 64; by += 8) {
/*     */         
/* 134 */         for (int bx = 0; bx < 64; bx += 8) {
/*     */           
/* 136 */           bg.setColor((((bx ^ by) & 0x8) != 0) ? Color.lightGray : Color.white);
/* 137 */           bg.fillRect(bx, by, 8, 8);
/*     */         } 
/*     */       } 
/*     */       
/* 141 */       bg.dispose();
/*     */     } 
/*     */     
/* 144 */     if (backgroundImage != null) {
/*     */       
/* 146 */       Shape saveClip = g.getClip();
/* 147 */       Rectangle r = g.getClipBounds();
/*     */       
/* 149 */       if (r == null)
/*     */       {
/* 151 */         r = new Rectangle(c.getSize());
/*     */       }
/*     */       
/* 154 */       r = r.intersection(new Rectangle(x, y, width, height));
/* 155 */       g.setClip(r);
/* 156 */       int w = backgroundImage.getWidth();
/* 157 */       int h = backgroundImage.getHeight();
/*     */       
/* 159 */       if (w != -1 && h != -1) {
/*     */         
/* 161 */         int x1 = r.x / w * w;
/* 162 */         int y1 = r.y / h * h;
/* 163 */         int x2 = (r.x + r.width + w - 1) / w * w;
/* 164 */         int y2 = (r.y + r.height + h - 1) / h * h;
/*     */         
/* 166 */         for (y = y1; y < y2; y += h) {
/* 167 */           for (x = x1; x < x2; x += w)
/*     */           {
/* 169 */             g.drawImage(backgroundImage, x, y, c);
/*     */           }
/*     */         } 
/*     */       } 
/* 173 */       g.setClip(saveClip);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Rectangle getSelectedBounds(BufferedImage p) {
/* 184 */     int width = p.getWidth();
/* 185 */     int height = p.getHeight();
/* 186 */     int maxX = 0, maxY = 0, minX = width, minY = height;
/* 187 */     boolean anySelected = false;
/*     */     
/* 189 */     int[] pixels = null;
/*     */     int y1;
/* 191 */     for (y1 = height - 1; y1 >= 0; y1--) {
/*     */       
/* 193 */       pixels = getRGB(p, 0, y1, width, 1, pixels);
/*     */       int x;
/* 195 */       for (x = 0; x < minX; x++) {
/*     */         
/* 197 */         if ((pixels[x] & 0xFF000000) != 0) {
/*     */           
/* 199 */           minX = x;
/* 200 */           maxY = y1;
/* 201 */           anySelected = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 206 */       for (x = width - 1; x >= maxX; x--) {
/*     */         
/* 208 */         if ((pixels[x] & 0xFF000000) != 0) {
/*     */           
/* 210 */           maxX = x;
/* 211 */           maxY = y1;
/* 212 */           anySelected = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 217 */       if (anySelected) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 223 */     pixels = null;
/*     */     
/* 225 */     for (int y = 0; y < y1; y++) {
/*     */       
/* 227 */       pixels = getRGB(p, 0, y, width, 1, pixels);
/*     */       int x;
/* 229 */       for (x = 0; x < minX; x++) {
/*     */         
/* 231 */         if ((pixels[x] & 0xFF000000) != 0) {
/*     */           
/* 233 */           minX = x;
/*     */           
/* 235 */           if (y < minY)
/*     */           {
/* 237 */             minY = y;
/*     */           }
/*     */           
/* 240 */           anySelected = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 245 */       for (x = width - 1; x >= maxX; x--) {
/*     */         
/* 247 */         if ((pixels[x] & 0xFF000000) != 0) {
/*     */           
/* 249 */           maxX = x;
/*     */           
/* 251 */           if (y < minY)
/*     */           {
/* 253 */             minY = y;
/*     */           }
/*     */           
/* 256 */           anySelected = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 262 */     if (anySelected)
/*     */     {
/* 264 */       return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
/*     */     }
/*     */     
/* 267 */     return null;
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
/*     */   public static void composeThroughMask(Raster src, WritableRaster dst, Raster sel) {
/* 279 */     int x = src.getMinX();
/* 280 */     int y = src.getMinY();
/* 281 */     int w = src.getWidth();
/* 282 */     int h = src.getHeight();
/* 283 */     int[] srcRGB = null;
/* 284 */     int[] selRGB = null;
/* 285 */     int[] dstRGB = null;
/*     */     
/* 287 */     for (int i = 0; i < h; i++) {
/*     */       
/* 289 */       srcRGB = src.getPixels(x, y, w, 1, srcRGB);
/* 290 */       selRGB = sel.getPixels(x, y, w, 1, selRGB);
/* 291 */       dstRGB = dst.getPixels(x, y, w, 1, dstRGB);
/* 292 */       int k = x;
/*     */       
/* 294 */       for (int j = 0; j < w; j++) {
/*     */         
/* 296 */         int sr = srcRGB[k];
/* 297 */         int dir = dstRGB[k];
/* 298 */         int sg = srcRGB[k + 1];
/* 299 */         int dig = dstRGB[k + 1];
/* 300 */         int sb = srcRGB[k + 2];
/* 301 */         int dib = dstRGB[k + 2];
/* 302 */         int sa = srcRGB[k + 3];
/* 303 */         int dia = dstRGB[k + 3];
/* 304 */         float a = selRGB[k + 3] / 255.0F;
/* 305 */         float ac = 1.0F - a;
/* 306 */         dstRGB[k] = (int)(a * sr + ac * dir);
/* 307 */         dstRGB[k + 1] = (int)(a * sg + ac * dig);
/* 308 */         dstRGB[k + 2] = (int)(a * sb + ac * dib);
/* 309 */         dstRGB[k + 3] = (int)(a * sa + ac * dia);
/* 310 */         k += 4;
/*     */       } 
/*     */       
/* 313 */       dst.setPixels(x, y, w, 1, dstRGB);
/* 314 */       y++;
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
/*     */   public static int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
/* 332 */     int type = image.getType();
/*     */     
/* 334 */     if (type == 2 || type == 1)
/*     */     {
/* 336 */       return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
/*     */     }
/*     */     
/* 339 */     return image.getRGB(x, y, width, height, pixels, 0, width);
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
/*     */   public static void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
/* 355 */     int type = image.getType();
/*     */     
/* 357 */     if (type == 2 || type == 1) {
/*     */       
/* 359 */       image.getRaster().setDataElements(x, y, width, height, pixels);
/*     */     }
/*     */     else {
/*     */       
/* 363 */       image.setRGB(x, y, width, height, pixels, 0, width);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ImageUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */