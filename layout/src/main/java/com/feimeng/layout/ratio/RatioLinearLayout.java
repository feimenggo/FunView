package com.feimeng.layout.ratio;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.layout.R;

/**
 * 自定义宽高比例的LinearLayout，例如2:5、4:3等等，默认为1:1。
 */
public class RatioLinearLayout extends LinearLayout implements IRatio {
    private RatioHelper helper = null;

    public RatioLinearLayout(@NonNull Context context) {
        super(context);
        initAttr(context, null, 0);
    }

    public RatioLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs, 0);
    }

    public RatioLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioLinearLayout, defStyleAttr, 0);
        int baseWhat = a.getInt(R.styleable.RatioLinearLayout_ratioTarget, 0);
        int aspectX = a.getInteger(R.styleable.RatioLinearLayout_ratioX, 1);
        int aspectY = a.getInteger(R.styleable.RatioLinearLayout_ratioY, 1);
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
