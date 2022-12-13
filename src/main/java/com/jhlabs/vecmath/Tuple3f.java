/*     */ package com.jhlabs.vecmath;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Tuple3f
/*     */ {
/*     */   public float x;
/*     */   public float y;
/*     */   public float z;
/*     */   
/*     */   public Tuple3f() {
/*  28 */     this(0.0F, 0.0F, 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple3f(float[] x) {
/*  33 */     this.x = x[0];
/*  34 */     this.y = x[1];
/*  35 */     this.z = x[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple3f(float x, float y, float z) {
/*  40 */     this.x = x;
/*  41 */     this.y = y;
/*  42 */     this.z = z;
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple3f(Tuple3f t) {
/*  47 */     this.x = t.x;
/*  48 */     this.y = t.y;
/*  49 */     this.z = t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void absolute() {
/*  54 */     this.x = Math.abs(this.x);
/*  55 */     this.y = Math.abs(this.y);
/*  56 */     this.z = Math.abs(this.z);
/*     */   }
/*     */ 
/*     */   
/*     */   public void absolute(Tuple3f t) {
/*  61 */     this.x = Math.abs(t.x);
/*  62 */     this.y = Math.abs(t.y);
/*  63 */     this.z = Math.abs(t.z);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clamp(float min, float max) {
/*  68 */     if (this.x < min) {
/*     */       
/*  70 */       this.x = min;
/*     */     }
/*  72 */     else if (this.x > max) {
/*     */       
/*  74 */       this.x = max;
/*     */     } 
/*     */     
/*  77 */     if (this.y < min) {
/*     */       
/*  79 */       this.y = min;
/*     */     }
/*  81 */     else if (this.y > max) {
/*     */       
/*  83 */       this.y = max;
/*     */     } 
/*     */     
/*  86 */     if (this.z < min) {
/*     */       
/*  88 */       this.z = min;
/*     */     }
/*  90 */     else if (this.z > max) {
/*     */       
/*  92 */       this.z = max;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float x, float y, float z) {
/*  98 */     this.x = x;
/*  99 */     this.y = y;
/* 100 */     this.z = z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float[] x) {
/* 105 */     this.x = x[0];
/* 106 */     this.y = x[1];
/* 107 */     this.z = x[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Tuple3f t) {
/* 112 */     this.x = t.x;
/* 113 */     this.y = t.y;
/* 114 */     this.z = t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(Tuple3f t) {
/* 119 */     t.x = this.x;
/* 120 */     t.y = this.y;
/* 121 */     t.z = this.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(float[] t) {
/* 126 */     t[0] = this.x;
/* 127 */     t[1] = this.y;
/* 128 */     t[2] = this.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void negate() {
/* 133 */     this.x = -this.x;
/* 134 */     this.y = -this.y;
/* 135 */     this.z = -this.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void negate(Tuple3f t) {
/* 140 */     this.x = -t.x;
/* 141 */     this.y = -t.y;
/* 142 */     this.z = -t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void interpolate(Tuple3f t, float alpha) {
/* 147 */     float a = 1.0F - alpha;
/* 148 */     this.x = a * this.x + alpha * t.x;
/* 149 */     this.y = a * this.y + alpha * t.y;
/* 150 */     this.z = a * this.z + alpha * t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void scale(float s) {
/* 155 */     this.x *= s;
/* 156 */     this.y *= s;
/* 157 */     this.z *= s;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Tuple3f t) {
/* 162 */     this.x += t.x;
/* 163 */     this.y += t.y;
/* 164 */     this.z += t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Tuple3f t1, Tuple3f t2) {
/* 169 */     t1.x += t2.x;
/* 170 */     t1.y += t2.y;
/* 171 */     t1.z += t2.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sub(Tuple3f t) {
/* 176 */     this.x -= t.x;
/* 177 */     this.y -= t.y;
/* 178 */     this.z -= t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sub(Tuple3f t1, Tuple3f t2) {
/* 183 */     t1.x -= t2.x;
/* 184 */     t1.y -= t2.y;
/* 185 */     t1.z -= t2.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void scaleAdd(float s, Tuple3f t) {
/* 190 */     this.x += s * t.x;
/* 191 */     this.y += s * t.y;
/* 192 */     this.z += s * t.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void scaleAdd(float s, Tuple3f t1, Tuple3f t2) {
/* 197 */     this.x = s * t1.x + t2.x;
/* 198 */     this.y = s * t1.y + t2.y;
/* 199 */     this.z = s * t1.z + t2.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 204 */     return "[" + this.x + ", " + this.y + ", " + this.z + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Tuple3f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */