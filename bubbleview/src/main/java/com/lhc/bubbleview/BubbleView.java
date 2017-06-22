package com.lhc.bubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：LHC on 2017/6/22 10:22
 * 描述：
 */
public class BubbleView extends View {
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
    private final int STATE_DIMISS = 3;
    /**
     * 目前状态
     */
    private int state = STATE_STATIC;

    private int mBubbleStillRadius;
    private int mBubbleMoveRadius;

    private Paint bgPaint;
    private Paint txtPaint;
    private Paint.FontMetrics fm;

    private int bgColor;
    private int txtColor;

    private RectF stillRect;
    private RectF moveRect;

    private String txt;

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(txtColor);

        fm = txtPaint.getFontMetrics();
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
            case STATE_DIMISS:
                drawDismiss(canvas);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(state == STATE_STATIC){
                    state = STATE_CONNECT;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                double dis = Math.hypot(stillRect.centerX() - moveRect.centerX(), stillRect.centerY() - moveRect.centerY());
                if(dis > mBubbleMoveRadius * 3){
                    state = STATE_DISCONNECT;
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void drawStill(Canvas canvas) {
        //绘制背景
        canvas.drawCircle(moveRect.centerX(), moveRect.centerY(), mBubbleMoveRadius, bgPaint);
        //绘制文字
        drawTxt(canvas, moveRect);
    }

    private void drawConnect(Canvas canvas) {
        canvas.drawCircle(stillRect.centerX(), stillRect.centerY(), mBubbleStillRadius, bgPaint);
        canvas.drawCircle(moveRect.centerX(), moveRect.centerY(), mBubbleMoveRadius, bgPaint);

        Path path = new Path();
        int centerX = (int) ((stillRect.centerX() + moveRect.centerX()) / 2);
        int centerY = (int) ((stillRect.centerY() + moveRect.centerY()) / 2);
        double dis = Math.hypot(stillRect.centerX() - moveRect.centerX(), stillRect.centerY() - moveRect.centerY());
        float cosTheta = (float) (stillRect.centerX() - moveRect.centerY() / dis);
        float sinTheta = (float) (stillRect.centerY() - moveRect.centerY() / dis);
        float aX = moveRect.centerX() + sinTheta * mBubbleMoveRadius;
        float aY = moveRect.centerY() + cosTheta * mBubbleMoveRadius;
        float bX = stillRect.centerX() + sinTheta * mBubbleStillRadius;
        float bY = stillRect.centerY() + cosTheta * mBubbleStillRadius;
        path.moveTo(aX, aY);
        path.quadTo(centerX, centerY, bX, bY);

        float cX = stillRect.centerX() - sinTheta * mBubbleStillRadius;
        float cY = stillRect.centerY() - cosTheta * mBubbleStillRadius;
        float dX = moveRect.centerX() - sinTheta * mBubbleMoveRadius;
        float dY = moveRect.centerY() - cosTheta * mBubbleMoveRadius;
        path.moveTo(cX, cY);
        path.quadTo(centerX, centerY, dX, dY);
        path.close();
        canvas.drawPath(path, bgPaint);

        drawTxt(canvas, moveRect);
    }

    private void drawDisconnect(Canvas canvas) {
        canvas.drawCircle(stillRect.centerX(), stillRect.centerY(), mBubbleStillRadius, bgPaint);
        canvas.drawCircle(moveRect.centerX(), moveRect.centerY(), mBubbleMoveRadius, bgPaint);
        drawTxt(canvas, moveRect);
    }

    private void drawDismiss(Canvas canvas) {

    }

    private void drawTxt(Canvas canvas, RectF rectF) {
        canvas.drawText(txt, rectF.centerX(), (fm.bottom - fm.top) / 2 - fm.bottom + rectF.centerY(), txtPaint);
    }
}
