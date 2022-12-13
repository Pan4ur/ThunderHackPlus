/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
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
/*    */ public abstract class PointFilter
/*    */   extends AbstractBufferedImageOp
/*    */ {
/*    */   protected boolean canFilterIndexColorModel = false;
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 30 */     int width = src.getWidth();
/* 31 */     int height = src.getHeight();
/* 32 */     int type = src.getType();
/* 33 */     WritableRaster srcRaster = src.getRaster();
/*    */     
/* 35 */     if (dst == null)
/*    */     {
/* 37 */       dst = createCompatibleDestImage(src, null);
/*    */     }
/*    */     
/* 40 */     WritableRaster dstRaster = dst.getRaster();
/* 41 */     setDimensions(width, height);
/* 42 */     int[] inPixels = new int[width];
/*    */     
/* 44 */     for (int y = 0; y < height; y++) {
/*    */ 
/*    */       
/* 47 */       if (type == 2) {
/*    */         
/* 49 */         srcRaster.getDataElements(0, y, width, 1, inPixels);
/*    */         
/* 51 */         for (int x = 0; x < width; x++)
/*    */         {
/* 53 */           inPixels[x] = filterRGB(x, y, inPixels[x]);
/*    */         }
/*    */         
/* 56 */         dstRaster.setDataElements(0, y, width, 1, inPixels);
/*    */       }
/*    */       else {
/*    */         
/* 60 */         src.getRGB(0, y, width, 1, inPixels, 0, width);
/*    */         
/* 62 */         for (int x = 0; x < width; x++)
/*    */         {
/* 64 */           inPixels[x] = filterRGB(x, y, inPixels[x]);
/*    */         }
/*    */         
/* 67 */         dst.setRGB(0, y, width, 1, inPixels, 0, width);
/*    */       } 
/*    */     } 
/*    */     
/* 71 */     return dst;
/*    */   }
/*    */   
/*    */   public void setDimensions(int width, int height) {}
/*    */   
/*    */   public abstract int filterRGB(int paramInt1, int paramInt2, int paramInt3);
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PointFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */