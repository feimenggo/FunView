package com.feimeng.view.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Feimeng
 * Time:   2021/1/14
 * Description:
 */
public class PatternLockView extends RelativeLayout {
    private int itemCount;
    private int smallRadius;
    private int bigRadius;
    private int normalColor;
    private int rightColor;
    private int wrongColor;
    private PatternLockDotView[] mPatternLockDotViews;
    private List<Integer> mCurrentViews;
    private Path mCurrentPath;
    private int skyStartX;
    private int skyStartY;
    private int mTempX;
    private int mTempY;
    private Paint mPaint;
    private String mAnswers;
    private int mMinLength;
    private PatternLockView.OnPatternLockListener mOnPatternLockListener;
    @PatternLockMode
    private int mMode;

    public PatternLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.skyStartX = -1;
        this.skyStartY = -1;
        this.mMinLength = 4;
        this.mMode = 1;
        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.PatternLockView);
        this.itemCount = array.getInt(R.styleable.PatternLockView_itemCount, 3);
        this.smallRadius = (int) array.getDimension(R.styleable.PatternLockView_smallRadius, this.getResources().getDimension(R.dimen.PLV_smallRadius));
        this.bigRadius = (int) array.getDimension(R.styleable.PatternLockView_bigRadius, this.getResources().getDimension(R.dimen.PLV_bigRadius));
        this.normalColor = array.getInt(R.styleable.PatternLockView_normalColor, ContextCompat.getColor(this.getContext(), R.color.PLV_normalColor));
        this.rightColor = array.getColor(R.styleable.PatternLockView_rightColor, ContextCompat.getColor(this.getContext(), R.color.PLV_rightColor));
        this.wrongColor = array.getColor(R.styleable.PatternLockView_wrongColor, ContextCompat.getColor(this.getContext(), R.color.PLV_wrongColor));
        array.recycle();
        this.mCurrentViews = new ArrayList<>();
        this.mCurrentPath = new Path();
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth((float) this.smallRadius * 0.5F);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setColor(this.normalColor);
        this.mPaint.setAlpha(5);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.setMeasuredDimension(width, width);
        if (this.mPatternLockDotViews == null) {
            this.mPatternLockDotViews = new PatternLockDotView[this.itemCount * this.itemCount];

            for (int i = 0; i < this.itemCount * this.itemCount; ++i) {
                this.mPatternLockDotViews[i] = new PatternLockDotView(this.getContext(), this.normalColor, this.smallRadius, this.bigRadius, this.rightColor, this.wrongColor);
                this.mPatternLockDotViews[i].setId(i + 1);
                RelativeLayout.LayoutParams params = new LayoutParams(-2, -2);
                int marginWidth = (this.getMeasuredWidth() - this.bigRadius * 2 * this.itemCount) / (this.itemCount + 1);
                if (i >= this.itemCount) {
                    params.addRule(3, this.mPatternLockDotViews[i - this.itemCount].getId());
                }

                if (i % this.itemCount != 0) {
                    params.addRule(1, this.mPatternLockDotViews[i - 1].getId());
                }

                int bottom = 0;
                int right = 0;
                params.setMargins(marginWidth, marginWidth, right, bottom);
                this.mPatternLockDotViews[i].setCurrentState(PatternLockDotView.State.STATE_NORMAL);
                this.addView(this.mPatternLockDotViews[i], params);
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return true;
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.resetView();
                break;
            case MotionEvent.ACTION_UP:
                if (this.mCurrentViews.size() < this.mMinLength) {
                    this.patternCancel();
                    this.resetView();
                } else {
                    this.patternResult();
                    this.skyStartX = -1;
                    this.skyStartY = -1;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                this.mPaint.setColor(this.normalColor);
                this.mPaint.setStrokeCap(Paint.Cap.ROUND);
                PatternLockDotView view = this.findLockScreenView(x, y);
                if (view != null) {
                    int id = view.getId();
                    if (!this.mCurrentViews.contains(id)) {
                        if (this.mCurrentViews.isEmpty()) {
                            this.patternStart();
                        }

                        this.mCurrentViews.add(id);
                        view.setCurrentState(PatternLockDotView.State.STATE_SELECTED);
                        this.skyStartX = (view.getLeft() + view.getRight()) / 2;
                        this.skyStartY = (view.getTop() + view.getBottom()) / 2;
                        if (this.mCurrentViews.size() == 1) {
                            this.mCurrentPath.moveTo((float) this.skyStartX, (float) this.skyStartY);
                        } else {
                            this.mCurrentPath.lineTo((float) this.skyStartX, (float) this.skyStartY);
                        }
                    }
                }

                this.mTempX = x;
                this.mTempY = y;
        }

        this.invalidate();
        return true;
    }

    public void setMinLength(int minLength) {
        mMinLength = minLength;
    }

    private void patternStart() {
        if (this.mOnPatternLockListener != null) {
            this.mOnPatternLockListener.onPatternStart();
        }

    }

    private void patternResult() {
        if (this.mOnPatternLockListener != null) {
            String currentAnswers = this.getCurrentAnswers();
            boolean success;
            if (this.mMode == 2) {
                success = true;
                this.setCurrentViewsState(PatternLockDotView.State.STATE_RESULT_RIGHT);
                this.mPaint.setColor(this.rightColor);
            } else {
                success = this.checkAnswer(currentAnswers);
                if (success) {
                    this.setCurrentViewsState(PatternLockDotView.State.STATE_RESULT_RIGHT);
                    this.mPaint.setColor(this.rightColor);
                } else {
                    this.setCurrentViewsState(PatternLockDotView.State.STATE_RESULT_WRONG);
                    this.mPaint.setColor(this.wrongColor);
                }
            }

            this.mOnPatternLockListener.onPatternResult(success, currentAnswers);
        }
    }

    private void patternCancel() {
        if (this.mOnPatternLockListener != null) {
            this.mOnPatternLockListener.onPatternCancel();
        }

    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!this.mCurrentPath.isEmpty()) {
            canvas.drawPath(this.mCurrentPath, this.mPaint);
        }

        if (this.skyStartX != -1) {
            canvas.drawLine((float) this.skyStartX, (float) this.skyStartY, (float) this.mTempX, (float) this.mTempY, this.mPaint);
        }

    }

    public void setAnswers(String answers) {
        this.mAnswers = answers;
    }

    public void setMode(@PatternLockMode int mode) {
        this.mMode = mode;
        if (!this.mCurrentViews.isEmpty()) {
            this.resetView();
            this.invalidate();
        }

    }

    public void clean() {
        this.resetView();
        this.invalidate();
    }

    private boolean checkAnswer(String currentAnswers) {
        return TextUtils.equals(this.mAnswers, currentAnswers);
    }

    private String getCurrentAnswers() {
        StringBuilder sb = new StringBuilder();
        Iterator var2 = this.mCurrentViews.iterator();

        while (var2.hasNext()) {
            Integer currentView = (Integer) var2.next();
            sb.append(currentView);
        }

        return sb.toString();
    }

    private PatternLockDotView findLockScreenView(int x, int y) {
        for (int i = 0; i < this.itemCount * this.itemCount; ++i) {
            if (this.isInLockViewArea(x, y, this.mPatternLockDotViews[i])) {
                return this.mPatternLockDotViews[i];
            }
        }
        return null;
    }

    private boolean isInLockViewArea(int x, int y, PatternLockDotView view) {
        return x > view.getLeft() - 5 && x < view.getRight() + 5 && y > view.getTop() - 5 && y < view.getBottom() + 5;
    }

    private void resetView() {
        if (this.mCurrentViews.size() > 0) {
            this.mCurrentViews.clear();
        }

        if (!this.mCurrentPath.isEmpty()) {
            this.mCurrentPath.reset();
        }

        for (int i = 0; i < this.itemCount * this.itemCount; ++i) {
            this.mPatternLockDotViews[i].setCurrentState(PatternLockDotView.State.STATE_NORMAL);
        }

        this.mPaint.setColor(this.wrongColor);
        this.skyStartX = -1;
        this.skyStartY = -1;
    }

    private void setCurrentViewsState(PatternLockDotView.State state) {
        for (int i = 0; i < this.mCurrentViews.size(); ++i) {
            PatternLockDotView view = (PatternLockDotView) this.findViewById((Integer) this.mCurrentViews.get(i));
            view.setCurrentState(state);
        }

    }

    public void setOnPatternLockListener(PatternLockView.OnPatternLockListener onPatternLockListener) {
        this.mOnPatternLockListener = onPatternLockListener;
    }

    public interface OnPatternLockListener {
        void onPatternStart();

        void onPatternResult(boolean var1, String var2);

        void onPatternCancel();
    }
}
