/*    */ package com.jhlabs.image;
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
/*    */ public class SwizzleFilter
/*    */   extends PointFilter
/*    */ {
/* 29 */   private int[] matrix = new int[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
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
/*    */   public void setMatrix(int[] matrix) {
/* 48 */     this.matrix = matrix;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int[] getMatrix() {
/* 58 */     return this.matrix;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 63 */     int a = rgb >> 24 & 0xFF;
/* 64 */     int r = rgb >> 16 & 0xFF;
/* 65 */     int g = rgb >> 8 & 0xFF;
/* 66 */     int b = rgb & 0xFF;
/* 67 */     a = this.matrix[0] * a + this.matrix[1] * r + this.matrix[2] * g + this.matrix[3] * b + this.matrix[4] * 255;
/* 68 */     r = this.matrix[5] * a + this.matrix[6] * r + this.matrix[7] * g + this.matrix[8] * b + this.matrix[9] * 255;
/* 69 */     g = this.matrix[10] * a + this.matrix[11] * r + this.matrix[12] * g + this.matrix[13] * b + this.matrix[14] * 255;
/* 70 */     b = this.matrix[15] * a + this.matrix[16] * r + this.matrix[17] * g + this.matrix[18] * b + this.matrix[19] * 255;
/* 71 */     a = PixelUtils.clamp(a);
/* 72 */     r = PixelUtils.clamp(r);
/* 73 */     g = PixelUtils.clamp(g);
/* 74 */     b = PixelUtils.clamp(b);
/* 75 */     return a << 24 | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     return "Channels/Swizzle...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\SwizzleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */