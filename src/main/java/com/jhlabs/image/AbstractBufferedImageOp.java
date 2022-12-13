/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
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
/*     */ public abstract class AbstractBufferedImageOp
/*     */   implements BufferedImageOp, Cloneable
/*     */ {
/*     */   public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
/*  30 */     if (dstCM == null)
/*     */     {
/*  32 */       dstCM = src.getColorModel();
/*     */     }
/*     */     
/*  35 */     return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Rectangle2D getBounds2D(BufferedImage src) {
/*  40 */     return new Rectangle(0, 0, src.getWidth(), src.getHeight());
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
/*  45 */     if (dstPt == null)
/*     */     {
/*  47 */       dstPt = new Point2D.Double();
/*     */     }
/*     */     
/*  50 */     dstPt.setLocation(srcPt.getX(), srcPt.getY());
/*  51 */     return dstPt;
/*     */   }
/*     */ 
/*     */   
/*     */   public RenderingHints getRenderingHints() {
/*  56 */     return null;
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
/*     */   public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
/*  73 */     int type = image.getType();
/*     */     
/*  75 */     if (type == 2 || type == 1)
/*     */     {
/*  77 */       return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
/*     */     }
/*     */     
/*  80 */     return image.getRGB(x, y, width, height, pixels, 0, width);
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
/*     */   public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
/*  96 */     int type = image.getType();
/*     */     
/*  98 */     if (type == 2 || type == 1) {
/*     */       
/* 100 */       image.getRaster().setDataElements(x, y, width, height, pixels);
/*     */     }
/*     */     else {
/*     */       
/* 104 */       image.setRGB(x, y, width, height, pixels, 0, width);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 112 */       return super.clone();
/*     */     }
/* 114 */     catch (CloneNotSupportedException e) {
/*     */       
/* 116 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\AbstractBufferedImageOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */