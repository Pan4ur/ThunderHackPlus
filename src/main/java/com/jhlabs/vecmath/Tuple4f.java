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
/*     */ public class Tuple4f
/*     */ {
/*     */   public float x;
/*     */   public float y;
/*     */   public float z;
/*     */   public float w;
/*     */   
/*     */   public Tuple4f() {
/*  28 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple4f(float[] x) {
/*  33 */     this.x = x[0];
/*  34 */     this.y = x[1];
/*  35 */     this.z = x[2];
/*  36 */     this.w = x[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple4f(float x, float y, float z, float w) {
/*  41 */     this.x = x;
/*  42 */     this.y = y;
/*  43 */     this.z = z;
/*  44 */     this.w = w;
/*     */   }
/*     */ 
/*     */   
/*     */   public Tuple4f(Tuple4f t) {
/*  49 */     this.x = t.x;
/*  50 */     this.y = t.y;
/*  51 */     this.z = t.z;
/*  52 */     this.w = t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void absolute() {
/*  57 */     this.x = Math.abs(this.x);
/*  58 */     this.y = Math.abs(this.y);
/*  59 */     this.z = Math.abs(this.z);
/*  60 */     this.w = Math.abs(this.w);
/*     */   }
/*     */ 
/*     */   
/*     */   public void absolute(Tuple4f t) {
/*  65 */     this.x = Math.abs(t.x);
/*  66 */     this.y = Math.abs(t.y);
/*  67 */     this.z = Math.abs(t.z);
/*  68 */     this.w = Math.abs(t.w);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clamp(float min, float max) {
/*  73 */     if (this.x < min) {
/*     */       
/*  75 */       this.x = min;
/*     */     }
/*  77 */     else if (this.x > max) {
/*     */       
/*  79 */       this.x = max;
/*     */     } 
/*     */     
/*  82 */     if (this.y < min) {
/*     */       
/*  84 */       this.y = min;
/*     */     }
/*  86 */     else if (this.y > max) {
/*     */       
/*  88 */       this.y = max;
/*     */     } 
/*     */     
/*  91 */     if (this.z < min) {
/*     */       
/*  93 */       this.z = min;
/*     */     }
/*  95 */     else if (this.z > max) {
/*     */       
/*  97 */       this.z = max;
/*     */     } 
/*     */     
/* 100 */     if (this.w < min) {
/*     */       
/* 102 */       this.w = min;
/*     */     }
/* 104 */     else if (this.w > max) {
/*     */       
/* 106 */       this.w = max;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float x, float y, float z, float w) {
/* 112 */     this.x = x;
/* 113 */     this.y = y;
/* 114 */     this.z = z;
/* 115 */     this.w = w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float[] x) {
/* 120 */     this.x = x[0];
/* 121 */     this.y = x[1];
/* 122 */     this.z = x[2];
/* 123 */     this.w = x[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Tuple4f t) {
/* 128 */     this.x = t.x;
/* 129 */     this.y = t.y;
/* 130 */     this.z = t.z;
/* 131 */     this.w = t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(Tuple4f t) {
/* 136 */     t.x = this.x;
/* 137 */     t.y = this.y;
/* 138 */     t.z = this.z;
/* 139 */     t.w = this.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(float[] t) {
/* 144 */     t[0] = this.x;
/* 145 */     t[1] = this.y;
/* 146 */     t[2] = this.z;
/* 147 */     t[3] = this.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void negate() {
/* 152 */     this.x = -this.x;
/* 153 */     this.y = -this.y;
/* 154 */     this.z = -this.z;
/* 155 */     this.w = -this.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void negate(Tuple4f t) {
/* 160 */     this.x = -t.x;
/* 161 */     this.y = -t.y;
/* 162 */     this.z = -t.z;
/* 163 */     this.w = -t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void interpolate(Tuple4f t, float alpha) {
/* 168 */     float a = 1.0F - alpha;
/* 169 */     this.x = a * this.x + alpha * t.x;
/* 170 */     this.y = a * this.y + alpha * t.y;
/* 171 */     this.z = a * this.z + alpha * t.z;
/* 172 */     this.w = a * this.w + alpha * t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void scale(float s) {
/* 177 */     this.x *= s;
/* 178 */     this.y *= s;
/* 179 */     this.z *= s;
/* 180 */     this.w *= s;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Tuple4f t) {
/* 185 */     this.x += t.x;
/* 186 */     this.y += t.y;
/* 187 */     this.z += t.z;
/* 188 */     this.w += t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Tuple4f t1, Tuple4f t2) {
/* 193 */     t1.x += t2.x;
/* 194 */     t1.y += t2.y;
/* 195 */     t1.z += t2.z;
/* 196 */     t1.w += t2.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sub(Tuple4f t) {
/* 201 */     this.x -= t.x;
/* 202 */     this.y -= t.y;
/* 203 */     this.z -= t.z;
/* 204 */     this.w -= t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sub(Tuple4f t1, Tuple4f t2) {
/* 209 */     t1.x -= t2.x;
/* 210 */     t1.y -= t2.y;
/* 211 */     t1.z -= t2.z;
/* 212 */     t1.w -= t2.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 217 */     return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Tuple4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */