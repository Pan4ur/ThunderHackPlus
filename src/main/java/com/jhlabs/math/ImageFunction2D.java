/*     */ package com.jhlabs.math;
/*     */ 
/*     */ import com.jhlabs.image.ImageMath;
/*     */ import com.jhlabs.image.PixelUtils;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.PixelGrabber;
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
/*     */ public class ImageFunction2D
/*     */   implements Function2D
/*     */ {
/*     */   public static final int ZERO = 0;
/*     */   public static final int CLAMP = 1;
/*     */   public static final int WRAP = 2;
/*     */   protected int[] pixels;
/*     */   protected int width;
/*     */   protected int height;
/*  32 */   protected int edgeAction = 0;
/*     */   
/*     */   protected boolean alpha = false;
/*     */   
/*     */   public ImageFunction2D(BufferedImage image) {
/*  37 */     this(image, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageFunction2D(BufferedImage image, boolean alpha) {
/*  42 */     this(image, 0, alpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageFunction2D(BufferedImage image, int edgeAction, boolean alpha) {
/*  47 */     init(getRGB(image, 0, 0, image.getWidth(), image.getHeight(), null), image.getWidth(), image.getHeight(), edgeAction, alpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageFunction2D(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
/*  52 */     init(pixels, width, height, edgeAction, alpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageFunction2D(Image image) {
/*  57 */     this(image, 0, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageFunction2D(Image image, int edgeAction, boolean alpha) {
/*  62 */     PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, null, 0, -1);
/*     */ 
/*     */     
/*     */     try {
/*  66 */       pg.grabPixels();
/*     */     }
/*  68 */     catch (InterruptedException e) {
/*     */       
/*  70 */       throw new RuntimeException("interrupted waiting for pixels!");
/*     */     } 
/*     */     
/*  73 */     if ((pg.status() & 0x80) != 0)
/*     */     {
/*  75 */       throw new RuntimeException("image fetch aborted");
/*     */     }
/*     */     
/*  78 */     init((int[])pg.getPixels(), pg.getWidth(), pg.getHeight(), edgeAction, alpha);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
/*  87 */     int type = image.getType();
/*     */     
/*  89 */     if (type == 2 || type == 1)
/*     */     {
/*  91 */       return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
/*     */     }
/*     */     
/*  94 */     return image.getRGB(x, y, width, height, pixels, 0, width);
/*     */   }
/*     */ 
/*     */   
/*     */   public void init(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
/*  99 */     this.pixels = pixels;
/* 100 */     this.width = width;
/* 101 */     this.height = height;
/* 102 */     this.edgeAction = edgeAction;
/* 103 */     this.alpha = alpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public float evaluate(float x, float y) {
/* 108 */     int ix = (int)x;
/* 109 */     int iy = (int)y;
/*     */     
/* 111 */     if (this.edgeAction == 2) {
/*     */       
/* 113 */       ix = ImageMath.mod(ix, this.width);
/* 114 */       iy = ImageMath.mod(iy, this.height);
/*     */     }
/* 116 */     else if (ix < 0 || iy < 0 || ix >= this.width || iy >= this.height) {
/*     */       
/* 118 */       if (this.edgeAction == 0)
/*     */       {
/* 120 */         return 0.0F;
/*     */       }
/*     */       
/* 123 */       if (ix < 0) {
/*     */         
/* 125 */         ix = 0;
/*     */       }
/* 127 */       else if (ix >= this.width) {
/*     */         
/* 129 */         ix = this.width - 1;
/*     */       } 
/*     */       
/* 132 */       if (iy < 0) {
/*     */         
/* 134 */         iy = 0;
/*     */       }
/* 136 */       else if (iy >= this.height) {
/*     */         
/* 138 */         iy = this.height - 1;
/*     */       } 
/*     */     } 
/*     */     
/* 142 */     return this.alpha ? ((this.pixels[iy * this.width + ix] >> 24 & 0xFF) / 255.0F) : (PixelUtils.brightness(this.pixels[iy * this.width + ix]) / 255.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEdgeAction(int edgeAction) {
/* 147 */     this.edgeAction = edgeAction;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getEdgeAction() {
/* 152 */     return this.edgeAction;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWidth() {
/* 157 */     return this.width;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 162 */     return this.height;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getPixels() {
/* 167 */     return this.pixels;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\ImageFunction2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */