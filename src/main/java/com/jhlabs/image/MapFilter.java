/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import com.jhlabs.math.Function2D;
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
/*    */ public class MapFilter
/*    */   extends TransformFilter
/*    */ {
/*    */   private Function2D xMapFunction;
/*    */   private Function2D yMapFunction;
/*    */   
/*    */   public void setXMapFunction(Function2D xMapFunction) {
/* 34 */     this.xMapFunction = xMapFunction;
/*    */   }
/*    */ 
/*    */   
/*    */   public Function2D getXMapFunction() {
/* 39 */     return this.xMapFunction;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setYMapFunction(Function2D yMapFunction) {
/* 44 */     this.yMapFunction = yMapFunction;
/*    */   }
/*    */ 
/*    */   
/*    */   public Function2D getYMapFunction() {
/* 49 */     return this.yMapFunction;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void transformInverse(int x, int y, float[] out) {
/* 55 */     float xMap = this.xMapFunction.evaluate(x, y);
/* 56 */     float yMap = this.yMapFunction.evaluate(x, y);
/* 57 */     out[0] = xMap * this.transformedSpace.width;
/* 58 */     out[1] = yMap * this.transformedSpace.height;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 63 */     return "Distort/Map Coordinates...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\MapFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */