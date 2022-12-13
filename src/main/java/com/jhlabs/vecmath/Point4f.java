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
/*    */ public class Point4f
/*    */   extends Tuple4f
/*    */ {
/*    */   public Point4f() {
/* 26 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public Point4f(float[] x) {
/* 31 */     this.x = x[0];
/* 32 */     this.y = x[1];
/* 33 */     this.z = x[2];
/* 34 */     this.w = x[3];
/*    */   }
/*    */ 
/*    */   
/*    */   public Point4f(float x, float y, float z, float w) {
/* 39 */     this.x = x;
/* 40 */     this.y = y;
/* 41 */     this.z = z;
/* 42 */     this.w = w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Point4f(Point4f t) {
/* 47 */     this.x = t.x;
/* 48 */     this.y = t.y;
/* 49 */     this.z = t.z;
/* 50 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public Point4f(Tuple4f t) {
/* 55 */     this.x = t.x;
/* 56 */     this.y = t.y;
/* 57 */     this.z = t.z;
/* 58 */     this.w = t.w;
/*    */   }
/*    */ 
/*    */   
/*    */   public float distanceL1(Point4f p) {
/* 63 */     return Math.abs(this.x - p.x) + Math.abs(this.y - p.y) + Math.abs(this.z - p.z) + Math.abs(this.w - p.w);
/*    */   }
/*    */ 
/*    */   
/*    */   public float distanceSquared(Point4f p) {
/* 68 */     float dx = this.x - p.x;
/* 69 */     float dy = this.y - p.y;
/* 70 */     float dz = this.z - p.z;
/* 71 */     float dw = this.w - p.w;
/* 72 */     return dx * dx + dy * dy + dz * dz + dw * dw;
/*    */   }
/*    */ 
/*    */   
/*    */   public float distance(Point4f p) {
/* 77 */     float dx = this.x - p.x;
/* 78 */     float dy = this.y - p.y;
/* 79 */     float dz = this.z - p.z;
/* 80 */     float dw = this.w - p.w;
/* 81 */     return (float)Math.sqrt((dx * dx + dy * dy + dz * dz + dw * dw));
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Point4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */