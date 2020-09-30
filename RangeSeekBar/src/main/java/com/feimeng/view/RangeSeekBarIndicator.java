package com.feimeng.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * Author: Feimeng
 * Time:   2020/9/30
 * Description:
 */
class RangeSeekBarIndicator extends AppCompatSeekBar {
    // 画笔
    private Paint mPaint;
    // 进度文字位置信息
    private Rect mProgressTextRect = new Rect();
    // 滑块按钮宽度
    private int mThumbWidth;
    private RangeSeekBar.SeekBarTextCreater mSeekBarTextCreater;

    public RangeSeekBarIndicator(Context context) {
        this(context, null);
    }

    public RangeSeekBarIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public RangeSeekBarIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setFakeBoldText(true);
        mPaint.setAntiAlias(true);
    }

    public void setTextSize(int sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
        mPaint.setTextSize(px);
    }

    public void setTextColor(int color) {
        mPaint.setColor(color);
    }

    public void setThumbWidth(int thumbWidth) {
        mThumbWidth = thumbWidth;
    }

    public void setSeekBarTextCreater(RangeSeekBar.SeekBarTextCreater seekBarTextCreater) {
        mSeekBarTextCreater = seekBarTextCreater;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressText(canvas);
    }

    private void drawProgressText(Canvas canvas) {
        if (mSeekBarTextCreater == null) return;
        String progressText = mSeekBarTextCreater.createText(getProgress());
        mPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);
        // 进度百分比
        float progressRatio = (float) getProgress() / getMax();
        // thumb偏移量
        float thumbOffset = getThumbOffset() - mThumbWidth / 2F - mProgressTextRect.width() / 2F - 5;
        float thumbX = getWidth() * progressRatio + thumbOffset;
        float thumbY = getHeight() / 2f + mProgressTextRect.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mPaint);
    }
}
