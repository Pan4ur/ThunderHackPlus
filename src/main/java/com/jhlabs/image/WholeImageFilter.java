/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.ColorModel;
/*    */ import java.awt.image.WritableRaster;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class WholeImageFilter
/*    */   extends AbstractBufferedImageOp
/*    */ {
/*    */   protected Rectangle transformedSpace;
/*    */   protected Rectangle originalSpace;
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 47 */     int width = src.getWidth();
/* 48 */     int height = src.getHeight();
/* 49 */     int type = src.getType();
/* 50 */     WritableRaster srcRaster = src.getRaster();
/* 51 */     this.originalSpace = new Rectangle(0, 0, width, height);
/* 52 */     this.transformedSpace = new Rectangle(0, 0, width, height);
/* 53 */     transformSpace(this.transformedSpace);
/*    */     
/* 55 */     if (dst == null) {
/*    */       
/* 57 */       ColorModel dstCM = src.getColorModel();
/* 58 */       dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.transformedSpace.width, this.transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
/*    */     } 
/*    */     
/* 61 */     WritableRaster dstRaster = dst.getRaster();
/* 62 */     int[] inPixels = getRGB(src, 0, 0, width, height, null);
/* 63 */     inPixels = filterPixels(width, height, inPixels, this.transformedSpace);
/* 64 */     setRGB(dst, 0, 0, this.transformedSpace.width, this.transformedSpace.height, inPixels);
/* 65 */     return dst;
/*    */   }
/*    */   
/*    */   protected void transformSpace(Rectangle rect) {}
/*    */   
/*    */   protected abstract int[] filterPixels(int paramInt1, int paramInt2, int[] paramArrayOfint, Rectangle paramRectangle);
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\WholeImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */