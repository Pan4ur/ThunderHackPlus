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
/*    */ public class AxisAngle4f
/*    */ {
/*    */   public float x;
/*    */   public float y;
/*    */   public float z;
/*    */   public float angle;
/*    */   
/*    */   public AxisAngle4f() {
/* 28 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public AxisAngle4f(float[] x) {
/* 33 */     this.x = x[0];
/* 34 */     this.y = x[1];
/* 35 */     this.z = x[2];
/* 36 */     this.angle = x[2];
/*    */   }
/*    */ 
/*    */   
/*    */   public AxisAngle4f(float x, float y, float z, float angle) {
/* 41 */     this.x = x;
/* 42 */     this.y = y;
/* 43 */     this.z = z;
/* 44 */     this.angle = angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public AxisAngle4f(AxisAngle4f t) {
/* 49 */     this.x = t.x;
/* 50 */     this.y = t.y;
/* 51 */     this.z = t.z;
/* 52 */     this.angle = t.angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public AxisAngle4f(Vector3f v, float angle) {
/* 57 */     this.x = v.x;
/* 58 */     this.y = v.y;
/* 59 */     this.z = v.z;
/* 60 */     this.angle = angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(float x, float y, float z, float angle) {
/* 65 */     this.x = x;
/* 66 */     this.y = y;
/* 67 */     this.z = z;
/* 68 */     this.angle = angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(AxisAngle4f t) {
/* 73 */     this.x = t.x;
/* 74 */     this.y = t.y;
/* 75 */     this.z = t.z;
/* 76 */     this.angle = t.angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public void get(AxisAngle4f t) {
/* 81 */     t.x = this.x;
/* 82 */     t.y = this.y;
/* 83 */     t.z = this.z;
/* 84 */     t.angle = this.angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public void get(float[] t) {
/* 89 */     t[0] = this.x;
/* 90 */     t[1] = this.y;
/* 91 */     t[2] = this.z;
/* 92 */     t[3] = this.angle;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 97 */     return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + "]";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\AxisAngle4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */