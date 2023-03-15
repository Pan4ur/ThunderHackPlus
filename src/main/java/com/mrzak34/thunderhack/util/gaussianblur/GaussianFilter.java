package com.mrzak34.thunderhack.util.gaussianblur;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;


public class GaussianFilter
        extends ConvolveFilter {
    protected float radius;
    protected Kernel kernel;

    public GaussianFilter(float radius) {
        /*  52 */
        setRadius(radius);
    }

    public static void convolveAndTranspose(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, boolean premultiply, boolean unpremultiply, int edgeAction) {
        /* 114 */
        float[] matrix = kernel.getKernelData(null);
        /* 115 */
        int cols = kernel.getWidth();
        /* 116 */
        int cols2 = cols / 2;

        /* 118 */
        for (int y = 0; y < height; y++) {

            /* 120 */
            int index = y;
            /* 121 */
            int ioffset = y * width;

            /* 123 */
            for (int x = 0; x < width; x++) {

                /* 125 */
                float r = 0.0F, g = 0.0F, b = 0.0F, a = 0.0F;
                /* 126 */
                int moffset = cols2;

                /* 128 */
                for (int col = -cols2; col <= cols2; col++) {

                    /* 130 */
                    float f = matrix[moffset + col];

                    /* 132 */
                    if (f != 0.0F) {

                        /* 134 */
                        int ix = x + col;

                        /* 136 */
                        if (ix < 0) {

                            /* 138 */
                            if (edgeAction == CLAMP_EDGES) {
                                /* 140 */
                                ix = 0;
                            }
                            /* 142 */
                            else if (edgeAction == WRAP_EDGES) {
                                /* 144 */
                                ix = (x + width) % width;
                            }

                            /* 147 */
                        } else if (ix >= width) {

                            /* 149 */
                            if (edgeAction == CLAMP_EDGES) {

                                /* 151 */
                                ix = width - 1;
                            }
                            /* 153 */
                            else if (edgeAction == WRAP_EDGES) {

                                /* 155 */
                                ix = (x + width) % width;
                            }
                        }

                        /* 159 */
                        int rgb = inPixels[ioffset + ix];
                        /* 160 */
                        int pa = rgb >> 24 & 0xFF;
                        /* 161 */
                        int pr = rgb >> 16 & 0xFF;
                        /* 162 */
                        int pg = rgb >> 8 & 0xFF;
                        /* 163 */
                        int pb = rgb & 0xFF;

                        /* 165 */
                        if (premultiply) {

                            /* 167 */
                            float a255 = pa * 0.003921569F;
                            /* 168 */
                            pr = (int) (pr * a255);
                            /* 169 */
                            pg = (int) (pg * a255);
                            /* 170 */
                            pb = (int) (pb * a255);
                        }

                        /* 173 */
                        a += f * pa;
                        /* 174 */
                        r += f * pr;
                        /* 175 */
                        g += f * pg;
                        /* 176 */
                        b += f * pb;
                    }
                }

                /* 180 */
                if (unpremultiply && a != 0.0F && a != 255.0F) {

                    /* 182 */
                    float f = 255.0F / a;
                    /* 183 */
                    r *= f;
                    /* 184 */
                    g *= f;
                    /* 185 */
                    b *= f;
                }

                /* 188 */
                int ia = alpha ? clamp((int) (a + 0.5D)) : 255;
                /* 189 */
                int ir = clamp((int) (r + 0.5D));
                /* 190 */
                int ig = clamp((int) (g + 0.5D));
                /* 191 */
                int ib = clamp((int) (b + 0.5D));
                /* 192 */
                outPixels[index] = ia << 24 | ir << 16 | ig << 8 | ib;
                /* 193 */
                index += height;
            }
        }
    }

    public static int clamp(int c) {
        if (c < 0) {
            return 0;
        }

        return Math.min(c, 255);
    }

    public static Kernel makeKernel(float radius) {
        /* 205 */
        int r = (int) Math.ceil(radius);
        /* 206 */
        int rows = r * 2 + 1;
        /* 207 */
        float[] matrix = new float[rows];
        /* 208 */
        float sigma = radius / 3.0F;
        /* 209 */
        float sigma22 = 2.0F * sigma * sigma;
        /* 210 */
        float sigmaPi2 = 6.2831855F * sigma;
        /* 211 */
        float sqrtSigmaPi2 = (float) Math.sqrt(sigmaPi2);
        /* 212 */
        float radius2 = radius * radius;
        /* 213 */
        float total = 0.0F;
        /* 214 */
        int index = 0;

        /* 216 */
        for (int row = -r; row <= r; row++) {

            /* 218 */
            float distance = (row * row);

            /* 220 */
            if (distance > radius2) {

                /* 222 */
                matrix[index] = 0.0F;
            } else {

                /* 226 */
                matrix[index] = (float) Math.exp((-distance / sigma22)) / sqrtSigmaPi2;
            }

            /* 229 */
            total += matrix[index];
            /* 230 */
            index++;
        }

        /* 233 */
        for (int i = 0; i < rows; i++) {
            /* 235 */
            matrix[i] = matrix[i] / total;
        }

        /* 238 */
        return new Kernel(rows, 1, matrix);
    }

    public float getRadius() {
        /*  75 */
        return this.radius;
    }

    public void setRadius(float radius) {
        /*  64 */
        this.radius = radius;
        /*  65 */
        this.kernel = makeKernel(radius);
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        /*  80 */
        int width = src.getWidth();
        /*  81 */
        int height = src.getHeight();

        /*  83 */
        if (dst == null) {
            /*  85 */
            dst = createCompatibleDestImage(src, null);
        }

        /*  88 */
        int[] inPixels = new int[width * height];
        /*  89 */
        int[] outPixels = new int[width * height];
        /*  90 */
        src.getRGB(0, 0, width, height, inPixels, 0, width);

        /*  92 */
        if (this.radius > 0.0F) {

            /*  94 */
            convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha, (this.alpha && this.premultiplyAlpha), false, CLAMP_EDGES);
            /*  95 */
            convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false, (this.alpha && this.premultiplyAlpha), CLAMP_EDGES);
        }

        /*  98 */
        dst.setRGB(0, 0, width, height, inPixels, 0, width);
        /*  99 */
        return dst;
    }

    public String toString() {
        /* 243 */
        return "Blur/Gaussian Blur...";
    }
}
