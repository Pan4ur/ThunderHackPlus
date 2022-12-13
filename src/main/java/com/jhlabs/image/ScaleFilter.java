/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.ColorModel;
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
/*    */ public class ScaleFilter
/*    */   extends AbstractBufferedImageOp
/*    */ {
/*    */   private int width;
/*    */   private int height;
/*    */   
/*    */   public ScaleFilter() {
/* 35 */     this(32, 32);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ScaleFilter(int width, int height) {
/* 45 */     this.width = width;
/* 46 */     this.height = height;
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 51 */     if (dst == null) {
/*    */       
/* 53 */       ColorModel dstCM = src.getColorModel();
/* 54 */       dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
/*    */     } 
/*    */     
/* 57 */     Image scaleImage = src.getScaledInstance(this.width, this.height, 16);
/* 58 */     Graphics2D g = dst.createGraphics();
/* 59 */     g.drawImage(scaleImage, 0, 0, this.width, this.height, null);
/* 60 */     g.dispose();
/* 61 */     return dst;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "Distort/Scale";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\ScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */