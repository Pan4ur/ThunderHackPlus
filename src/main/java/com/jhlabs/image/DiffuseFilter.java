/*    */ package com.jhlabs.image;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiffuseFilter
/*    */   extends TransformFilter
/*    */ {
/*    */   private float[] sinTable;
/*    */   private float[] cosTable;
/* 29 */   private float scale = 4.0F;
/*    */ 
/*    */   
/*    */   public DiffuseFilter() {
/* 33 */     setEdgeAction(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setScale(float scale) {
/* 45 */     this.scale = scale;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float getScale() {
/* 55 */     return this.scale;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void transformInverse(int x, int y, float[] out) {
/* 60 */     int angle = (int)(Math.random() * 255.0D);
/* 61 */     float distance = (float)Math.random();
/* 62 */     out[0] = x + distance * this.sinTable[angle];
/* 63 */     out[1] = y + distance * this.cosTable[angle];
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 68 */     this.sinTable = new float[256];
/* 69 */     this.cosTable = new float[256];
/*    */     
/* 71 */     for (int i = 0; i < 256; i++) {
/*    */       
/* 73 */       float angle = 6.2831855F * i / 256.0F;
/* 74 */       this.sinTable[i] = (float)(this.scale * Math.sin(angle));
/* 75 */       this.cosTable[i] = (float)(this.scale * Math.cos(angle));
/*    */     } 
/*    */     
/* 78 */     return super.filter(src, dst);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 83 */     return "Distort/Diffuse...";
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\DiffuseFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */