/*     */ package com.jhlabs.composite;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.CompositeContext;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.image.ColorModel;
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
/*     */ public final class MiscComposite
/*     */   implements Composite
/*     */ {
/*     */   public static final int BLEND = 0;
/*     */   public static final int ADD = 1;
/*     */   public static final int SUBTRACT = 2;
/*     */   public static final int DIFFERENCE = 3;
/*     */   public static final int MULTIPLY = 4;
/*     */   public static final int DARKEN = 5;
/*     */   public static final int BURN = 6;
/*     */   public static final int COLOR_BURN = 7;
/*     */   public static final int SCREEN = 8;
/*     */   public static final int LIGHTEN = 9;
/*     */   public static final int DODGE = 10;
/*     */   public static final int COLOR_DODGE = 11;
/*     */   public static final int HUE = 12;
/*     */   public static final int SATURATION = 13;
/*     */   public static final int VALUE = 14;
/*     */   public static final int COLOR = 15;
/*     */   public static final int OVERLAY = 16;
/*     */   public static final int SOFT_LIGHT = 17;
/*     */   public static final int HARD_LIGHT = 18;
/*     */   public static final int PIN_LIGHT = 19;
/*     */   public static final int EXCLUSION = 20;
/*     */   public static final int NEGATION = 21;
/*     */   public static final int AVERAGE = 22;
/*     */   public static final int STENCIL = 23;
/*     */   public static final int SILHOUETTE = 24;
/*     */   private static final int MIN_RULE = 0;
/*     */   private static final int MAX_RULE = 24;
/*  59 */   public static String[] RULE_NAMES = new String[] { "Normal", "Add", "Subtract", "Difference", "Multiply", "Darken", "Burn", "Color Burn", "Screen", "Lighten", "Dodge", "Color Dodge", "Hue", "Saturation", "Brightness", "Color", "Overlay", "Soft Light", "Hard Light", "Pin Light", "Exclusion", "Negation", "Average", "Stencil", "Silhouette" };
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
/*     */   protected float extraAlpha;
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
/*     */   protected int rule;
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
/*     */   private MiscComposite(int rule) {
/*  99 */     this(rule, 1.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   private MiscComposite(int rule, float alpha) {
/* 104 */     if (alpha < 0.0F || alpha > 1.0F)
/*     */     {
/* 106 */       throw new IllegalArgumentException("alpha value out of range");
/*     */     }
/*     */     
/* 109 */     if (rule < 0 || rule > 24)
/*     */     {
/* 111 */       throw new IllegalArgumentException("unknown composite rule");
/*     */     }
/*     */     
/* 114 */     this.rule = rule;
/* 115 */     this.extraAlpha = alpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Composite getInstance(int rule, float alpha) {
/* 120 */     switch (rule) {
/*     */       
/*     */       case 0:
/* 123 */         return AlphaComposite.getInstance(3, alpha);
/*     */       
/*     */       case 1:
/* 126 */         return new AddComposite(alpha);
/*     */       
/*     */       case 2:
/* 129 */         return new SubtractComposite(alpha);
/*     */       
/*     */       case 3:
/* 132 */         return new DifferenceComposite(alpha);
/*     */       
/*     */       case 4:
/* 135 */         return new MultiplyComposite(alpha);
/*     */       
/*     */       case 5:
/* 138 */         return new DarkenComposite(alpha);
/*     */       
/*     */       case 6:
/* 141 */         return new BurnComposite(alpha);
/*     */       
/*     */       case 7:
/* 144 */         return new ColorBurnComposite(alpha);
/*     */       
/*     */       case 8:
/* 147 */         return new ScreenComposite(alpha);
/*     */       
/*     */       case 9:
/* 150 */         return new LightenComposite(alpha);
/*     */       
/*     */       case 10:
/* 153 */         return new DodgeComposite(alpha);
/*     */       
/*     */       case 11:
/* 156 */         return new ColorDodgeComposite(alpha);
/*     */       
/*     */       case 12:
/* 159 */         return new HueComposite(alpha);
/*     */       
/*     */       case 13:
/* 162 */         return new SaturationComposite(alpha);
/*     */       
/*     */       case 14:
/* 165 */         return new ValueComposite(alpha);
/*     */       
/*     */       case 15:
/* 168 */         return new ColorComposite(alpha);
/*     */       
/*     */       case 16:
/* 171 */         return new OverlayComposite(alpha);
/*     */       
/*     */       case 17:
/* 174 */         return new SoftLightComposite(alpha);
/*     */       
/*     */       case 18:
/* 177 */         return new HardLightComposite(alpha);
/*     */       
/*     */       case 19:
/* 180 */         return new PinLightComposite(alpha);
/*     */       
/*     */       case 20:
/* 183 */         return new ExclusionComposite(alpha);
/*     */       
/*     */       case 21:
/* 186 */         return new NegationComposite(alpha);
/*     */       
/*     */       case 22:
/* 189 */         return new AverageComposite(alpha);
/*     */       
/*     */       case 23:
/* 192 */         return AlphaComposite.getInstance(6, alpha);
/*     */       
/*     */       case 24:
/* 195 */         return AlphaComposite.getInstance(8, alpha);
/*     */     } 
/*     */     
/* 198 */     return new MiscComposite(rule, alpha);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 203 */     return new MiscCompositeContext(this.rule, this.extraAlpha, srcColorModel, dstColorModel);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAlpha() {
/* 208 */     return this.extraAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRule() {
/* 213 */     return this.rule;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 218 */     return Float.floatToIntBits(this.extraAlpha) * 31 + this.rule;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 223 */     if (!(o instanceof MiscComposite))
/*     */     {
/* 225 */       return false;
/*     */     }
/*     */     
/* 228 */     MiscComposite c = (MiscComposite)o;
/*     */     
/* 230 */     if (this.rule != c.rule)
/*     */     {
/* 232 */       return false;
/*     */     }
/*     */     
/* 235 */     if (this.extraAlpha != c.extraAlpha)
/*     */     {
/* 237 */       return false;
/*     */     }
/*     */     
/* 240 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\MiscComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */