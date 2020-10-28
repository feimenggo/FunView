package com.feimeng.view.replace;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description: 支持查找高亮与替换的控件
 */
public class ReplaceView extends ScrollView {
    private ReplaceTextView mReplaceTextView;

    public ReplaceView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ReplaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ReplaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mReplaceTextView = new ReplaceTextView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReplaceView);
        int paddingLeft = a.getDimensionPixelSize(R.styleable.ReplaceView_contentPaddingLeft, 0);
        int paddingTop = a.getDimensionPixelSize(R.styleable.ReplaceView_contentPaddingTop, 0);
        int paddingRight = a.getDimensionPixelSize(R.styleable.ReplaceView_contentPaddingRight, 0);
        int paddingBottom = a.getDimensionPixelSize(R.styleable.ReplaceView_contentPaddingBottom, 0);
        a.recycle();
        mReplaceTextView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        addView(mReplaceTextView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setOnReplaceListener(OnReplaceListener onReplaceListener) {
        mReplaceTextView.setOnReplaceListener(onReplaceListener);
    }

    public void setContent(String content) {
        mReplaceTextView.setContent(content);
    }

    public String getContent() {
        return mReplaceTextView.getContent();
    }

    public void setContentSize(float size) {
        mReplaceTextView.setContentSize(size);
    }

    public void setContentColor(@ColorInt int color) {
        mReplaceTextView.setContentColor(color);
    }

    /**
     * 设置高亮颜色
     *
     * @param searchColor     查找颜色
     * @param replaceColor    替换颜色
     * @param indicationColor 指示器颜色
     */
    public void setHighlightColor(int searchColor, int replaceColor, int indicationColor) {
        mReplaceTextView.setHighlightColor(searchColor, replaceColor, indicationColor);
    }

    /**
     * 查找文本
     *
     * @param searchText 搜索文本
     */
    public void search(String searchText) {
        mReplaceTextView.search(searchText);
    }

    /**
     * 往前移动指示器
     */
    public void prev() {
        mReplaceTextView.prev();
    }

    /**
     * 往后移动指示器
     */
    public void next() {
        mReplaceTextView.next();
    }

    /**
     * 往后移动指示器
     */
    public void repeal() {
        mReplaceTextView.repeal();
    }

    /**
     * 替换指示器处的内容
     *
     * @param replaceText 替换文本
     */
    public void replace(String replaceText) {
        mReplaceTextView.replace(replaceText);
    }

    /**
     * 替换所有查找的内容
     *
     * @param replaceText 替换文本
     */
    public void replaceAll(String replaceText) {
        mReplaceTextView.replaceAll(replaceText);
    }
}
