/*    */ package com.jhlabs.vecmath;
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
/*    */ public class Vector4f
/*    */   extends Tuple4f
/*    */ {
/*    */   public Vector4f() {
/* 26 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector4f(float[] x) {
/* 31 */     this.x = x[0];
/* 32 */     this.y = x[1];
/* 33 */     this.z = x[2];
/* 34 */     this.w = x[2];
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector4f(float x, float y, float z, float w) {
/* 39 */     this.x = x;
/* 40 */     this.y = y;
/* 41 */     this.z = z;
/* 42 */     this.w = w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector4f(Vector4f t) {
/* 47 */     this.x = t.x;
/* 48 */     this.y = t.y;
/* 49 */     this.z = t.z;
/* 50 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector4f(Tuple4f t) {
/* 55 */     this.x = t.x;
/* 56 */     this.y = t.y;
/* 57 */     this.z = t.z;
/* 58 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public float dot(Vector4f v) {
/* 63 */     return v.x * this.x + v.y * this.y + v.z * this.z + v.w * this.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public float length() {
/* 68 */     return (float)Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
/*    */   }
/*    */ 
/*    */   
/*    */   public void normalize() {
/* 73 */     float d = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
/* 74 */     this.x *= d;
/* 75 */     this.y *= d;
/* 76 */     this.z *= d;
/* 77 */     this.w *= d;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Vector4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */