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
/*    */ public class LinearColormap
/*    */   implements Colormap
/*    */ {
/*    */   private int color1;
/*    */   private int color2;
/*    */   
/*    */   public LinearColormap() {
/* 32 */     this(-16777216, -1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LinearColormap(int color1, int color2) {
/* 42 */     this.color1 = color1;
/* 43 */     this.color2 = color2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setColor1(int color1) {
/* 52 */     this.color1 = color1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getColor1() {
/* 61 */     return this.color1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setColor2(int color2) {
/* 70 */     this.color2 = color2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getColor2() {
/* 79 */     return this.color2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getColor(float v) {
/* 89 */     return ImageMath.mixColors(ImageMath.clamp(v, 0.0F, 1.0F), this.color1, this.color2);
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LinearColormap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */