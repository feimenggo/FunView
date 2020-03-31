package com.feimeng.keyboard.kaomojiboard.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.feimeng.keyboard.kaomojiboard.R
import com.feimeng.keyboard.kaomojiboard.adapter.KaomojiPageAdapter
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPackage

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字分类
 */
class CategoryView : LinearLayout, View.OnClickListener {
    private var mCurrentIndex: Int = -1
    private var mData: List<KaomojiPackage>? = null
    private var mListener: OnClickKaomojiPackageListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        orientation = HORIZONTAL
//        showDividers = SHOW_DIVIDER_MIDDLE
//        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.kb_shape_indicator_spacing)
    }

    fun setIndex(index: Int) {
        mCurrentIndex = index
        updateIndicate()
    }

    private fun updateIndicate() {
        for (poi in 0 until childCount) {
            val view = getChildAt(poi)
            if (poi == mCurrentIndex) {
                view.setBackgroundColor(Color.parseColor("#F4F9FF"))
            } else {
                view.background = null
            }
        }
    }

    fun setData(list: List<KaomojiPackage>) {
        mData = list
        removeAllViewsInLayout()
        val color = Color.parseColor("#505962")
        for (poi in list.indices) {
            val tv = TextView(context)
            tv.tag = poi
            tv.text = list[poi].logo
            tv.textSize = 10F
            tv.setTextColor(color)
            tv.gravity = Gravity.CENTER
            tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            tv.setOnClickListener(this)
            addView(tv)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        val size = context.resources.getDimensionPixelSize(R.dimen.kb_categorySize)
        return LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun bind(vp: ViewPager2, adapter: KaomojiPageAdapter) {
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                post {
                    val kaomojiPage = adapter.getKaomojiPage(position)
                    setIndex(kaomojiPage.packageIndex)
                }
            }
        })
    }

    override fun onClick(v: View) {
        if (mListener != null) {
            val index = v.tag as Int
            mListener!!.onClickKaomojiPackage(mData!![index], index)
        }
    }

    fun setOnClickKaomojiPackageListener(listener: OnClickKaomojiPackageListener) {
        mListener = listener
    }

    interface OnClickKaomojiPackageListener {
        fun onClickKaomojiPackage(kaomojiPackage: KaomojiPackage, position: Int)
    }
}