/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.BufferedImageOp;
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
/*    */ public class IteratedFilter
/*    */   extends AbstractBufferedImageOp
/*    */ {
/*    */   private BufferedImageOp filter;
/*    */   private int iterations;
/*    */   
/*    */   public IteratedFilter(BufferedImageOp filter, int iterations) {
/* 36 */     this.filter = filter;
/* 37 */     this.iterations = iterations;
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 42 */     BufferedImage image = src;
/*    */     
/* 44 */     for (int i = 0; i < this.iterations; i++)
/*    */     {
/* 46 */       image = this.filter.filter(image, dst);
/*    */     }
/*    */     
/* 49 */     return image;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\IteratedFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */