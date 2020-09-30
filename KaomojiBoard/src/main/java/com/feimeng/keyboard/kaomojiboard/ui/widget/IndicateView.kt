package com.feimeng.keyboard.kaomojiboard.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.feimeng.keyboard.kaomojiboard.R
import com.feimeng.keyboard.kaomojiboard.adapter.KaomojiPageAdapter

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 页码指示器
 */
class IndicateView : LinearLayout {
    private var mCurrentIndex: Int = -1

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.kb_shape_indicator_spacing)
    }

    fun setIndex(index: Int) {
        mCurrentIndex = index
        updateIndicate()
    }

    private fun updateIndicate() {
        for (poi in 0 until childCount) {
            val view = getChildAt(poi)
            view.setBackgroundResource(if (poi == mCurrentIndex) R.drawable.kb_shape_indicator_focus else
                R.drawable.kb_shape_indicator)
        }
    }

    fun setCount(count: Int) {
        if (childCount != count) {
            removeAllViewsInLayout()
            for (poi in 0 until count) {
                addView(View(context))
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        val size = context.resources.getDimensionPixelSize(R.dimen.kb_indicatorSize)
        return LayoutParams(size, size)
    }

    fun bind(vp: ViewPager2, adapter: KaomojiPageAdapter) {
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                post {
                    val kaomojiPage = adapter.getKaomojiPage(position)
                    setCount(kaomojiPage.pageCount)
                    setIndex(kaomojiPage.pageIndex)
                    Log.d("nodawang", "thread:${Thread.currentThread()} packageIndex:${kaomojiPage.packageIndex} pageIndex:${kaomojiPage.pageIndex} pageCount:${kaomojiPage.pageCount}")
                }
            }
        })
    }
}