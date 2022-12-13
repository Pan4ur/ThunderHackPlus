/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PolarFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   public static final int RECT_TO_POLAR = 0;
/*     */   public static final int POLAR_TO_RECT = 1;
/*     */   public static final int INVERT_IN_CIRCLE = 2;
/*     */   private int type;
/*     */   private float width;
/*     */   private float height;
/*     */   private float centreX;
/*     */   private float centreY;
/*     */   private float radius;
/*     */   
/*     */   public PolarFilter() {
/*  52 */     this(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolarFilter(int type) {
/*  61 */     this.type = type;
/*  62 */     setEdgeAction(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/*  67 */     this.width = src.getWidth();
/*  68 */     this.height = src.getHeight();
/*  69 */     this.centreX = this.width / 2.0F;
/*  70 */     this.centreY = this.height / 2.0F;
/*  71 */     this.radius = Math.max(this.centreY, this.centreX);
/*  72 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(int type) {
/*  82 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/*  92 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   private float sqr(float x) {
/*  97 */     return x * x;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 104 */     float theta, t, m, theta2, nx, ny, dx, dy, distance2, r = 0.0F;
/*     */     
/* 106 */     switch (this.type) {
/*     */       
/*     */       case 0:
/* 109 */         theta = 0.0F;
/*     */         
/* 111 */         if (x >= this.centreX) {
/*     */           
/* 113 */           if (y > this.centreY)
/*     */           {
/* 115 */             theta = 3.1415927F - (float)Math.atan(((x - this.centreX) / (y - this.centreY)));
/* 116 */             r = (float)Math.sqrt((sqr(x - this.centreX) + sqr(y - this.centreY)));
/*     */           }
/* 118 */           else if (y < this.centreY)
/*     */           {
/* 120 */             theta = (float)Math.atan(((x - this.centreX) / (this.centreY - y)));
/* 121 */             r = (float)Math.sqrt((sqr(x - this.centreX) + sqr(this.centreY - y)));
/*     */           }
/*     */           else
/*     */           {
/* 125 */             theta = 1.5707964F;
/* 126 */             r = x - this.centreX;
/*     */           }
/*     */         
/* 129 */         } else if (x < this.centreX) {
/*     */           
/* 131 */           if (y < this.centreY) {
/*     */             
/* 133 */             theta = 6.2831855F - (float)Math.atan(((this.centreX - x) / (this.centreY - y)));
/* 134 */             r = (float)Math.sqrt((sqr(this.centreX - x) + sqr(this.centreY - y)));
/*     */           }
/* 136 */           else if (y > this.centreY) {
/*     */             
/* 138 */             theta = 3.1415927F + (float)Math.atan(((this.centreX - x) / (y - this.centreY)));
/* 139 */             r = (float)Math.sqrt((sqr(this.centreX - x) + sqr(y - this.centreY)));
/*     */           }
/*     */           else {
/*     */             
/* 143 */             theta = 4.712389F;
/* 144 */             r = this.centreX - x;
/*     */           } 
/*     */         } 
/*     */         
/* 148 */         if (x != this.centreX) {
/*     */           
/* 150 */           m = Math.abs((y - this.centreY) / (x - this.centreX));
/*     */         }
/*     */         else {
/*     */           
/* 154 */           m = 0.0F;
/*     */         } 
/*     */         
/* 157 */         if (m <= this.height / this.width) {
/*     */           
/* 159 */           if (x == this.centreX)
/*     */           {
/* 161 */             float xmax = 0.0F;
/* 162 */             float ymax = this.centreY;
/*     */           }
/*     */           else
/*     */           {
/* 166 */             float xmax = this.centreX;
/* 167 */             float ymax = m * xmax;
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 172 */           float ymax = this.centreY;
/* 173 */           float xmax = ymax / m;
/*     */         } 
/*     */         
/* 176 */         out[0] = this.width - 1.0F - (this.width - 1.0F) / 6.2831855F * theta;
/* 177 */         out[1] = this.height * r / this.radius;
/*     */         break;
/*     */       
/*     */       case 1:
/* 181 */         theta = x / this.width * 6.2831855F;
/*     */ 
/*     */         
/* 184 */         if (theta >= 4.712389F) {
/*     */           
/* 186 */           theta2 = 6.2831855F - theta;
/*     */         }
/* 188 */         else if (theta >= 3.1415927F) {
/*     */           
/* 190 */           theta2 = theta - 3.1415927F;
/*     */         }
/* 192 */         else if (theta >= 1.5707964F) {
/*     */           
/* 194 */           theta2 = 3.1415927F - theta;
/*     */         }
/*     */         else {
/*     */           
/* 198 */           theta2 = theta;
/*     */         } 
/*     */         
/* 201 */         t = (float)Math.tan(theta2);
/*     */         
/* 203 */         if (t != 0.0F) {
/*     */           
/* 205 */           m = 1.0F / t;
/*     */         }
/*     */         else {
/*     */           
/* 209 */           m = 0.0F;
/*     */         } 
/*     */         
/* 212 */         if (m <= this.height / this.width) {
/*     */           
/* 214 */           if (theta2 == 0.0F)
/*     */           {
/* 216 */             float xmax = 0.0F;
/* 217 */             float ymax = this.centreY;
/*     */           }
/*     */           else
/*     */           {
/* 221 */             float xmax = this.centreX;
/* 222 */             float ymax = m * xmax;
/*     */           }
/*     */         
/*     */         } else {
/*     */           
/* 227 */           float ymax = this.centreY;
/* 228 */           float xmax = ymax / m;
/*     */         } 
/*     */         
/* 231 */         r = this.radius * y / this.height;
/* 232 */         nx = -r * (float)Math.sin(theta2);
/* 233 */         ny = r * (float)Math.cos(theta2);
/*     */         
/* 235 */         if (theta >= 4.712389F) {
/*     */           
/* 237 */           out[0] = this.centreX - nx;
/* 238 */           out[1] = this.centreY - ny; break;
/*     */         } 
/* 240 */         if (theta >= Math.PI) {
/*     */           
/* 242 */           out[0] = this.centreX - nx;
/* 243 */           out[1] = this.centreY + ny; break;
/*     */         } 
/* 245 */         if (theta >= 1.5707963267948966D) {
/*     */           
/* 247 */           out[0] = this.centreX + nx;
/* 248 */           out[1] = this.centreY + ny;
/*     */           
/*     */           break;
/*     */         } 
/* 252 */         out[0] = this.centreX + nx;
/* 253 */         out[1] = this.centreY - ny;
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 2:
/* 259 */         dx = x - this.centreX;
/* 260 */         dy = y - this.centreY;
/* 261 */         distance2 = dx * dx + dy * dy;
/* 262 */         out[0] = this.centreX + this.centreX * this.centreX * dx / distance2;
/* 263 */         out[1] = this.centreY + this.centreY * this.centreY * dy / distance2;
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 270 */     return "Distort/Polar Coordinates...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PolarFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */