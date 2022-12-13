/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
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
/*     */ public class TransitionFilter
/*     */   extends AbstractBufferedImageOp
/*     */ {
/*  31 */   private float transition = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage destination;
/*     */ 
/*     */ 
/*     */   
/*     */   private String property;
/*     */ 
/*     */ 
/*     */   
/*     */   private Method method;
/*     */ 
/*     */ 
/*     */   
/*     */   protected BufferedImageOp filter;
/*     */ 
/*     */ 
/*     */   
/*     */   protected float minValue;
/*     */ 
/*     */ 
/*     */   
/*     */   protected float maxValue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TransitionFilter() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransitionFilter(BufferedImageOp filter, String property, float minValue, float maxValue) {
/*  67 */     this.filter = filter;
/*  68 */     this.property = property;
/*  69 */     this.minValue = minValue;
/*  70 */     this.maxValue = maxValue;
/*     */ 
/*     */     
/*     */     try {
/*  74 */       BeanInfo info = Introspector.getBeanInfo(filter.getClass());
/*  75 */       PropertyDescriptor[] pds = info.getPropertyDescriptors();
/*     */       
/*  77 */       for (int i = 0; i < pds.length; i++) {
/*     */         
/*  79 */         PropertyDescriptor pd = pds[i];
/*     */         
/*  81 */         if (property.equals(pd.getName())) {
/*     */           
/*  83 */           this.method = pd.getWriteMethod();
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*  88 */       if (this.method == null)
/*     */       {
/*  90 */         throw new IllegalArgumentException("No such property in object: " + property);
/*     */       }
/*     */     }
/*  93 */     catch (IntrospectionException e) {
/*     */       
/*  95 */       throw new IllegalArgumentException(e.toString());
/*     */     } 
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
/*     */   public void setTransition(float transition) {
/* 108 */     this.transition = transition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getTransition() {
/* 118 */     return this.transition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestination(BufferedImage destination) {
/* 128 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getDestination() {
/* 138 */     return this.destination;
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
/*     */   public void prepareFilter(float transition) {
/*     */     try {
/* 160 */       this.method.invoke(this.filter, new Object[] { new Float(transition) });
/*     */     }
/* 162 */     catch (Exception e) {
/*     */       
/* 164 */       throw new IllegalArgumentException("Error setting value for property: " + this.property);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage filter(BufferedImage src, BufferedImage dst) {
/* 170 */     if (dst == null)
/*     */     {
/* 172 */       dst = createCompatibleDestImage(src, null);
/*     */     }
/*     */     
/* 175 */     if (this.destination == null)
/*     */     {
/* 177 */       return dst;
/*     */     }
/*     */     
/* 180 */     float itransition = 1.0F - this.transition;
/* 181 */     Graphics2D g = dst.createGraphics();
/*     */     
/* 183 */     if (this.transition != 1.0F) {
/*     */       
/* 185 */       float t = this.minValue + this.transition * (this.maxValue - this.minValue);
/* 186 */       prepareFilter(t);
/* 187 */       g.drawImage(src, this.filter, 0, 0);
/*     */     } 
/*     */     
/* 190 */     if (this.transition != 0.0F) {
/*     */       
/* 192 */       g.setComposite(AlphaComposite.getInstance(3, this.transition));
/* 193 */       float t = this.minValue + itransition * (this.maxValue - this.minValue);
/* 194 */       prepareFilter(t);
/* 195 */       g.drawImage(this.destination, this.filter, 0, 0);
/*     */     } 
/*     */     
/* 198 */     g.dispose();
/* 199 */     return dst;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 204 */     return "Transitions/Transition...";
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\TransitionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */