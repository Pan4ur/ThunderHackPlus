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
/*    */ public class Vector3f
/*    */   extends Tuple3f
/*    */ {
/*    */   public Vector3f() {
/* 26 */     this(0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3f(float[] x) {
/* 31 */     this.x = x[0];
/* 32 */     this.y = x[1];
/* 33 */     this.z = x[2];
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3f(float x, float y, float z) {
/* 38 */     this.x = x;
/* 39 */     this.y = y;
/* 40 */     this.z = z;
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3f(Vector3f t) {
/* 45 */     this.x = t.x;
/* 46 */     this.y = t.y;
/* 47 */     this.z = t.z;
/*    */   }
/*    */ 
/*    */   
/*    */   public Vector3f(Tuple3f t) {
/* 52 */     this.x = t.x;
/* 53 */     this.y = t.y;
/* 54 */     this.z = t.z;
/*    */   }
/*    */ 
/*    */   
/*    */   public float angle(Vector3f v) {
/* 59 */     return (float)Math.acos((dot(v) / length() * v.length()));
/*    */   }
/*    */ 
/*    */   
/*    */   public float dot(Vector3f v) {
/* 64 */     return v.x * this.x + v.y * this.y + v.z * this.z;
/*    */   }
/*    */ 
/*    */   
/*    */   public void cross(Vector3f v1, Vector3f v2) {
/* 69 */     this.x = v1.y * v2.z - v1.z * v2.y;
/* 70 */     this.y = v1.z * v2.x - v1.x * v2.z;
/* 71 */     this.z = v1.x * v2.y - v1.y * v2.x;
/*    */   }
/*    */ 
/*    */   
/*    */   public float length() {
/* 76 */     return (float)Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z));
/*    */   }
/*    */ 
/*    */   
/*    */   public void normalize() {
/* 81 */     float d = 1.0F / (float)Math.sqrt((this.x * this.x + this.y * this.y + this.z * this.z));
/* 82 */     this.x *= d;
/* 83 */     this.y *= d;
/* 84 */     this.z *= d;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Vector3f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */