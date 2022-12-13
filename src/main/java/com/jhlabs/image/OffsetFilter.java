/*     */ package com.jhlabs.image;
/*     */ 
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
/*     */ public class OffsetFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   private int width;
/*     */   private int height;
/*     */   private int xOffset;
/*     */   private int yOffset;
/*     */   private boolean wrap;
/*     */   
/*     */   public OffsetFilter() {
/*  30 */     this(0, 0, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public OffsetFilter(int xOffset, int yOffset, boolean wrap) {
/*  35 */     this.xOffset = xOffset;
/*  36 */     this.yOffset = yOffset;
/*  37 */     this.wrap = wrap;
/*  38 */     setEdgeAction(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setXOffset(int xOffset) {
/*  43 */     this.xOffset = xOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getXOffset() {
/*  48 */     return this.xOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setYOffset(int yOffset) {
/*  53 */     this.yOffset = yOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getYOffset() {
/*  58 */     return this.yOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWrap(boolean wrap) {
/*  63 */     this.wrap = wrap;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWrap() {
/*  68 */     return this.wrap;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/*  73 */     if (this.wrap) {
/*     */       
/*  75 */       out[0] = ((x + this.width - this.xOffset) % this.width);
/*  76 */       out[1] = ((y + this.height - this.yOffset) % this.height);
/*     */     }
/*     */     else {
/*     */       
/*  80 */       out[0] = (x - this.xOffset);
/*  81 */       out[1] = (y - this.yOffset);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  87 */     this.width = src.getWidth();
/*  88 */     this.height = src.getHeight();
/*     */     
/*  90 */     if (this.wrap) {
/*     */       
/*  92 */       while (this.xOffset < 0)
/*     */       {
/*  94 */         this.xOffset += this.width;
/*     */       }
/*     */       
/*  97 */       while (this.yOffset < 0)
/*     */       {
/*  99 */         this.yOffset += this.height;
/*     */       }
/*     */       
/* 102 */       this.xOffset %= this.width;
/* 103 */       this.yOffset %= this.height;
/*     */     } 
/*     */     
/* 106 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     return "Distort/Offset...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\OffsetFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */