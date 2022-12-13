/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
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
/*     */ public class RotateFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   private float angle;
/*     */   private float cos;
/*     */   private float sin;
/*     */   private boolean resize = true;
/*     */   
/*     */   public RotateFilter() {
/*  36 */     this(3.1415927F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RotateFilter(float angle) {
/*  45 */     this(angle, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RotateFilter(float angle, boolean resize) {
/*  55 */     setAngle(angle);
/*  56 */     this.resize = resize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAngle(float angle) {
/*  67 */     this.angle = angle;
/*  68 */     this.cos = (float)Math.cos(this.angle);
/*  69 */     this.sin = (float)Math.sin(this.angle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAngle() {
/*  79 */     return this.angle;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformSpace(Rectangle rect) {
/*  84 */     if (this.resize) {
/*     */       
/*  86 */       Point out = new Point(0, 0);
/*  87 */       int minx = Integer.MAX_VALUE;
/*  88 */       int miny = Integer.MAX_VALUE;
/*  89 */       int maxx = Integer.MIN_VALUE;
/*  90 */       int maxy = Integer.MIN_VALUE;
/*  91 */       int w = rect.width;
/*  92 */       int h = rect.height;
/*  93 */       int x = rect.x;
/*  94 */       int y = rect.y;
/*     */       
/*  96 */       for (int i = 0; i < 4; i++) {
/*     */         
/*  98 */         switch (i) {
/*     */           
/*     */           case 0:
/* 101 */             transform(x, y, out);
/*     */             break;
/*     */           
/*     */           case 1:
/* 105 */             transform(x + w, y, out);
/*     */             break;
/*     */           
/*     */           case 2:
/* 109 */             transform(x, y + h, out);
/*     */             break;
/*     */           
/*     */           case 3:
/* 113 */             transform(x + w, y + h, out);
/*     */             break;
/*     */         } 
/*     */         
/* 117 */         minx = Math.min(minx, out.x);
/* 118 */         miny = Math.min(miny, out.y);
/* 119 */         maxx = Math.max(maxx, out.x);
/* 120 */         maxy = Math.max(maxy, out.y);
/*     */       } 
/*     */       
/* 123 */       rect.x = minx;
/* 124 */       rect.y = miny;
/* 125 */       rect.width = maxx - rect.x;
/* 126 */       rect.height = maxy - rect.y;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void transform(int x, int y, Point out) {
/* 132 */     out.x = (int)(x * this.cos + y * this.sin);
/* 133 */     out.y = (int)(y * this.cos - x * this.sin);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 138 */     out[0] = x * this.cos - y * this.sin;
/* 139 */     out[1] = y * this.cos + x * this.sin;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 144 */     return "Rotate " + (int)((this.angle * 180.0F) / Math.PI);
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RotateFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */