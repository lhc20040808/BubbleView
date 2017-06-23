package com.lhc.bubbleview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * 作者：LHC on 2017/6/22 10:22
 * 描述：
 */
public class BubbleView extends View {
    private static final int MOVE_OFFSET = 25;
    /**
     * 静止状态
     */
    private final int STATE_STATIC = 0;
    /**
     * 相连状态
     */
    private final int STATE_CONNECT = 1;
    /**
     * 不相连状态
     */
    private final int STATE_DISCONNECT = 2;
    /**
     * 消失状态
     */
    private final int STATE_DISMISS = 3;
    /**
     * 目前状态
     */
    private int state = STATE_STATIC;

    private Path path;

    private int mBubbleStillRadius;
    private int mBubbleMoveRadius;

    private Paint bgPaint;
    private Paint txtPaint;
    private Paint.FontMetrics fm;

    private int bgColor;
    private int txtColor;

    private PointF mStillCenter = new PointF();
    private PointF mMoveCenter = new PointF();

    private String txt;
    /**
     * 圆心距
     */
    private int mDis;

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        testInit();
        init();
    }

    private void testInit() {
        txtColor = Color.WHITE;
        bgColor = Color.parseColor("#FF7256");
        mBubbleMoveRadius = 30;
        txt = "5";
    }

    public void setText(String txt) {
        this.txt = txt;
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(txtColor);
        txtPaint.setTextSize(40);

        fm = txtPaint.getFontMetrics();

        path = new Path();

        mBubbleStillRadius = mBubbleMoveRadius;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMoveCenter.x = getWidth() / 2;
        mMoveCenter.y = getHeight() / 2;
        mStillCenter.x = getWidth() / 2;
        mStillCenter.y = getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (state) {
            case STATE_STATIC:
                drawStill(canvas);
                break;
            case STATE_CONNECT:
                drawConnect(canvas);
                break;
            case STATE_DISCONNECT:
                drawDisconnect(canvas);
                break;
            case STATE_DISMISS:
                drawDismiss(canvas);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state != STATE_DISMISS) {
                    mDis = (int) Math.hypot(event.getX() - mStillCenter.x, event.getY() - mStillCenter.y);
                    Log.d("test", "dis:" + mDis + "|" + "mBubbleMoveRadius + MOVE_OFFSET:" + mBubbleMoveRadius + MOVE_OFFSET);
                    if (mDis < mBubbleMoveRadius + MOVE_OFFSET) {
                        state = STATE_CONNECT;
                    } else {
                        state = STATE_STATIC;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state != STATE_STATIC) {
                    mMoveCenter.x = (int) event.getX();
                    mMoveCenter.y = (int) event.getY();

                    mDis = (int) Math.hypot(event.getX() - mStillCenter.x, event.getY() - mStillCenter.y);
                    if (state == STATE_CONNECT) {
                        if (mDis < mBubbleMoveRadius * 8 - MOVE_OFFSET) {
                            mBubbleStillRadius = mBubbleMoveRadius - mDis / 8;
                        } else {
                            state = STATE_DISCONNECT;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == STATE_CONNECT) {
                    startBubbleResetAnim();
                } else if (state == STATE_DISCONNECT) {
                    if (mDis < mBubbleMoveRadius * 2) {
                        startBubbleResetAnim();
                    } else {
                        startBurstAnim();
                    }
                }
                break;
        }
        return true;
    }

    private void startBurstAnim() {
        state = STATE_DISMISS;
    }

    private void startBubbleResetAnim() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            api21RestAnim();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void api21RestAnim() {
        ValueAnimator valueAnimator = null;
        valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(), new PointF(mMoveCenter.x, mMoveCenter.y), new PointF(mStillCenter.x, mStillCenter.y));
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new OvershootInterpolator(5));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMoveCenter = (PointF) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                state = STATE_STATIC;
            }
        });
        valueAnimator.start();
    }

    private void drawStill(Canvas canvas) {
        //绘制背景
        canvas.drawCircle(mMoveCenter.x, mMoveCenter.y, mBubbleMoveRadius, bgPaint);
        //绘制文字
        drawTxt(canvas, mMoveCenter.x, mMoveCenter.y);
    }

    private void drawConnect(Canvas canvas) {
        canvas.drawCircle(mStillCenter.x, mStillCenter.y, mBubbleStillRadius, bgPaint);
        canvas.drawCircle(mMoveCenter.x, mMoveCenter.y, mBubbleMoveRadius, bgPaint);
        path.reset();
        float iAnchorX = (mStillCenter.x + mMoveCenter.x) / 2;
        float iAnchorY = (mStillCenter.y + mMoveCenter.y) / 2;
        float cosTheta = (mMoveCenter.x - mStillCenter.x) / mDis;
        float sinTheta = (mMoveCenter.y - mStillCenter.y) / mDis;
        float aX = mStillCenter.x - sinTheta * mBubbleStillRadius;
        float aY = mStillCenter.y + cosTheta * mBubbleStillRadius;
        float bX = mMoveCenter.x - sinTheta * mBubbleMoveRadius;
        float bY = mMoveCenter.y + cosTheta * mBubbleMoveRadius;
        path.moveTo(aX, aY);
        path.quadTo(iAnchorX, iAnchorY, bX, bY);

        float cX = mMoveCenter.x + sinTheta * mBubbleMoveRadius;
        float cY = mMoveCenter.y - cosTheta * mBubbleMoveRadius;
        float dX = mStillCenter.x + sinTheta * mBubbleStillRadius;
        float dY = mStillCenter.y - cosTheta * mBubbleStillRadius;
        path.lineTo(cX, cY);
        path.quadTo(iAnchorX, iAnchorY, dX, dY);
        path.close();
        canvas.drawPath(path, bgPaint);

        drawTxt(canvas, mMoveCenter.x, mMoveCenter.y);
    }

    private void drawDisconnect(Canvas canvas) {
        canvas.drawCircle(mStillCenter.x, mStillCenter.y, mBubbleStillRadius, bgPaint);
        canvas.drawCircle(mMoveCenter.x, mMoveCenter.y, mBubbleMoveRadius, bgPaint);
        drawTxt(canvas, mMoveCenter.x, mMoveCenter.y);
    }

    private void drawDismiss(Canvas canvas) {

    }

    private void drawTxt(Canvas canvas, float x, float y) {
        canvas.drawText(txt, x, (fm.bottom - fm.top) / 2 - fm.bottom + y, txtPaint);
    }
}
