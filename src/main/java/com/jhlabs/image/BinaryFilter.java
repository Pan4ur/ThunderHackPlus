/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import com.jhlabs.math.BinaryFunction;
/*    */ import com.jhlabs.math.BlackFunction;
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
/*    */ public abstract class BinaryFilter
/*    */   extends WholeImageFilter
/*    */ {
/* 28 */   protected int newColor = -16777216;
/* 29 */   protected BinaryFunction blackFunction = (BinaryFunction)new BlackFunction();
/* 30 */   protected int iterations = 1;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Colormap colormap;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIterations(int iterations) {
/* 41 */     this.iterations = iterations;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getIterations() {
/* 51 */     return this.iterations;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setColormap(Colormap colormap) {
/* 61 */     this.colormap = colormap;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Colormap getColormap() {
/* 71 */     return this.colormap;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setNewColor(int newColor) {
/* 76 */     this.newColor = newColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNewColor() {
/* 81 */     return this.newColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setBlackFunction(BinaryFunction blackFunction) {
/* 86 */     this.blackFunction = blackFunction;
/*    */   }
/*    */ 
/*    */   
/*    */   public BinaryFunction getBlackFunction() {
/* 91 */     return this.blackFunction;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\BinaryFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */