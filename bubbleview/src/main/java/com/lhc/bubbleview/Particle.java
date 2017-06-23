package com.lhc.bubbleview;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 作者：LHC on 2017/6/23 17:50
 * 描述：
 */
public abstract class Particle {
    float x;
    float y;
    int color;
    int width;
    int height;

    abstract void draw(Canvas canvas, Paint paint);

    abstract void cal(float factor);

    public void advance(Canvas canvas, Paint paint, float factor) {
        cal(factor);
        draw(canvas, paint);
    }
}
