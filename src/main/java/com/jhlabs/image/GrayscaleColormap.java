/*    */ package com.jhlabs.image;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GrayscaleColormap
/*    */   implements Colormap
/*    */ {
/*    */   public int getColor(float v) {
/* 35 */     int n = (int)(v * 255.0F);
/*    */     
/* 37 */     if (n < 0) {
/*    */       
/* 39 */       n = 0;
/*    */     }
/* 41 */     else if (n > 255) {
/*    */       
/* 43 */       n = 255;
/*    */     } 
/*    */     
/* 46 */     return 0xFF000000 | n << 16 | n << 8 | n;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\GrayscaleColormap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */