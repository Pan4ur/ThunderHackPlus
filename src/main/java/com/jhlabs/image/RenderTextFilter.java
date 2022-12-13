/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Paint;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ public class RenderTextFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*     */   private String text;
/*     */   private Font font;
/*     */   private Paint paint;
/*     */   private Composite composite;
/*     */   private AffineTransform transform;
/*     */   
/*     */   public RenderTextFilter() {}
/*     */   
/*     */   public RenderTextFilter(String text, Font font, Paint paint, Composite composite, AffineTransform transform) {
/*  51 */     this.text = text;
/*  52 */     this.font = font;
/*  53 */     this.composite = composite;
/*  54 */     this.paint = paint;
/*  55 */     this.transform = transform;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setText(String text) {
/*  65 */     this.text = text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/*  75 */     return this.text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setComposite(Composite composite) {
/*  85 */     this.composite = composite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Composite getComposite() {
/*  95 */     return this.composite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPaint(Paint paint) {
/* 105 */     this.paint = paint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Paint getPaint() {
/* 115 */     return this.paint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFont(Font font) {
/* 125 */     this.font = font;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Font getFont() {
/* 135 */     return this.font;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTransform(AffineTransform transform) {
/* 145 */     this.transform = transform;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AffineTransform getTransform() {
/* 155 */     return this.transform;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 160 */     if (dst == null)
/*     */     {
/* 162 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 165 */     Graphics2D g = dst.createGraphics();
/* 166 */     g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*     */     
/* 168 */     if (this.font != null)
/*     */     {
/* 170 */       g.setFont(this.font);
/*     */     }
/*     */     
/* 173 */     if (this.transform != null)
/*     */     {
/* 175 */       g.setTransform(this.transform);
/*     */     }
/*     */     
/* 178 */     if (this.composite != null)
/*     */     {
/* 180 */       g.setComposite(this.composite);
/*     */     }
/*     */     
/* 183 */     if (this.paint != null)
/*     */     {
/* 185 */       g.setPaint(this.paint);
/*     */     }
/*     */     
/* 188 */     if (this.text != null)
/*     */     {
/* 190 */       g.drawString(this.text, 10, 100);
/*     */     }
/*     */     
/* 193 */     g.dispose();
/* 194 */     return dst;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\RenderTextFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */