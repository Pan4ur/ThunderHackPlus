/*    */ package com.jhlabs.math;
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
/*    */ public class FBM
/*    */   implements Function2D
/*    */ {
/*    */   protected float[] exponents;
/*    */   protected float H;
/*    */   protected float lacunarity;
/*    */   protected float octaves;
/*    */   protected Function2D basis;
/*    */   
/*    */   public FBM(float H, float lacunarity, float octaves) {
/* 29 */     this(H, lacunarity, octaves, new Noise());
/*    */   }
/*    */ 
/*    */   
/*    */   public FBM(float H, float lacunarity, float octaves, Function2D basis) {
/* 34 */     this.H = H;
/* 35 */     this.lacunarity = lacunarity;
/* 36 */     this.octaves = octaves;
/* 37 */     this.basis = basis;
/* 38 */     this.exponents = new float[(int)octaves + 1];
/* 39 */     float frequency = 1.0F;
/*    */     
/* 41 */     for (int i = 0; i <= (int)octaves; i++) {
/*    */       
/* 43 */       this.exponents[i] = (float)Math.pow(frequency, -H);
/* 44 */       frequency *= lacunarity;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void setBasis(Function2D basis) {
/* 50 */     this.basis = basis;
/*    */   }
/*    */ 
/*    */   
/*    */   public Function2D getBasisType() {
/* 55 */     return this.basis;
/*    */   }
/*    */ 
/*    */   
/*    */   public float evaluate(float x, float y) {
/* 60 */     float value = 0.0F;
/*    */ 
/*    */ 
/*    */     
/* 64 */     x += 371.0F;
/* 65 */     y += 529.0F;
/*    */     int i;
/* 67 */     for (i = 0; i < (int)this.octaves; i++) {
/*    */       
/* 69 */       value += this.basis.evaluate(x, y) * this.exponents[i];
/* 70 */       x *= this.lacunarity;
/* 71 */       y *= this.lacunarity;
/*    */     } 
/*    */     
/* 74 */     float remainder = this.octaves - (int)this.octaves;
/*    */     
/* 76 */     if (remainder != 0.0F)
/*    */     {
/* 78 */       value += remainder * this.basis.evaluate(x, y) * this.exponents[i];
/*    */     }
/*    */     
/* 81 */     return value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\math\FBM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */