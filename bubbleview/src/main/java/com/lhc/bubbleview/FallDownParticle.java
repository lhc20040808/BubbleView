package com.lhc.bubbleview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by qgg on 2017/6/23.
 */

public class FallDownParticle extends Particle {
    private float radius = FallDownParticleFactory.DEFAULT_RADIUS;
    private float alpha = 1.0f;
    private Random random = new Random();

    public FallDownParticle(int x, int y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        paint.setAlpha((int) (Color.alpha(color) * alpha));
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    void cal(float factor) {

        x = x + factor * random.nextInt(width) * (random.nextFloat() - 0.5f);
        y = y + factor * random.nextInt(height / 2);
        radius = radius - random.nextInt(2);
        alpha = (1 - factor) * (1 + random.nextFloat());

    }
}
