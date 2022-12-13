/*    */ package com.jhlabs.vecmath;
/*    */ 
/*    */ import java.awt.Color;
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
/*    */ public class Color4f
/*    */   extends Tuple4f
/*    */ {
/*    */   public Color4f() {
/* 28 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public Color4f(float[] x) {
/* 33 */     this.x = x[0];
/* 34 */     this.y = x[1];
/* 35 */     this.z = x[2];
/* 36 */     this.w = x[3];
/*    */   }
/*    */ 
/*    */   
/*    */   public Color4f(float x, float y, float z, float w) {
/* 41 */     this.x = x;
/* 42 */     this.y = y;
/* 43 */     this.z = z;
/* 44 */     this.w = w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Color4f(Color4f t) {
/* 49 */     this.x = t.x;
/* 50 */     this.y = t.y;
/* 51 */     this.z = t.z;
/* 52 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Color4f(Tuple4f t) {
/* 57 */     this.x = t.x;
/* 58 */     this.y = t.y;
/* 59 */     this.z = t.z;
/* 60 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Color4f(Color c) {
/* 65 */     set(c);
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(Color c) {
/* 70 */     set(c.getRGBComponents(null));
/*    */   }
/*    */ 
/*    */   
/*    */   public Color get() {
/* 75 */     return new Color(this.x, this.y, this.z, this.w);
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Color4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */