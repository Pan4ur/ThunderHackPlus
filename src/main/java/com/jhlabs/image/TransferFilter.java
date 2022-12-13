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
/*    */ public abstract class TransferFilter
/*    */   extends PointFilter
/*    */ {
/*    */   protected int[] rTable;
/*    */   protected int[] gTable;
/*    */   protected int[] bTable;
/*    */   protected boolean initialized = false;
/*    */   
/*    */   public TransferFilter() {
/* 29 */     this.canFilterIndexColorModel = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 34 */     int a = rgb & 0xFF000000;
/* 35 */     int r = rgb >> 16 & 0xFF;
/* 36 */     int g = rgb >> 8 & 0xFF;
/* 37 */     int b = rgb & 0xFF;
/* 38 */     r = this.rTable[r];
/* 39 */     g = this.gTable[g];
/* 40 */     b = this.bTable[b];
/* 41 */     return a | r << 16 | g << 8 | b;
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 46 */     if (!this.initialized)
/*    */     {
/* 48 */       initialize();
/*    */     }
/*    */     
/* 51 */     return super.filter(src, dst);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void initialize() {
/* 56 */     this.initialized = true;
/* 57 */     this.rTable = this.gTable = this.bTable = makeTable();
/*    */   }
/*    */ 
/*    */   
/*    */   protected int[] makeTable() {
/* 62 */     int[] table = new int[256];
/*    */     
/* 64 */     for (int i = 0; i < 256; i++)
/*    */     {
/* 66 */       table[i] = PixelUtils.clamp((int)(255.0F * transferFunction(i / 255.0F)));
/*    */     }
/*    */     
/* 69 */     return table;
/*    */   }
/*    */ 
/*    */   
/*    */   protected float transferFunction(float v) {
/* 74 */     return 0.0F;
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] getLUT() {
/* 79 */     if (!this.initialized)
/*    */     {
/* 81 */       initialize();
/*    */     }
/*    */     
/* 84 */     int[] lut = new int[256];
/*    */     
/* 86 */     for (int i = 0; i < 256; i++)
/*    */     {
/* 88 */       lut[i] = filterRGB(0, 0, i << 24 | i << 16 | i << 8 | i);
/*    */     }
/*    */     
/* 91 */     return lut;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TransferFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */