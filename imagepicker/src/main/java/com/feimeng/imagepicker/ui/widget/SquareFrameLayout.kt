package com.feimeng.imagepicker.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * Author: Feimeng
 * Time:   2018/8/20 10:56
 * Description: 矩形FrameLayout
 */
class SquareFrameLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec), View.getDefaultSize(0, heightMeasureSpec))
        val childWidthSize = measuredWidth
        // 高度和宽度一样
        super.onMeasure(MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY), widthMeasureSpec)
    }
}