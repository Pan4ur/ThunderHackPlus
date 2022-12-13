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
/*    */ public class Point3f
/*    */   extends Tuple3f
/*    */ {
/*    */   public Point3f() {
/* 26 */     this(0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public Point3f(float[] x) {
/* 31 */     this.x = x[0];
/* 32 */     this.y = x[1];
/* 33 */     this.z = x[2];
/*    */   }
/*    */ 
/*    */   
/*    */   public Point3f(float x, float y, float z) {
/* 38 */     this.x = x;
/* 39 */     this.y = y;
/* 40 */     this.z = z;
/*    */   }
/*    */ 
/*    */   
/*    */   public Point3f(Point3f t) {
/* 45 */     this.x = t.x;
/* 46 */     this.y = t.y;
/* 47 */     this.z = t.z;
/*    */   }
/*    */ 
/*    */   
/*    */   public Point3f(Tuple3f t) {
/* 52 */     this.x = t.x;
/* 53 */     this.y = t.y;
/* 54 */     this.z = t.z;
/*    */   }
/*    */ 
/*    */   
/*    */   public float distanceL1(Point3f p) {
/* 59 */     return Math.abs(this.x - p.x) + Math.abs(this.y - p.y) + Math.abs(this.z - p.z);
/*    */   }
/*    */ 
/*    */   
/*    */   public float distanceSquared(Point3f p) {
/* 64 */     float dx = this.x - p.x;
/* 65 */     float dy = this.y - p.y;
/* 66 */     float dz = this.z - p.z;
/* 67 */     return dx * dx + dy * dy + dz * dz;
/*    */   }
/*    */ 
/*    */   
/*    */   public float distance(Point3f p) {
/* 72 */     float dx = this.x - p.x;
/* 73 */     float dy = this.y - p.y;
/* 74 */     float dz = this.z - p.z;
/* 75 */     return (float)Math.sqrt((dx * dx + dy * dy + dz * dz));
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Point3f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */