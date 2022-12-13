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
/*     */ public class Matrix4f
/*     */ {
/*     */   public float m00;
/*     */   public float m01;
/*     */   public float m02;
/*     */   public float m03;
/*     */   public float m10;
/*     */   public float m11;
/*     */   public float m12;
/*     */   public float m13;
/*     */   public float m20;
/*     */   public float m21;
/*     */   public float m22;
/*     */   public float m23;
/*     */   public float m30;
/*     */   public float m31;
/*     */   public float m32;
/*     */   public float m33;
/*     */   
/*     */   public Matrix4f() {
/*  31 */     setIdentity();
/*     */   }
/*     */ 
/*     */   
/*     */   public Matrix4f(Matrix4f m) {
/*  36 */     set(m);
/*     */   }
/*     */ 
/*     */   
/*     */   public Matrix4f(float[] m) {
/*  41 */     set(m);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Matrix4f m) {
/*  46 */     this.m00 = m.m00;
/*  47 */     this.m01 = m.m01;
/*  48 */     this.m02 = m.m02;
/*  49 */     this.m03 = m.m03;
/*  50 */     this.m10 = m.m10;
/*  51 */     this.m11 = m.m11;
/*  52 */     this.m12 = m.m12;
/*  53 */     this.m13 = m.m13;
/*  54 */     this.m20 = m.m20;
/*  55 */     this.m21 = m.m21;
/*  56 */     this.m22 = m.m22;
/*  57 */     this.m23 = m.m23;
/*  58 */     this.m30 = m.m30;
/*  59 */     this.m31 = m.m31;
/*  60 */     this.m32 = m.m32;
/*  61 */     this.m33 = m.m33;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float[] m) {
/*  66 */     this.m00 = m[0];
/*  67 */     this.m01 = m[1];
/*  68 */     this.m02 = m[2];
/*  69 */     this.m03 = m[3];
/*  70 */     this.m10 = m[4];
/*  71 */     this.m11 = m[5];
/*  72 */     this.m12 = m[6];
/*  73 */     this.m13 = m[7];
/*  74 */     this.m20 = m[8];
/*  75 */     this.m21 = m[9];
/*  76 */     this.m22 = m[10];
/*  77 */     this.m23 = m[11];
/*  78 */     this.m30 = m[12];
/*  79 */     this.m31 = m[13];
/*  80 */     this.m32 = m[14];
/*  81 */     this.m33 = m[15];
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(Matrix4f m) {
/*  86 */     m.m00 = this.m00;
/*  87 */     m.m01 = this.m01;
/*  88 */     m.m02 = this.m02;
/*  89 */     m.m03 = this.m03;
/*  90 */     m.m10 = this.m10;
/*  91 */     m.m11 = this.m11;
/*  92 */     m.m12 = this.m12;
/*  93 */     m.m13 = this.m13;
/*  94 */     m.m20 = this.m20;
/*  95 */     m.m21 = this.m21;
/*  96 */     m.m22 = this.m22;
/*  97 */     m.m23 = this.m23;
/*  98 */     m.m30 = this.m30;
/*  99 */     m.m31 = this.m31;
/* 100 */     m.m32 = this.m32;
/* 101 */     m.m33 = this.m33;
/*     */   }
/*     */ 
/*     */   
/*     */   public void get(float[] m) {
/* 106 */     m[0] = this.m00;
/* 107 */     m[1] = this.m01;
/* 108 */     m[2] = this.m02;
/* 109 */     m[3] = this.m03;
/* 110 */     m[4] = this.m10;
/* 111 */     m[5] = this.m11;
/* 112 */     m[6] = this.m12;
/* 113 */     m[7] = this.m13;
/* 114 */     m[8] = this.m20;
/* 115 */     m[9] = this.m21;
/* 116 */     m[10] = this.m22;
/* 117 */     m[11] = this.m23;
/* 118 */     m[12] = this.m30;
/* 119 */     m[13] = this.m31;
/* 120 */     m[14] = this.m32;
/* 121 */     m[15] = this.m33;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIdentity() {
/* 126 */     this.m00 = 1.0F;
/* 127 */     this.m01 = 0.0F;
/* 128 */     this.m02 = 0.0F;
/* 129 */     this.m03 = 0.0F;
/* 130 */     this.m10 = 0.0F;
/* 131 */     this.m11 = 1.0F;
/* 132 */     this.m12 = 0.0F;
/* 133 */     this.m13 = 0.0F;
/* 134 */     this.m20 = 0.0F;
/* 135 */     this.m21 = 0.0F;
/* 136 */     this.m22 = 1.0F;
/* 137 */     this.m23 = 0.0F;
/* 138 */     this.m30 = 0.0F;
/* 139 */     this.m31 = 0.0F;
/* 140 */     this.m32 = 0.0F;
/* 141 */     this.m33 = 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mul(Matrix4f m) {
/* 146 */     float tm00 = this.m00;
/* 147 */     float tm01 = this.m01;
/* 148 */     float tm02 = this.m02;
/* 149 */     float tm03 = this.m03;
/* 150 */     float tm10 = this.m10;
/* 151 */     float tm11 = this.m11;
/* 152 */     float tm12 = this.m12;
/* 153 */     float tm13 = this.m13;
/* 154 */     float tm20 = this.m20;
/* 155 */     float tm21 = this.m21;
/* 156 */     float tm22 = this.m22;
/* 157 */     float tm23 = this.m23;
/* 158 */     float tm30 = this.m30;
/* 159 */     float tm31 = this.m31;
/* 160 */     float tm32 = this.m32;
/* 161 */     float tm33 = this.m33;
/* 162 */     this.m00 = tm00 * m.m00 + tm10 * m.m01 + tm20 * m.m02 + tm30 * m.m03;
/* 163 */     this.m01 = tm01 * m.m00 + tm11 * m.m01 + tm21 * m.m02 + tm31 * m.m03;
/* 164 */     this.m02 = tm02 * m.m00 + tm12 * m.m01 + tm22 * m.m02 + tm32 * m.m03;
/* 165 */     this.m03 = tm03 * m.m00 + tm13 * m.m01 + tm23 * m.m02 + tm33 * m.m03;
/* 166 */     this.m10 = tm00 * m.m10 + tm10 * m.m11 + tm20 * m.m12 + tm30 * m.m13;
/* 167 */     this.m11 = tm01 * m.m10 + tm11 * m.m11 + tm21 * m.m12 + tm31 * m.m13;
/* 168 */     this.m12 = tm02 * m.m10 + tm12 * m.m11 + tm22 * m.m12 + tm32 * m.m13;
/* 169 */     this.m13 = tm03 * m.m10 + tm13 * m.m11 + tm23 * m.m12 + tm33 * m.m13;
/* 170 */     this.m20 = tm00 * m.m20 + tm10 * m.m21 + tm20 * m.m22 + tm30 * m.m23;
/* 171 */     this.m21 = tm01 * m.m20 + tm11 * m.m21 + tm21 * m.m22 + tm31 * m.m23;
/* 172 */     this.m22 = tm02 * m.m20 + tm12 * m.m21 + tm22 * m.m22 + tm32 * m.m23;
/* 173 */     this.m23 = tm03 * m.m20 + tm13 * m.m21 + tm23 * m.m22 + tm33 * m.m23;
/* 174 */     this.m30 = tm00 * m.m30 + tm10 * m.m31 + tm20 * m.m32 + tm30 * m.m33;
/* 175 */     this.m31 = tm01 * m.m30 + tm11 * m.m31 + tm21 * m.m32 + tm31 * m.m33;
/* 176 */     this.m32 = tm02 * m.m30 + tm12 * m.m31 + tm22 * m.m32 + tm32 * m.m33;
/* 177 */     this.m33 = tm03 * m.m30 + tm13 * m.m31 + tm23 * m.m32 + tm33 * m.m33;
/*     */   }
/*     */ 
/*     */   
/*     */   public void invert() {
/* 182 */     Matrix4f t = new Matrix4f(this);
/* 183 */     invert(t);
/*     */   }
/*     */ 
/*     */   
/*     */   public void invert(Matrix4f t) {
/* 188 */     this.m00 = t.m00;
/* 189 */     this.m01 = t.m10;
/* 190 */     this.m02 = t.m20;
/* 191 */     this.m03 = t.m03;
/* 192 */     this.m10 = t.m01;
/* 193 */     this.m11 = t.m11;
/* 194 */     this.m12 = t.m21;
/* 195 */     this.m13 = t.m13;
/* 196 */     this.m20 = t.m02;
/* 197 */     this.m21 = t.m12;
/* 198 */     this.m22 = t.m22;
/* 199 */     this.m23 = t.m23;
/* 200 */     this.m30 *= -1.0F;
/* 201 */     this.m31 *= -1.0F;
/* 202 */     this.m32 *= -1.0F;
/* 203 */     this.m33 = t.m33;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(AxisAngle4f a) {
/* 208 */     float halfTheta = a.angle * 0.5F;
/* 209 */     float cosHalfTheta = (float)Math.cos(halfTheta);
/* 210 */     float sinHalfTheta = (float)Math.sin(halfTheta);
/* 211 */     set(new Quat4f(a.x * sinHalfTheta, a.y * sinHalfTheta, a.z * sinHalfTheta, cosHalfTheta));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(Quat4f q) {
/* 217 */     float x2 = q.x + q.x;
/* 218 */     float y2 = q.y + q.y;
/* 219 */     float z2 = q.z + q.z;
/* 220 */     float xx = q.x * x2;
/* 221 */     float xy = q.x * y2;
/* 222 */     float xz = q.x * z2;
/* 223 */     float yy = q.y * y2;
/* 224 */     float yz = q.y * z2;
/* 225 */     float zz = q.z * z2;
/* 226 */     float wx = q.w * x2;
/* 227 */     float wy = q.w * y2;
/* 228 */     float wz = q.w * z2;
/* 229 */     this.m00 = 1.0F - yy + zz;
/* 230 */     this.m01 = xy - wz;
/* 231 */     this.m02 = xz + wy;
/* 232 */     this.m03 = 0.0F;
/* 233 */     this.m10 = xy + wz;
/* 234 */     this.m11 = 1.0F - xx + zz;
/* 235 */     this.m12 = yz - wx;
/* 236 */     this.m13 = 0.0F;
/* 237 */     this.m20 = xz - wy;
/* 238 */     this.m21 = yz + wx;
/* 239 */     this.m22 = 1.0F - xx + yy;
/* 240 */     this.m23 = 0.0F;
/* 241 */     this.m30 = 0.0F;
/* 242 */     this.m31 = 0.0F;
/* 243 */     this.m32 = 0.0F;
/* 244 */     this.m33 = 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void transform(Point3f v) {
/* 249 */     float x = v.x * this.m00 + v.y * this.m10 + v.z * this.m20 + this.m30;
/* 250 */     float y = v.x * this.m01 + v.y * this.m11 + v.z * this.m21 + this.m31;
/* 251 */     float z = v.x * this.m02 + v.y * this.m12 + v.z * this.m22 + this.m32;
/* 252 */     v.x = x;
/* 253 */     v.y = y;
/* 254 */     v.z = z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void transform(Vector3f v) {
/* 259 */     float x = v.x * this.m00 + v.y * this.m10 + v.z * this.m20;
/* 260 */     float y = v.x * this.m01 + v.y * this.m11 + v.z * this.m21;
/* 261 */     float z = v.x * this.m02 + v.y * this.m12 + v.z * this.m22;
/* 262 */     v.x = x;
/* 263 */     v.y = y;
/* 264 */     v.z = z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTranslation(Vector3f v) {
/* 269 */     this.m30 = v.x;
/* 270 */     this.m31 = v.y;
/* 271 */     this.m32 = v.z;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(float scale) {
/* 276 */     this.m00 = scale;
/* 277 */     this.m11 = scale;
/* 278 */     this.m22 = scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public void rotX(float angle) {
/* 283 */     float s = (float)Math.sin(angle);
/* 284 */     float c = (float)Math.cos(angle);
/* 285 */     this.m00 = 1.0F;
/* 286 */     this.m01 = 0.0F;
/* 287 */     this.m02 = 0.0F;
/* 288 */     this.m03 = 0.0F;
/* 289 */     this.m10 = 0.0F;
/* 290 */     this.m11 = c;
/* 291 */     this.m12 = s;
/* 292 */     this.m13 = 0.0F;
/* 293 */     this.m20 = 0.0F;
/* 294 */     this.m21 = -s;
/* 295 */     this.m22 = c;
/* 296 */     this.m23 = 0.0F;
/* 297 */     this.m30 = 0.0F;
/* 298 */     this.m31 = 0.0F;
/* 299 */     this.m32 = 0.0F;
/* 300 */     this.m33 = 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void rotY(float angle) {
/* 305 */     float s = (float)Math.sin(angle);
/* 306 */     float c = (float)Math.cos(angle);
/* 307 */     this.m00 = c;
/* 308 */     this.m01 = 0.0F;
/* 309 */     this.m02 = -s;
/* 310 */     this.m03 = 0.0F;
/* 311 */     this.m10 = 0.0F;
/* 312 */     this.m11 = 1.0F;
/* 313 */     this.m12 = 0.0F;
/* 314 */     this.m13 = 0.0F;
/* 315 */     this.m20 = s;
/* 316 */     this.m21 = 0.0F;
/* 317 */     this.m22 = c;
/* 318 */     this.m23 = 0.0F;
/* 319 */     this.m30 = 0.0F;
/* 320 */     this.m31 = 0.0F;
/* 321 */     this.m32 = 0.0F;
/* 322 */     this.m33 = 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void rotZ(float angle) {
/* 327 */     float s = (float)Math.sin(angle);
/* 328 */     float c = (float)Math.cos(angle);
/* 329 */     this.m00 = c;
/* 330 */     this.m01 = s;
/* 331 */     this.m02 = 0.0F;
/* 332 */     this.m03 = 0.0F;
/* 333 */     this.m10 = -s;
/* 334 */     this.m11 = c;
/* 335 */     this.m12 = 0.0F;
/* 336 */     this.m13 = 0.0F;
/* 337 */     this.m20 = 0.0F;
/* 338 */     this.m21 = 0.0F;
/* 339 */     this.m22 = 1.0F;
/* 340 */     this.m23 = 0.0F;
/* 341 */     this.m30 = 0.0F;
/* 342 */     this.m31 = 0.0F;
/* 343 */     this.m32 = 0.0F;
/* 344 */     this.m33 = 1.0F;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Matrix4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */