package com.feimeng.view.lock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Author: Feimeng
 * Time:   2021/1/14
 * Description:
 */
@SuppressLint({"ViewConstructor"})
public class PatternLockDotView extends View {
    private int normalColor;
    private int smallRadius;
    private int bigRadius;
    private int rightColor;
    private int wrongColor;
    private PatternLockDotView.State mCurrentState;
    private Paint mPaint;
    private boolean needZoomIn;

    public PatternLockDotView(Context context, int normalColor, int smallRadius, int bigRadius, int rightColor, int wrongColor) {
        super(context);
        this.mCurrentState = PatternLockDotView.State.STATE_NORMAL;
        this.normalColor = normalColor;
        this.smallRadius = smallRadius;
        this.bigRadius = bigRadius;
        this.rightColor = rightColor;
        this.wrongColor = wrongColor;
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == -2147483648) {
            widthSize = Math.round((float)(this.bigRadius * 2));
        }

        if (heightMode == -2147483648) {
            heightSize = Math.round((float)(this.bigRadius * 2));
        }

        this.setMeasuredDimension(widthSize, heightSize);
    }

    protected void onDraw(Canvas canvas) {
        switch(this.mCurrentState) {
            case STATE_NORMAL:
                this.mPaint.setColor(this.normalColor);
                this.mPaint.setAlpha(255);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.smallRadius, this.mPaint);
                if (this.needZoomIn) {
                    this.zoomIn();
                }
                break;
            case STATE_SELECTED:
                this.mPaint.setColor(this.normalColor);
                this.mPaint.setAlpha(255);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.smallRadius, this.mPaint);
                this.mPaint.setAlpha(76);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.bigRadius, this.mPaint);
                this.zoomOut();
                break;
            case STATE_RESULT_RIGHT:
                this.mPaint.setColor(this.rightColor);
                this.mPaint.setAlpha(76);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.bigRadius, this.mPaint);
                this.mPaint.setAlpha(255);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.smallRadius, this.mPaint);
                break;
            case STATE_RESULT_WRONG:
                this.mPaint.setColor(this.wrongColor);
                this.mPaint.setAlpha(76);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.bigRadius, this.mPaint);
                this.mPaint.setAlpha(255);
                canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), (float)this.smallRadius, this.mPaint);
        }

    }

    public void setCurrentState(PatternLockDotView.State state) {
        this.mCurrentState = state;
        this.invalidate();
    }

    private void zoomOut() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.0F, 1.2F});
        animatorX.setDuration(50L);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.0F, 1.2F});
        animatorY.setDuration(50L);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{animatorX, animatorY});
        set.start();
        this.needZoomIn = true;
    }

    private void zoomIn() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.0F, 1.0F});
        animatorX.setDuration(0L);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.0F, 1.0F});
        animatorY.setDuration(0L);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{animatorX, animatorY});
        set.start();
        this.needZoomIn = false;
    }

    public static enum State {
        STATE_NORMAL,
        STATE_SELECTED,
        STATE_RESULT_RIGHT,
        STATE_RESULT_WRONG;

        private State() {
        }
    }
}

