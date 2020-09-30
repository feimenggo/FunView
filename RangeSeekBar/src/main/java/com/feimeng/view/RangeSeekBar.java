package com.feimeng.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

/**
 * Author: Feimeng
 * Time:   2020/9/30
 * Description:
 */
public class RangeSeekBar extends FrameLayout {
    protected int mSize;
    protected Drawable mStartDrawable, mEndDrawable;
    protected int mBackgroundColor, mProgressColor;
    protected int mTextColor;
    protected RangeSeekBarIndicator mSeekBar;
    protected View mStartEdge, mEndEdge;
    protected ImageView mStartView, mEndView;
    protected SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    public RangeSeekBar(@NonNull Context context) {
        this(context, null);
    }

    public RangeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RangeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar, defStyleAttr, defStyleRes);
        mStartDrawable = a.getDrawable(R.styleable.RangeSeekBar_startDrawable);
        mEndDrawable = a.getDrawable(R.styleable.RangeSeekBar_endDrawable);
        mBackgroundColor = a.getColor(R.styleable.RangeSeekBar_backgroundColor, Color.parseColor("#E9EDF2"));
        mProgressColor = a.getColor(R.styleable.RangeSeekBar_progressColor, Color.parseColor("#007AFD"));
        mTextColor = a.getColor(R.styleable.RangeSeekBar_android_textColor, Color.BLACK);
        int max = a.getInt(R.styleable.RangeSeekBar_android_max, 9);
        int progress = a.getInt(R.styleable.RangeSeekBar_android_progress, 4);
        a.recycle();

        ViewGroup group = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.rsb_layout, this, false);
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            attachViewToParent(child, i, child.getLayoutParams());
        }
        mStartEdge = getChildAt(0);
        mEndEdge = getChildAt(1);
        mStartView = (ImageView) getChildAt(3);
        if (mStartDrawable != null) mStartView.setImageDrawable(mStartDrawable);
        mEndView = (ImageView) getChildAt(4);
        if (mEndDrawable != null) mEndView.setImageDrawable(mEndDrawable);
        mSeekBar = (RangeSeekBarIndicator) getChildAt(2);
        mSeekBar.setProgressDrawable(createProgressDrawable());
        mSeekBar.setTextSize(14);
        mSeekBar.setTextColor(mTextColor);
        mSeekBar.setMax(max);
        mSeekBar.setProgress(progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateProgress();
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
        updateProgress();
        setClipChildren(false);
    }

    protected void updateProgress() {
        int progress = mSeekBar.getProgress();
        mStartView.setVisibility(progress == 0 ? GONE : VISIBLE);
        mEndView.setVisibility(progress == mSeekBar.getMax() ? GONE : VISIBLE);
    }

    public void setMax(int max) {
        mSeekBar.setMax(max);
    }

    public int getMax() {
        return mSeekBar.getMax();
    }

    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public void setSeekBarTextCreater(RangeSeekBar.SeekBarTextCreater seekBarTextCreater) {
        mSeekBar.setSeekBarTextCreater(seekBarTextCreater);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mOnSeekBarChangeListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            int size = (int) (getResources().getDimension(R.dimen.size) * 10);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelSize(R.dimen.size), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mSize = getMeasuredHeight();
            int sizeHalf = mSize / 2;
            mStartEdge.getLayoutParams().width = sizeHalf;
            mEndEdge.getLayoutParams().width = sizeHalf;
            mStartEdge.setBackground(createStartEdgeDrawable());
            mEndEdge.setBackground(createEndEdgeDrawable());
            mSeekBar.setThumb(createThumbDrawable());
            mSeekBar.setThumbWidth(mSize);
            LayoutParams lp = (LayoutParams) mSeekBar.getLayoutParams();
            lp.leftMargin = sizeHalf;
            lp.rightMargin = sizeHalf;
        }
    }

    protected Drawable createStartEdgeDrawable() {
        GradientDrawable edge = new GradientDrawable();
        edge.setColor(mProgressColor);
        float radius = mSize;
        float[] radii = new float[]{radius, radius, 0, 0, 0, 0, radius, radius};
        edge.setCornerRadii(radii);
        return edge;
    }

    protected Drawable createEndEdgeDrawable() {
        GradientDrawable edge = new GradientDrawable();
        edge.setColor(mBackgroundColor);
        float radius = mSize;
        float[] radii = new float[]{0, 0, radius, radius, radius, radius, 0, 0};
        edge.setCornerRadii(radii);
        return edge;
    }

    protected Drawable createThumbDrawable() {
        GradientDrawable thumb = new GradientDrawable();
        thumb.setSize(mSize + 2, mSize + 2);
        thumb.setColor(Color.WHITE);
        float[] radii = new float[8];
        Arrays.fill(radii, mSize);
        thumb.setCornerRadii(radii);
        return thumb;
    }

    protected Drawable createProgressDrawable() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(mBackgroundColor);
        GradientDrawable progress = new GradientDrawable();
        progress.setColor(mProgressColor);
        ScaleDrawable scaleDrawable = new ScaleDrawable(progress, Gravity.LEFT, 1F, -1F);
        return new LayerDrawable(new Drawable[]{background, scaleDrawable});
    }

    public interface SeekBarTextCreater {
        String createText(int progress);
    }

    public static class SimpleSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
