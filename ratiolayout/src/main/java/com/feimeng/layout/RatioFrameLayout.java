package com.feimeng.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 自定义宽高比例的FrameLayout，例如2:5、4:3等等，默认为1:1。
 */
public class RatioFrameLayout extends FrameLayout implements IRatio {
    private RatioHelper helper = null;

    public RatioFrameLayout(@NonNull Context context) {
        super(context);
        initAttr(context, null, 0);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs, 0);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout, defStyleAttr, 0);
        int baseWhat = a.getInt(R.styleable.RatioFrameLayout_ratioTarget, 0);
        int aspectX = a.getInteger(R.styleable.RatioFrameLayout_ratioX, 1);
        int aspectY = a.getInteger(R.styleable.RatioFrameLayout_ratioY, 1);
        a.recycle();
        helper = new RatioHelper(baseWhat, aspectX, aspectY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        switch (helper.getRatioTarget()) {
            case RatioHelper.BASE_HORIZONTAL:
                heightMeasureSpec = helper.calHeightMeasureSpec(getMeasuredWidth());
                break;
            case RatioHelper.BASE_VERTICAL:
                widthMeasureSpec = helper.calWidthMeasureSpec(getMeasuredHeight());
                break;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getRatioTarget() {
        return helper.getRatioTarget();
    }

    @Override
    public void setRatioTarget(@RatioHelper.RatioTarget int baseWhat) {
        helper.setRatioTarget(baseWhat);
        requestLayout();
    }

    @Override
    public int getRatioX() {
        return helper.getRatioX();
    }

    @Override
    public void setRatioX(@IntRange(from = 1) int aspectX) {
        helper.setRatioX(aspectX);
        requestLayout();
    }

    @Override
    public int getRatioY() {
        return helper.getRatioY();
    }

    @Override
    public void setRatioY(@IntRange(from = 1) int aspectY) {
        helper.setRatioY(aspectY);
        requestLayout();
    }
}
