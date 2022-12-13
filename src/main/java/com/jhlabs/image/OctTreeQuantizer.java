/*     */ package com.jhlabs.image;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
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
/*     */ public class OctTreeQuantizer
/*     */   implements Quantizer
/*     */ {
/*     */   static final int MAX_LEVEL = 5;
/*     */   
/*     */   class OctTreeNode
/*     */   {
/*     */     int children;
/*     */     int level;
/*     */     OctTreeNode parent;
/*  44 */     OctTreeNode[] leaf = new OctTreeNode[8];
/*     */     
/*     */     boolean isLeaf;
/*     */     
/*     */     int count;
/*     */     
/*     */     int totalRed;
/*     */     int totalGreen;
/*     */     int totalBlue;
/*     */     int index;
/*     */     
/*     */     public void list(PrintStream s, int level) {
/*     */       int i;
/*  57 */       for (i = 0; i < level; i++)
/*     */       {
/*  59 */         System.out.print(' ');
/*     */       }
/*     */       
/*  62 */       if (this.count == 0) {
/*     */         
/*  64 */         System.out.println(this.index + ": count=" + this.count);
/*     */       }
/*     */       else {
/*     */         
/*  68 */         System.out.println(this.index + ": count=" + this.count + " red=" + (this.totalRed / this.count) + " green=" + (this.totalGreen / this.count) + " blue=" + (this.totalBlue / this.count));
/*     */       } 
/*     */       
/*  71 */       for (i = 0; i < 8; i++) {
/*  72 */         if (this.leaf[i] != null)
/*     */         {
/*  74 */           this.leaf[i].list(s, level + 2);
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*  79 */   private int nodes = 0;
/*     */   private OctTreeNode root;
/*     */   private int reduceColors;
/*     */   private int maximumColors;
/*  83 */   private int colors = 0;
/*     */   
/*     */   private Vector[] colorList;
/*     */   
/*     */   public OctTreeQuantizer() {
/*  88 */     setup(256);
/*  89 */     this.colorList = new Vector[6];
/*     */     
/*  91 */     for (int i = 0; i < 6; i++)
/*     */     {
/*  93 */       this.colorList[i] = new Vector();
/*     */     }
/*     */     
/*  96 */     this.root = new OctTreeNode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setup(int numColors) {
/* 105 */     this.maximumColors = numColors;
/* 106 */     this.reduceColors = Math.max(512, numColors * 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPixels(int[] pixels, int offset, int count) {
/* 117 */     for (int i = 0; i < count; i++) {
/*     */       
/* 119 */       insertColor(pixels[i + offset]);
/*     */       
/* 121 */       if (this.colors > this.reduceColors)
/*     */       {
/* 123 */         reduceTree(this.reduceColors);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndexForColor(int rgb) {
/* 135 */     int red = rgb >> 16 & 0xFF;
/* 136 */     int green = rgb >> 8 & 0xFF;
/* 137 */     int blue = rgb & 0xFF;
/* 138 */     OctTreeNode node = this.root;
/*     */     
/* 140 */     for (int level = 0; level <= 5; level++) {
/*     */ 
/*     */       
/* 143 */       int bit = 128 >> level;
/* 144 */       int index = 0;
/*     */       
/* 146 */       if ((red & bit) != 0)
/*     */       {
/* 148 */         index += 4;
/*     */       }
/*     */       
/* 151 */       if ((green & bit) != 0)
/*     */       {
/* 153 */         index += 2;
/*     */       }
/*     */       
/* 156 */       if ((blue & bit) != 0)
/*     */       {
/* 158 */         index++;
/*     */       }
/*     */       
/* 161 */       OctTreeNode child = node.leaf[index];
/*     */       
/* 163 */       if (child == null)
/*     */       {
/* 165 */         return node.index;
/*     */       }
/* 167 */       if (child.isLeaf)
/*     */       {
/* 169 */         return child.index;
/*     */       }
/*     */ 
/*     */       
/* 173 */       node = child;
/*     */     } 
/*     */ 
/*     */     
/* 177 */     System.out.println("getIndexForColor failed");
/* 178 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   private void insertColor(int rgb) {
/* 183 */     int red = rgb >> 16 & 0xFF;
/* 184 */     int green = rgb >> 8 & 0xFF;
/* 185 */     int blue = rgb & 0xFF;
/* 186 */     OctTreeNode node = this.root;
/*     */ 
/*     */     
/* 189 */     for (int level = 0; level <= 5; level++) {
/*     */ 
/*     */       
/* 192 */       int bit = 128 >> level;
/* 193 */       int index = 0;
/*     */       
/* 195 */       if ((red & bit) != 0)
/*     */       {
/* 197 */         index += 4;
/*     */       }
/*     */       
/* 200 */       if ((green & bit) != 0)
/*     */       {
/* 202 */         index += 2;
/*     */       }
/*     */       
/* 205 */       if ((blue & bit) != 0)
/*     */       {
/* 207 */         index++;
/*     */       }
/*     */       
/* 210 */       OctTreeNode child = node.leaf[index];
/*     */       
/* 212 */       if (child == null) {
/*     */         
/* 214 */         node.children++;
/* 215 */         child = new OctTreeNode();
/* 216 */         child.parent = node;
/* 217 */         node.leaf[index] = child;
/* 218 */         node.isLeaf = false;
/* 219 */         this.nodes++;
/* 220 */         this.colorList[level].addElement(child);
/*     */         
/* 222 */         if (level == 5) {
/*     */           
/* 224 */           child.isLeaf = true;
/* 225 */           child.count = 1;
/* 226 */           child.totalRed = red;
/* 227 */           child.totalGreen = green;
/* 228 */           child.totalBlue = blue;
/* 229 */           child.level = level;
/* 230 */           this.colors++;
/*     */           
/*     */           return;
/*     */         } 
/* 234 */         node = child;
/*     */       } else {
/* 236 */         if (child.isLeaf) {
/*     */           
/* 238 */           child.count++;
/* 239 */           child.totalRed += red;
/* 240 */           child.totalGreen += green;
/* 241 */           child.totalBlue += blue;
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 246 */         node = child;
/*     */       } 
/*     */     } 
/*     */     
/* 250 */     System.out.println("insertColor failed");
/*     */   }
/*     */ 
/*     */   
/*     */   private void reduceTree(int numColors) {
/* 255 */     for (int level = 4; level >= 0; level--) {
/*     */       
/* 257 */       Vector<OctTreeNode> v = this.colorList[level];
/*     */       
/* 259 */       if (v != null && v.size() > 0)
/*     */       {
/* 261 */         for (int j = 0; j < v.size(); j++) {
/*     */           
/* 263 */           OctTreeNode node = v.elementAt(j);
/*     */           
/* 265 */           if (node.children > 0) {
/*     */             
/* 267 */             for (int i = 0; i < 8; i++) {
/*     */               
/* 269 */               OctTreeNode child = node.leaf[i];
/*     */               
/* 271 */               if (child != null) {
/*     */                 
/* 273 */                 if (!child.isLeaf)
/*     */                 {
/* 275 */                   System.out.println("not a leaf!");
/*     */                 }
/*     */                 
/* 278 */                 node.count += child.count;
/* 279 */                 node.totalRed += child.totalRed;
/* 280 */                 node.totalGreen += child.totalGreen;
/* 281 */                 node.totalBlue += child.totalBlue;
/* 282 */                 node.leaf[i] = null;
/* 283 */                 node.children--;
/* 284 */                 this.colors--;
/* 285 */                 this.nodes--;
/* 286 */                 this.colorList[level + 1].removeElement(child);
/*     */               } 
/*     */             } 
/*     */             
/* 290 */             node.isLeaf = true;
/* 291 */             this.colors++;
/*     */             
/* 293 */             if (this.colors <= numColors) {
/*     */               return;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 302 */     System.out.println("Unable to reduce the OctTree");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] buildColorTable() {
/* 311 */     int[] table = new int[this.colors];
/* 312 */     buildColorTable(this.root, table, 0);
/* 313 */     return table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void buildColorTable(int[] inPixels, int[] table) {
/* 323 */     int count = inPixels.length;
/* 324 */     this.maximumColors = table.length;
/*     */     
/* 326 */     for (int i = 0; i < count; i++) {
/*     */       
/* 328 */       insertColor(inPixels[i]);
/*     */       
/* 330 */       if (this.colors > this.reduceColors)
/*     */       {
/* 332 */         reduceTree(this.reduceColors);
/*     */       }
/*     */     } 
/*     */     
/* 336 */     if (this.colors > this.maximumColors)
/*     */     {
/* 338 */       reduceTree(this.maximumColors);
/*     */     }
/*     */     
/* 341 */     buildColorTable(this.root, table, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   private int buildColorTable(OctTreeNode node, int[] table, int index) {
/* 346 */     if (this.colors > this.maximumColors)
/*     */     {
/* 348 */       reduceTree(this.maximumColors);
/*     */     }
/*     */     
/* 351 */     if (node.isLeaf) {
/*     */       
/* 353 */       int count = node.count;
/* 354 */       table[index] = 0xFF000000 | node.totalRed / count << 16 | node.totalGreen / count << 8 | node.totalBlue / count;
/*     */ 
/*     */ 
/*     */       
/* 358 */       node.index = index++;
/*     */     }
/*     */     else {
/*     */       
/* 362 */       for (int i = 0; i < 8; i++) {
/*     */         
/* 364 */         if (node.leaf[i] != null) {
/*     */           
/* 366 */           node.index = index;
/* 367 */           index = buildColorTable(node.leaf[i], table, index);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 372 */     return index;
/*     */   }
/*     */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\OctTreeQuantizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */