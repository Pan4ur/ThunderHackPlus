/*     */ package com.jhlabs.image;
/*     */ 
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
/*     */ public class FlipFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   public static final int FLIP_H = 1;
/*     */   public static final int FLIP_V = 2;
/*     */   public static final int FLIP_HV = 3;
/*     */   public static final int FLIP_90CW = 4;
/*     */   public static final int FLIP_90CCW = 5;
/*     */   public static final int FLIP_180 = 6;
/*     */   private int operation;
/*     */   private int width;
/*     */   private int height;
/*     */   private int newWidth;
/*     */   private int newHeight;
/*     */   
/*     */   public FlipFilter() {
/*  65 */     this(3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlipFilter(int operation) {
/*  74 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOperation(int operation) {
/*  84 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getOperation() {
/*  94 */     return this.operation;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  99 */     int width = src.getWidth();
/* 100 */     int height = src.getHeight();
/* 101 */     int type = src.getType();
/* 102 */     WritableRaster srcRaster = src.getRaster();
/* 103 */     int[] inPixels = getRGB(src, 0, 0, width, height, null);
/* 104 */     int x = 0, y = 0;
/* 105 */     int w = width;
/* 106 */     int h = height;
/* 107 */     int newX = 0;
/* 108 */     int newY = 0;
/* 109 */     int newW = w;
/* 110 */     int newH = h;
/*     */     
/* 112 */     switch (this.operation) {
/*     */       
/*     */       case 1:
/* 115 */         newX = width - x + w;
/*     */         break;
/*     */       
/*     */       case 2:
/* 119 */         newY = height - y + h;
/*     */         break;
/*     */       
/*     */       case 3:
/* 123 */         newW = h;
/* 124 */         newH = w;
/* 125 */         newX = y;
/* 126 */         newY = x;
/*     */         break;
/*     */       
/*     */       case 4:
/* 130 */         newW = h;
/* 131 */         newH = w;
/* 132 */         newX = height - y + h;
/* 133 */         newY = x;
/*     */         break;
/*     */       
/*     */       case 5:
/* 137 */         newW = h;
/* 138 */         newH = w;
/* 139 */         newX = y;
/* 140 */         newY = width - x + w;
/*     */         break;
/*     */       
/*     */       case 6:
/* 144 */         newX = width - x + w;
/* 145 */         newY = height - y + h;
/*     */         break;
/*     */     } 
/*     */     
/* 149 */     int[] newPixels = new int[newW * newH];
/*     */     
/* 151 */     for (int row = 0; row < h; row++) {
/*     */       
/* 153 */       for (int col = 0; col < w; col++) {
/*     */         
/* 155 */         int index = row * width + col;
/* 156 */         int newRow = row;
/* 157 */         int newCol = col;
/*     */         
/* 159 */         switch (this.operation) {
/*     */           
/*     */           case 1:
/* 162 */             newCol = w - col - 1;
/*     */             break;
/*     */           
/*     */           case 2:
/* 166 */             newRow = h - row - 1;
/*     */             break;
/*     */           
/*     */           case 3:
/* 170 */             newRow = col;
/* 171 */             newCol = row;
/*     */             break;
/*     */           
/*     */           case 4:
/* 175 */             newRow = col;
/* 176 */             newCol = h - row - 1;
/*     */             break;
/*     */           
/*     */           case 5:
/* 180 */             newRow = w - col - 1;
/* 181 */             newCol = row;
/*     */             break;
/*     */           
/*     */           case 6:
/* 185 */             newRow = h - row - 1;
/* 186 */             newCol = w - col - 1;
/*     */             break;
/*     */         } 
/*     */         
/* 190 */         int newIndex = newRow * newW + newCol;
/* 191 */         newPixels[newIndex] = inPixels[index];
/*     */       } 
/*     */     } 
/*     */     
/* 195 */     if (dst == null) {
/*     */       
/* 197 */       ColorModel dstCM = src.getColorModel();
/* 198 */       dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(newW, newH), dstCM.isAlphaPremultiplied(), null);
/*     */     } 
/*     */     
/* 201 */     WritableRaster dstRaster = dst.getRaster();
/* 202 */     setRGB(dst, 0, 0, newW, newH, newPixels);
/* 203 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 208 */     switch (this.operation) {
/*     */       
/*     */       case 1:
/* 211 */         return "Flip Horizontal";
/*     */       
/*     */       case 2:
/* 214 */         return "Flip Vertical";
/*     */       
/*     */       case 3:
/* 217 */         return "Flip Diagonal";
/*     */       
/*     */       case 4:
/* 220 */         return "Rotate 90";
/*     */       
/*     */       case 5:
/* 223 */         return "Rotate -90";
/*     */       
/*     */       case 6:
/* 226 */         return "Rotate 180";
/*     */     } 
/*     */     
/* 229 */     return "Flip";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FlipFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */