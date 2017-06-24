package com.lhc.bubbleview;

import android.graphics.Bitmap;

/**
 * 作者：lhc
 * 时间：2017/6/23.
 */

public interface IParticleFactory {

   public Particle[][] generateParticleFactory(Bitmap bitmap, int left, int top);
}
