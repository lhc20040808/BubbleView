package com.lhc.bubbleview;

import android.graphics.Bitmap;

/**
 * 作者：lhc
 * 时间：2017/6/23.
 */

public class FallDownParticleFactory implements IParticleFactory {
    public static final int DEFAULT_RADIUS = 4;

    @Override
    public Particle[][] generateParticleFactory(Bitmap bitmap, int left, int top) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int rowCount = h / DEFAULT_RADIUS * 2;//行数
        int columnCount = w / DEFAULT_RADIUS * 2;//列数

        int bitmapPartWidth = w / columnCount;
        int bitmapPartHeight = h / rowCount;

        Particle[][] particles = new Particle[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                int color = bitmap.getPixel(bitmapPartWidth * column, bitmapPartHeight * row);
                int x = column * DEFAULT_RADIUS + left;
                int y = row * DEFAULT_RADIUS + top;
                particles[row][column] = new FallDownParticle(x, y, w, h, color);
            }
        }

        return particles;

    }
}
