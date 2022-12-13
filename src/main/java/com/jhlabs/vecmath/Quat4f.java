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
/*     */ 
/*     */ public class Quat4f
/*     */   extends Tuple4f
/*     */ {
/*     */   public Quat4f() {
/*  26 */     this(0.0F, 0.0F, 0.0F, 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public Quat4f(float[] x) {
/*  31 */     this.x = x[0];
/*  32 */     this.y = x[1];
/*  33 */     this.z = x[2];
/*  34 */     this.w = x[3];
/*     */   }
/*     */ 
/*     */   
/*     */   public Quat4f(float x, float y, float z, float w) {
/*  39 */     this.x = x;
/*  40 */     this.y = y;
/*  41 */     this.z = z;
/*  42 */     this.w = w;
/*     */   }
/*     */ 
/*     */   
/*     */   public Quat4f(Quat4f t) {
/*  47 */     this.x = t.x;
/*  48 */     this.y = t.y;
/*  49 */     this.z = t.z;
/*  50 */     this.w = t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public Quat4f(Tuple4f t) {
/*  55 */     this.x = t.x;
/*  56 */     this.y = t.y;
/*  57 */     this.z = t.z;
/*  58 */     this.w = t.w;
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(AxisAngle4f a) {
/*  63 */     float halfTheta = a.angle * 0.5F;
/*  64 */     float cosHalfTheta = (float)Math.cos(halfTheta);
/*  65 */     float sinHalfTheta = (float)Math.sin(halfTheta);
/*  66 */     this.x = a.x * sinHalfTheta;
/*  67 */     this.y = a.y * sinHalfTheta;
/*  68 */     this.z = a.z * sinHalfTheta;
/*  69 */     this.w = cosHalfTheta;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void normalize() {
/*  93 */     float d = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
/*  94 */     this.x *= d;
/*  95 */     this.y *= d;
/*  96 */     this.z *= d;
/*  97 */     this.w *= d;
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
/*     */   public void set(Matrix4f m) {
/* 128 */     float tr = m.m00 + m.m11 + m.m22;
/*     */     
/* 130 */     if (tr > 0.0D) {
/*     */       
/* 132 */       float s = (float)Math.sqrt((tr + 1.0F));
/* 133 */       this.w = s / 2.0F;
/* 134 */       s = 0.5F / s;
/* 135 */       this.x = (m.m12 - m.m21) * s;
/* 136 */       this.y = (m.m20 - m.m02) * s;
/* 137 */       this.z = (m.m01 - m.m10) * s;
/*     */     } else {
/*     */       float s;
/*     */       
/* 141 */       int i = 0;
/*     */       
/* 143 */       if (m.m11 > m.m00) {
/*     */         
/* 145 */         i = 1;
/*     */         
/* 147 */         if (m.m22 > m.m11)
/*     */         {
/* 149 */           i = 2;
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/* 157 */       else if (m.m22 > m.m00) {
/*     */         
/* 159 */         i = 2;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 166 */       switch (i) {
/*     */         
/*     */         case 0:
/* 169 */           s = (float)Math.sqrt((m.m00 - m.m11 + m.m22 + 1.0F));
/* 170 */           this.x = s * 0.5F;
/*     */           
/* 172 */           if (s != 0.0D)
/*     */           {
/* 174 */             s = 0.5F / s;
/*     */           }
/*     */           
/* 177 */           this.w = (m.m12 - m.m21) * s;
/* 178 */           this.y = (m.m01 + m.m10) * s;
/* 179 */           this.z = (m.m02 + m.m20) * s;
/*     */           break;
/*     */         
/*     */         case 1:
/* 183 */           s = (float)Math.sqrt((m.m11 - m.m22 + m.m00 + 1.0F));
/* 184 */           this.y = s * 0.5F;
/*     */           
/* 186 */           if (s != 0.0D)
/*     */           {
/* 188 */             s = 0.5F / s;
/*     */           }
/*     */           
/* 191 */           this.w = (m.m20 - m.m02) * s;
/* 192 */           this.z = (m.m12 + m.m21) * s;
/* 193 */           this.x = (m.m10 + m.m01) * s;
/*     */           break;
/*     */         
/*     */         case 2:
/* 197 */           s = (float)Math.sqrt((m.m00 - m.m11 + m.m22 + 1.0F));
/* 198 */           this.z = s * 0.5F;
/*     */           
/* 200 */           if (s != 0.0D)
/*     */           {
/* 202 */             s = 0.5F / s;
/*     */           }
/*     */           
/* 205 */           this.w = (m.m01 - m.m10) * s;
/* 206 */           this.x = (m.m20 + m.m02) * s;
/* 207 */           this.y = (m.m21 + m.m12) * s;
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\vecmath\Quat4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */