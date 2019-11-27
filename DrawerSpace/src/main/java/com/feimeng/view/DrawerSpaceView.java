package com.feimeng.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

/**
 * Author: Feimeng
 * Time:   2019/10/31
 * Description: 空间展开控件
 */
public class DrawerSpaceView extends LinearLayout implements View.OnTouchListener {
    private Integer mDimColor;
    private boolean mIsShowing, mIsAnimating;
    private boolean mSpaceInit;
    private View mSpaceView;
    private int mAnimationDuration = 250;
    private OnStateChangeListener mStateChangeListener;
    private Runnable mHideRunnable;

    public DrawerSpaceView(Context context) {
        this(context, null);
    }

    public DrawerSpaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerSpaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawerSpaceView);
        if (array.hasValue(R.styleable.DrawerSpaceView_dimColor)) {
            setDimColor(array.getColor(R.styleable.DrawerSpaceView_dimColor, Color.TRANSPARENT));
        }
        array.recycle();
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SpaceWipeView can host only one direct child");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SpaceWipeView can host only one direct child");
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SpaceWipeView can host only one direct child");
        }
        super.addView(child, index, params);
        mSpaceView = child;
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SpaceWipeView can host only one direct child");
        }
        super.addView(child);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mSpaceInit) {
            mSpaceInit = true;
            mIsShowing = false;
            mIsAnimating = false;
            if (!isInEditMode()) {
                setVisibility(GONE);
                setOnTouchListener(this);
                mSpaceView.setTranslationY(-mSpaceView.getHeight());
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getY() > mSpaceView.getHeight()) {
            hide();
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHideRunnable = null;
    }

    public void setSpaceView(View view) {
        mSpaceView = view;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getSpaceView() {
        return (T) mSpaceView;
    }

    public void setDimColor(@ColorInt int color) {
        mDimColor = color;
        if (!mIsAnimating && mIsShowing) setBackgroundColor(mDimColor);
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setStateChangeListener(OnStateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }

    public interface OnStateChangeListener {
        void onStateChange(boolean showing);
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void toggle() {
        if (mIsShowing) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        if (mIsAnimating) return;
        if (!mIsShowing) {
            mIsShowing = true;
            animateViewIn();
        }
    }

    public void hide() {
        hide(null);
    }

    public void hide(Runnable runnable) {
        if (mIsAnimating) return;
        if (mIsShowing) {
            mIsShowing = false;
            mHideRunnable = runnable;
            animateViewOut();
        }
    }

    private void animateViewIn() {
        if (mStateChangeListener != null) mStateChangeListener.onStateChange(true);
        View spaceView = getSpaceView();
        ViewCompat.animate(spaceView)
                .translationY(0)
                .setDuration(mAnimationDuration)
                .setInterpolator(new LinearInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                        setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                    }
                }).withLayer().start();
        if (mDimColor != null) updateDim(Color.TRANSPARENT, mDimColor);
    }

    private void animateViewOut() {
        View spaceView = getSpaceView();
        ViewCompat.animate(spaceView)
                .translationY(-spaceView.getHeight())
                .setDuration(mAnimationDuration)
                .setInterpolator(new LinearInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        mIsAnimating = false;
                        setVisibility(View.GONE);
                        if (mStateChangeListener != null) mStateChangeListener.onStateChange(false);
                        if (mHideRunnable != null) {
                            mHideRunnable.run();
                            mHideRunnable = null;
                        }
                    }
                }).withLayer().start();
        if (mDimColor != null) updateDim(mDimColor, Color.TRANSPARENT);
    }

    private void updateDim(int startColor, int endColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.addUpdateListener(animation -> setBackgroundColor((int) animation.getAnimatedValue()));
        colorAnimator.setDuration(mAnimationDuration);
        colorAnimator.start();
    }
}
