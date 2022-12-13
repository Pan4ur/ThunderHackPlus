/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Point;
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
/*     */ public class FieldWarpFilter
/*     */   extends TransformFilter
/*     */ {
/*     */   public static class Line
/*     */   {
/*     */     public int x1;
/*     */     public int y1;
/*     */     public int x2;
/*     */     public int y2;
/*     */     public int dx;
/*     */     public int dy;
/*     */     public float length;
/*     */     public float lengthSquared;
/*     */     
/*     */     public Line(int x1, int y1, int x2, int y2) {
/*  35 */       this.x1 = x1;
/*  36 */       this.y1 = y1;
/*  37 */       this.x2 = x2;
/*  38 */       this.y2 = y2;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setup() {
/*  43 */       this.dx = this.x2 - this.x1;
/*  44 */       this.dy = this.y2 - this.y1;
/*  45 */       this.lengthSquared = (this.dx * this.dx + this.dy * this.dy);
/*  46 */       this.length = (float)Math.sqrt(this.lengthSquared);
/*     */     }
/*     */   }
/*     */   
/*  50 */   private float amount = 1.0F;
/*  51 */   private float power = 1.0F;
/*  52 */   private float strength = 2.0F;
/*     */ 
/*     */   
/*     */   private Line[] inLines;
/*     */ 
/*     */   
/*     */   private Line[] outLines;
/*     */ 
/*     */   
/*     */   private Line[] intermediateLines;
/*     */ 
/*     */   
/*     */   private float width;
/*     */ 
/*     */   
/*     */   private float height;
/*     */ 
/*     */   
/*     */   public void setAmount(float amount) {
/*  71 */     this.amount = amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAmount() {
/*  81 */     return this.amount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPower(float power) {
/*  86 */     this.power = power;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getPower() {
/*  91 */     return this.power;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStrength(float strength) {
/*  96 */     this.strength = strength;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStrength() {
/* 101 */     return this.strength;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInLines(Line[] inLines) {
/* 106 */     this.inLines = inLines;
/*     */   }
/*     */ 
/*     */   
/*     */   public Line[] getInLines() {
/* 111 */     return this.inLines;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOutLines(Line[] outLines) {
/* 116 */     this.outLines = outLines;
/*     */   }
/*     */ 
/*     */   
/*     */   public Line[] getOutLines() {
/* 121 */     return this.outLines;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void transform(int x, int y, Point out) {}
/*     */ 
/*     */   
/*     */   protected void transformInverse(int x, int y, float[] out) {
/* 130 */     float u = 0.0F, v = 0.0F;
/* 131 */     float fraction = 0.0F;
/*     */ 
/*     */ 
/*     */     
/* 135 */     float a = 0.001F;
/* 136 */     float b = 1.5F * this.strength + 0.5F;
/* 137 */     float p = this.power;
/* 138 */     float totalWeight = 0.0F;
/* 139 */     float sumX = 0.0F;
/* 140 */     float sumY = 0.0F;
/*     */     
/* 142 */     for (int line = 0; line < this.inLines.length; line++) {
/*     */       float distance;
/* 144 */       Line l1 = this.inLines[line];
/* 145 */       Line l = this.intermediateLines[line];
/* 146 */       float dx = (x - l.x1);
/* 147 */       float dy = (y - l.y1);
/* 148 */       fraction = (dx * l.dx + dy * l.dy) / l.lengthSquared;
/* 149 */       float fdist = (dy * l.dx - dx * l.dy) / l.length;
/*     */       
/* 151 */       if (fraction <= 0.0F) {
/*     */         
/* 153 */         distance = (float)Math.sqrt((dx * dx + dy * dy));
/*     */       }
/* 155 */       else if (fraction >= 1.0F) {
/*     */         
/* 157 */         dx = (x - l.x2);
/* 158 */         dy = (y - l.y2);
/* 159 */         distance = (float)Math.sqrt((dx * dx + dy * dy));
/*     */       }
/* 161 */       else if (fdist >= 0.0F) {
/*     */         
/* 163 */         distance = fdist;
/*     */       }
/*     */       else {
/*     */         
/* 167 */         distance = -fdist;
/*     */       } 
/*     */       
/* 170 */       u = l1.x1 + fraction * l1.dx - fdist * l1.dy / l1.length;
/* 171 */       v = l1.y1 + fraction * l1.dy + fdist * l1.dx / l1.length;
/* 172 */       float weight = (float)Math.pow(Math.pow(l.length, p) / (a + distance), b);
/* 173 */       sumX += (u - x) * weight;
/* 174 */       sumY += (v - y) * weight;
/*     */       
/* 176 */       totalWeight += weight;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 181 */     out[0] = x + sumX / totalWeight + 0.5F;
/* 182 */     out[1] = y + sumY / totalWeight + 0.5F;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 187 */     this.width = this.width;
/* 188 */     this.height = this.height;
/*     */     
/* 190 */     if (this.inLines != null && this.outLines != null) {
/*     */       
/* 192 */       this.intermediateLines = new Line[this.inLines.length];
/*     */       
/* 194 */       for (int line = 0; line < this.inLines.length; line++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 200 */         Line l = this.intermediateLines[line] = new Line(ImageMath.lerp(this.amount, (this.inLines[line]).x1, (this.outLines[line]).x1), ImageMath.lerp(this.amount, (this.inLines[line]).y1, (this.outLines[line]).y1), ImageMath.lerp(this.amount, (this.inLines[line]).x2, (this.outLines[line]).x2), ImageMath.lerp(this.amount, (this.inLines[line]).y2, (this.outLines[line]).y2));
/*     */         
/* 202 */         l.setup();
/* 203 */         this.inLines[line].setup();
/*     */       } 
/*     */       
/* 206 */       dst = super.filter(src, dst);
/* 207 */       this.intermediateLines = null;
/* 208 */       return dst;
/*     */     } 
/*     */     
/* 211 */     return src;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 216 */     return "Distort/Field Warp...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\FieldWarpFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */