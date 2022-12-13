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
/*    */ public class LookupFilter
/*    */   extends PointFilter
/*    */ {
/* 27 */   private Colormap colormap = new Gradient();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LookupFilter() {
/* 34 */     this.canFilterIndexColorModel = true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LookupFilter(Colormap colormap) {
/* 43 */     this.canFilterIndexColorModel = true;
/* 44 */     this.colormap = colormap;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setColormap(Colormap colormap) {
/* 54 */     this.colormap = colormap;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Colormap getColormap() {
/* 64 */     return this.colormap;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 70 */     int r = rgb >> 16 & 0xFF;
/* 71 */     int g = rgb >> 8 & 0xFF;
/* 72 */     int b = rgb & 0xFF;
/* 73 */     rgb = (r + g + b) / 3;
/* 74 */     return this.colormap.getColor(rgb / 255.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     return "Colors/Lookup...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\LookupFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */