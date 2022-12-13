/*    */ package com.jhlabs.composite;
/*    */ 
/*    */ import java.awt.Composite;
/*    */ import java.awt.CompositeContext;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.image.ColorModel;
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
/*    */ public final class ContourComposite
/*    */   implements Composite
/*    */ {
/*    */   private int offset;
/*    */   
/*    */   public ContourComposite(int offset) {
/* 33 */     this.offset = offset;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 38 */     return new ContourCompositeContext(this.offset, srcColorModel, dstColorModel);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 43 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 48 */     if (!(o instanceof ContourComposite))
/*    */     {
/* 50 */       return false;
/*    */     }
/*    */     
/* 53 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\ContourComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */