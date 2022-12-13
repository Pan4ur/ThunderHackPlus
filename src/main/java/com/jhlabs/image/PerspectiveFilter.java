/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
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
/*     */ public class PerspectiveFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   private float x0;
/*     */   private float y0;
/*     */   private float x1;
/*     */   private float y1;
/*     */   private float x2;
/*     */   private float y2;
/*     */   private float x3;
/*     */   private float y3;
/*     */   private float dx1;
/*     */   private float dy1;
/*     */   private float dx2;
/*     */   private float dy2;
/*     */   private float dx3;
/*     */   private float dy3;
/*     */   private float A;
/*     */   private float B;
/*     */   private float C;
/*     */   
/*     */   public PerspectiveFilter() {
/*  42 */     this(0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F);
/*     */   }
/*     */   private float D; private float E; private float F; private float G; private float H; private float I;
/*     */   private float a11;
/*     */   private float a12;
/*     */   private float a13;
/*     */   private float a21;
/*     */   private float a22;
/*     */   private float a23;
/*     */   private float a31;
/*     */   private float a32;
/*     */   private float a33;
/*     */   private boolean scaled;
/*     */   private boolean clip = false;
/*     */   
/*     */   public PerspectiveFilter(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/*  58 */     unitSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClip(boolean clip) {
/*  63 */     this.clip = clip;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getClip() {
/*  68 */     return this.clip;
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
/*     */   public void setCorners(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/*  86 */     unitSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3);
/*  87 */     this.scaled = true;
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
/*     */   public void unitSquareToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/* 104 */     this.x0 = x0;
/* 105 */     this.y0 = y0;
/* 106 */     this.x1 = x1;
/* 107 */     this.y1 = y1;
/* 108 */     this.x2 = x2;
/* 109 */     this.y2 = y2;
/* 110 */     this.x3 = x3;
/* 111 */     this.y3 = y3;
/* 112 */     this.dx1 = x1 - x2;
/* 113 */     this.dy1 = y1 - y2;
/* 114 */     this.dx2 = x3 - x2;
/* 115 */     this.dy2 = y3 - y2;
/* 116 */     this.dx3 = x0 - x1 + x2 - x3;
/* 117 */     this.dy3 = y0 - y1 + y2 - y3;
/*     */     
/* 119 */     if (this.dx3 == 0.0F && this.dy3 == 0.0F) {
/*     */       
/* 121 */       this.a11 = x1 - x0;
/* 122 */       this.a21 = x2 - x1;
/* 123 */       this.a31 = x0;
/* 124 */       this.a12 = y1 - y0;
/* 125 */       this.a22 = y2 - y1;
/* 126 */       this.a32 = y0;
/* 127 */       this.a13 = this.a23 = 0.0F;
/*     */     }
/*     */     else {
/*     */       
/* 131 */       this.a13 = (this.dx3 * this.dy2 - this.dx2 * this.dy3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
/* 132 */       this.a23 = (this.dx1 * this.dy3 - this.dy1 * this.dx3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
/* 133 */       this.a11 = x1 - x0 + this.a13 * x1;
/* 134 */       this.a21 = x3 - x0 + this.a23 * x3;
/* 135 */       this.a31 = x0;
/* 136 */       this.a12 = y1 - y0 + this.a13 * y1;
/* 137 */       this.a22 = y3 - y0 + this.a23 * y3;
/* 138 */       this.a32 = y0;
/*     */     } 
/*     */     
/* 141 */     this.a33 = 1.0F;
/* 142 */     this.scaled = false;
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
/*     */   public void quadToUnitSquare(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/* 159 */     unitSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3);
/*     */     
/* 161 */     float ta11 = this.a22 * this.a33 - this.a32 * this.a23;
/* 162 */     float ta21 = this.a32 * this.a13 - this.a12 * this.a33;
/* 163 */     float ta31 = this.a12 * this.a23 - this.a22 * this.a13;
/* 164 */     float ta12 = this.a31 * this.a23 - this.a21 * this.a33;
/* 165 */     float ta22 = this.a11 * this.a33 - this.a31 * this.a13;
/* 166 */     float ta32 = this.a21 * this.a13 - this.a11 * this.a23;
/* 167 */     float ta13 = this.a21 * this.a32 - this.a31 * this.a22;
/* 168 */     float ta23 = this.a31 * this.a12 - this.a11 * this.a32;
/* 169 */     float ta33 = this.a11 * this.a22 - this.a21 * this.a12;
/* 170 */     float f = 1.0F / ta33;
/* 171 */     this.a11 = ta11 * f;
/* 172 */     this.a21 = ta12 * f;
/* 173 */     this.a31 = ta13 * f;
/* 174 */     this.a12 = ta21 * f;
/* 175 */     this.a22 = ta22 * f;
/* 176 */     this.a32 = ta23 * f;
/* 177 */     this.a13 = ta31 * f;
/* 178 */     this.a23 = ta32 * f;
/* 179 */     this.a33 = 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 184 */     this.A = this.a22 * this.a33 - this.a32 * this.a23;
/* 185 */     this.B = this.a31 * this.a23 - this.a21 * this.a33;
/* 186 */     this.C = this.a21 * this.a32 - this.a31 * this.a22;
/* 187 */     this.D = this.a32 * this.a13 - this.a12 * this.a33;
/* 188 */     this.E = this.a11 * this.a33 - this.a31 * this.a13;
/* 189 */     this.F = this.a31 * this.a12 - this.a11 * this.a32;
/* 190 */     this.G = this.a12 * this.a23 - this.a22 * this.a13;
/* 191 */     this.H = this.a21 * this.a13 - this.a11 * this.a23;
/* 192 */     this.I = this.a11 * this.a22 - this.a21 * this.a12;
/*     */     
/* 194 */     if (!this.scaled) {
/*     */       
/* 196 */       int width = src.getWidth();
/* 197 */       int height = src.getHeight();
/* 198 */       float invWidth = 1.0F / width;
/* 199 */       float invHeight = 1.0F / height;
/* 200 */       this.A *= invWidth;
/* 201 */       this.D *= invWidth;
/* 202 */       this.G *= invWidth;
/* 203 */       this.B *= invHeight;
/* 204 */       this.E *= invHeight;
/* 205 */       this.H *= invHeight;
/*     */     } 
/*     */     
/* 208 */     return super.filter(src, dst);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle rect) {
/* 213 */     if (this.scaled) {
/*     */       
/* 215 */       rect.x = (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
/* 216 */       rect.y = (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
/* 217 */       rect.width = (int)Math.max(Math.max(this.x0, this.x1), Math.max(this.x2, this.x3)) - rect.x;
/* 218 */       rect.height = (int)Math.max(Math.max(this.y0, this.y1), Math.max(this.y2, this.y3)) - rect.y;
/*     */       
/*     */       return;
/*     */     } 
/* 222 */     if (!this.clip) {
/*     */       
/* 224 */       float w = (float)rect.getWidth(), h = (float)rect.getHeight();
/* 225 */       Rectangle r = new Rectangle();
/* 226 */       r.add(getPoint2D(new Point2D.Float(0.0F, 0.0F), null));
/* 227 */       r.add(getPoint2D(new Point2D.Float(w, 0.0F), null));
/* 228 */       r.add(getPoint2D(new Point2D.Float(0.0F, h), null));
/* 229 */       r.add(getPoint2D(new Point2D.Float(w, h), null));
/* 230 */       rect.setRect(r);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOriginX() {
/* 240 */     return this.x0 - (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOriginY() {
/* 249 */     return this.y0 - (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
/*     */   }
/*     */ 
/*     */   
/*     */   public Rectangle2D getBounds2D(BufferedImage src) {
/* 254 */     if (this.clip)
/*     */     {
/* 256 */       return new Rectangle(0, 0, src.getWidth(), src.getHeight());
/*     */     }
/*     */     
/* 259 */     float w = src.getWidth(), h = src.getHeight();
/* 260 */     Rectangle2D r = new Rectangle2D.Float();
/* 261 */     r.add(getPoint2D(new Point2D.Float(0.0F, 0.0F), null));
/* 262 */     r.add(getPoint2D(new Point2D.Float(w, 0.0F), null));
/* 263 */     r.add(getPoint2D(new Point2D.Float(0.0F, h), null));
/* 264 */     r.add(getPoint2D(new Point2D.Float(w, h), null));
/* 265 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
/* 270 */     if (dstPt == null)
/*     */     {
/* 272 */       dstPt = new Point2D.Float();
/*     */     }
/*     */     
/* 275 */     float x = (float)srcPt.getX();
/* 276 */     float y = (float)srcPt.getY();
/* 277 */     float f = 1.0F / (x * this.a13 + y * this.a23 + this.a33);
/* 278 */     dstPt.setLocation(((x * this.a11 + y * this.a21 + this.a31) * f), ((x * this.a12 + y * this.a22 + this.a32) * f));
/* 279 */     return dstPt;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 284 */     out[0] = this.originalSpace.width * (this.A * x + this.B * y + this.C) / (this.G * x + this.H * y + this.I);
/* 285 */     out[1] = this.originalSpace.height * (this.D * x + this.E * y + this.F) / (this.G * x + this.H * y + this.I);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 290 */     return "Distort/Perspective...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\PerspectiveFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */