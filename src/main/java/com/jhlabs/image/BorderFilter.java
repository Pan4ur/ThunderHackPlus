/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Paint;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BorderFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private int leftBorder;
/*     */   private int rightBorder;
/*     */   private int topBorder;
/*     */   private int bottomBorder;
/*     */   private Paint borderPaint;
/*     */   
/*     */   public BorderFilter() {}
/*     */   
/*     */   public BorderFilter(int leftBorder, int topBorder, int rightBorder, int bottomBorder, Paint borderPaint) {
/*  49 */     this.leftBorder = leftBorder;
/*  50 */     this.topBorder = topBorder;
/*  51 */     this.rightBorder = rightBorder;
/*  52 */     this.bottomBorder = bottomBorder;
/*  53 */     this.borderPaint = borderPaint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLeftBorder(int leftBorder) {
/*  64 */     this.leftBorder = leftBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLeftBorder() {
/*  74 */     return this.leftBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRightBorder(int rightBorder) {
/*  85 */     this.rightBorder = rightBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRightBorder() {
/*  95 */     return this.rightBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTopBorder(int topBorder) {
/* 106 */     this.topBorder = topBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTopBorder() {
/* 116 */     return this.topBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBottomBorder(int bottomBorder) {
/* 127 */     this.bottomBorder = bottomBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBottomBorder() {
/* 137 */     return this.bottomBorder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBorderPaint(Paint borderPaint) {
/* 147 */     this.borderPaint = borderPaint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Paint getBorderPaint() {
/* 157 */     return this.borderPaint;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 162 */     int width = src.getWidth();
/* 163 */     int height = src.getHeight();
/*     */     
/* 165 */     if (dst == null)
/*     */     {
/* 167 */       dst = new BufferedImage(width + this.leftBorder + this.rightBorder, height + this.topBorder + this.bottomBorder, src.getType());
/*     */     }
/*     */     
/* 170 */     Graphics2D g = dst.createGraphics();
/*     */     
/* 172 */     if (this.borderPaint != null) {
/*     */       
/* 174 */       g.setPaint(this.borderPaint);
/*     */       
/* 176 */       if (this.leftBorder > 0)
/*     */       {
/* 178 */         g.fillRect(0, 0, this.leftBorder, height);
/*     */       }
/*     */       
/* 181 */       if (this.rightBorder > 0)
/*     */       {
/* 183 */         g.fillRect(width - this.rightBorder, 0, this.rightBorder, height);
/*     */       }
/*     */       
/* 186 */       if (this.topBorder > 0)
/*     */       {
/* 188 */         g.fillRect(this.leftBorder, 0, width - this.leftBorder - this.rightBorder, this.topBorder);
/*     */       }
/*     */       
/* 191 */       if (this.bottomBorder > 0)
/*     */       {
/* 193 */         g.fillRect(this.leftBorder, height - this.bottomBorder, width - this.leftBorder - this.rightBorder, this.bottomBorder);
/*     */       }
/*     */     } 
/*     */     
/* 197 */     g.drawRenderedImage(src, AffineTransform.getTranslateInstance(this.leftBorder, this.rightBorder));
/* 198 */     g.dispose();
/* 199 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 204 */     return "Distort/Border...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\BorderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */