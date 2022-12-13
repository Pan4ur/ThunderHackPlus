/*     */ package com.jhlabs.math;
/*     */ 
/*     */ import java.util.Random;
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
/*     */ public class Noise
/*     */   implements Function1D, Function2D, Function3D
/*     */ {
/*  26 */   private static Random randomGenerator = new Random();
/*     */   private static final int B = 256;
/*     */   
/*     */   public float evaluate(float x) {
/*  30 */     return noise1(x);
/*     */   }
/*     */   private static final int BM = 255; private static final int N = 4096;
/*     */   
/*     */   public float evaluate(float x, float y) {
/*  35 */     return noise2(x, y);
/*     */   }
/*     */ 
/*     */   
/*     */   public float evaluate(float x, float y, float z) {
/*  40 */     return noise3(x, y, z);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float turbulence2(float x, float y, float octaves) {
/*  52 */     float t = 0.0F;
/*     */     float f;
/*  54 */     for (f = 1.0F; f <= octaves; f *= 2.0F)
/*     */     {
/*  56 */       t += Math.abs(noise2(f * x, f * y)) / f;
/*     */     }
/*     */     
/*  59 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float turbulence3(float x, float y, float z, float octaves) {
/*  71 */     float t = 0.0F;
/*     */     float f;
/*  73 */     for (f = 1.0F; f <= octaves; f *= 2.0F)
/*     */     {
/*  75 */       t += Math.abs(noise3(f * x, f * y, f * z)) / f;
/*     */     }
/*     */     
/*  78 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   static int[] p = new int[514];
/*  86 */   static float[][] g3 = new float[514][3];
/*  87 */   static float[][] g2 = new float[514][2];
/*  88 */   static float[] g1 = new float[514];
/*     */   
/*     */   static boolean start = true;
/*     */   
/*     */   private static float sCurve(float t) {
/*  93 */     return t * t * (3.0F - 2.0F * t);
/*     */   }
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
/*     */   public static float noise1(float x) {
/* 106 */     if (start) {
/*     */       
/* 108 */       start = false;
/* 109 */       init();
/*     */     } 
/*     */     
/* 112 */     float t = x + 4096.0F;
/* 113 */     int bx0 = (int)t & 0xFF;
/* 114 */     int bx1 = bx0 + 1 & 0xFF;
/* 115 */     float rx0 = t - (int)t;
/* 116 */     float rx1 = rx0 - 1.0F;
/* 117 */     float sx = sCurve(rx0);
/* 118 */     float u = rx0 * g1[p[bx0]];
/* 119 */     float v = rx1 * g1[p[bx1]];
/* 120 */     return 2.3F * lerp(sx, u, v);
/*     */   }
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
/*     */   public static float noise2(float x, float y) {
/* 135 */     if (start) {
/*     */       
/* 137 */       start = false;
/* 138 */       init();
/*     */     } 
/*     */     
/* 141 */     float t = x + 4096.0F;
/* 142 */     int bx0 = (int)t & 0xFF;
/* 143 */     int bx1 = bx0 + 1 & 0xFF;
/* 144 */     float rx0 = t - (int)t;
/* 145 */     float rx1 = rx0 - 1.0F;
/* 146 */     t = y + 4096.0F;
/* 147 */     int by0 = (int)t & 0xFF;
/* 148 */     int by1 = by0 + 1 & 0xFF;
/* 149 */     float ry0 = t - (int)t;
/* 150 */     float ry1 = ry0 - 1.0F;
/* 151 */     int i = p[bx0];
/* 152 */     int j = p[bx1];
/* 153 */     int b00 = p[i + by0];
/* 154 */     int b10 = p[j + by0];
/* 155 */     int b01 = p[i + by1];
/* 156 */     int b11 = p[j + by1];
/* 157 */     float sx = sCurve(rx0);
/* 158 */     float sy = sCurve(ry0);
/* 159 */     float[] q = g2[b00];
/* 160 */     float u = rx0 * q[0] + ry0 * q[1];
/* 161 */     q = g2[b10];
/* 162 */     float v = rx1 * q[0] + ry0 * q[1];
/* 163 */     float a = lerp(sx, u, v);
/* 164 */     q = g2[b01];
/* 165 */     u = rx0 * q[0] + ry1 * q[1];
/* 166 */     q = g2[b11];
/* 167 */     v = rx1 * q[0] + ry1 * q[1];
/* 168 */     float b = lerp(sx, u, v);
/* 169 */     return 1.5F * lerp(sy, a, b);
/*     */   }
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
/*     */   public static float noise3(float x, float y, float z) {
/* 185 */     if (start) {
/*     */       
/* 187 */       start = false;
/* 188 */       init();
/*     */     } 
/*     */     
/* 191 */     float t = x + 4096.0F;
/* 192 */     int bx0 = (int)t & 0xFF;
/* 193 */     int bx1 = bx0 + 1 & 0xFF;
/* 194 */     float rx0 = t - (int)t;
/* 195 */     float rx1 = rx0 - 1.0F;
/* 196 */     t = y + 4096.0F;
/* 197 */     int by0 = (int)t & 0xFF;
/* 198 */     int by1 = by0 + 1 & 0xFF;
/* 199 */     float ry0 = t - (int)t;
/* 200 */     float ry1 = ry0 - 1.0F;
/* 201 */     t = z + 4096.0F;
/* 202 */     int bz0 = (int)t & 0xFF;
/* 203 */     int bz1 = bz0 + 1 & 0xFF;
/* 204 */     float rz0 = t - (int)t;
/* 205 */     float rz1 = rz0 - 1.0F;
/* 206 */     int i = p[bx0];
/* 207 */     int j = p[bx1];
/* 208 */     int b00 = p[i + by0];
/* 209 */     int b10 = p[j + by0];
/* 210 */     int b01 = p[i + by1];
/* 211 */     int b11 = p[j + by1];
/* 212 */     t = sCurve(rx0);
/* 213 */     float sy = sCurve(ry0);
/* 214 */     float sz = sCurve(rz0);
/* 215 */     float[] q = g3[b00 + bz0];
/* 216 */     float u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2];
/* 217 */     q = g3[b10 + bz0];
/* 218 */     float v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2];
/* 219 */     float a = lerp(t, u, v);
/* 220 */     q = g3[b01 + bz0];
/* 221 */     u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2];
/* 222 */     q = g3[b11 + bz0];
/* 223 */     v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2];
/* 224 */     float b = lerp(t, u, v);
/* 225 */     float c = lerp(sy, a, b);
/* 226 */     q = g3[b00 + bz1];
/* 227 */     u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2];
/* 228 */     q = g3[b10 + bz1];
/* 229 */     v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2];
/* 230 */     a = lerp(t, u, v);
/* 231 */     q = g3[b01 + bz1];
/* 232 */     u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2];
/* 233 */     q = g3[b11 + bz1];
/* 234 */     v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2];
/* 235 */     b = lerp(t, u, v);
/* 236 */     float d = lerp(sy, a, b);
/* 237 */     return 1.5F * lerp(sz, c, d);
/*     */   }
/*     */ 
/*     */   
/*     */   public static float lerp(float t, float a, float b) {
/* 242 */     return a + t * (b - a);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void normalize2(float[] v) {
/* 247 */     float s = (float)Math.sqrt((v[0] * v[0] + v[1] * v[1]));
/* 248 */     v[0] = v[0] / s;
/* 249 */     v[1] = v[1] / s;
/*     */   }
/*     */ 
/*     */   
/*     */   static void normalize3(float[] v) {
/* 254 */     float s = (float)Math.sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
/* 255 */     v[0] = v[0] / s;
/* 256 */     v[1] = v[1] / s;
/* 257 */     v[2] = v[2] / s;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int random() {
/* 262 */     return randomGenerator.nextInt() & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void init() {
/*     */     int i;
/* 269 */     for (i = 0; i < 256; i++) {
/*     */       
/* 271 */       p[i] = i;
/* 272 */       g1[i] = (random() % 512 - 256) / 256.0F;
/*     */       int j;
/* 274 */       for (j = 0; j < 2; j++)
/*     */       {
/* 276 */         g2[i][j] = (random() % 512 - 256) / 256.0F;
/*     */       }
/*     */       
/* 279 */       normalize2(g2[i]);
/*     */       
/* 281 */       for (j = 0; j < 3; j++)
/*     */       {
/* 283 */         g3[i][j] = (random() % 512 - 256) / 256.0F;
/*     */       }
/*     */       
/* 286 */       normalize3(g3[i]);
/*     */     } 
/*     */     
/* 289 */     for (i = 255; i >= 0; i--) {
/*     */       
/* 291 */       int k = p[i]; int j;
/* 292 */       p[i] = p[j = random() % 256];
/* 293 */       p[j] = k;
/*     */     } 
/*     */     
/* 296 */     for (i = 0; i < 258; i++) {
/*     */       
/* 298 */       p[256 + i] = p[i];
/* 299 */       g1[256 + i] = g1[i];
/*     */       int j;
/* 301 */       for (j = 0; j < 2; j++)
/*     */       {
/* 303 */         g2[256 + i][j] = g2[i][j];
/*     */       }
/*     */       
/* 306 */       for (j = 0; j < 3; j++)
/*     */       {
/* 308 */         g3[256 + i][j] = g3[i][j];
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float[] findRange(Function1D f, float[] minmax) {
/* 320 */     if (minmax == null)
/*     */     {
/* 322 */       minmax = new float[2];
/*     */     }
/*     */     
/* 325 */     float min = 0.0F, max = 0.0F;
/*     */     
/*     */     float x;
/* 328 */     for (x = -100.0F; x < 100.0F; x = (float)(x + 1.27139D)) {
/*     */       
/* 330 */       float n = f.evaluate(x);
/* 331 */       min = Math.min(min, n);
/* 332 */       max = Math.max(max, n);
/*     */     } 
/*     */     
/* 335 */     minmax[0] = min;
/* 336 */     minmax[1] = max;
/* 337 */     return minmax;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float[] findRange(Function2D f, float[] minmax) {
/* 347 */     if (minmax == null)
/*     */     {
/* 349 */       minmax = new float[2];
/*     */     }
/*     */     
/* 352 */     float min = 0.0F, max = 0.0F;
/*     */     
/*     */     float y;
/* 355 */     for (y = -100.0F; y < 100.0F; y = (float)(y + 10.35173D)) {
/*     */       float x;
/* 357 */       for (x = -100.0F; x < 100.0F; x = (float)(x + 10.77139D)) {
/*     */         
/* 359 */         float n = f.evaluate(x, y);
/* 360 */         min = Math.min(min, n);
/* 361 */         max = Math.max(max, n);
/*     */       } 
/*     */     } 
/*     */     
/* 365 */     minmax[0] = min;
/* 366 */     minmax[1] = max;
/* 367 */     return minmax;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\Noise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */