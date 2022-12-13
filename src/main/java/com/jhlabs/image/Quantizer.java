package com.jhlabs.image;

public interface Quantizer {
  void setup(int paramInt);
  
  void addPixels(int[] paramArrayOfint, int paramInt1, int paramInt2);
  
  int[] buildColorTable();
  
  int getIndexForColor(int paramInt);
}


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\image\Quantizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */