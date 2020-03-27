package com.feimeng.kaomojiboard

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.feimeng.kaomojiboard.model.KaomojiPage

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 一页显示的颜文字
 */
class PageView : LinearLayout {
    private var mPage: KaomojiPage? = null
    private val mRowCount: Int = 4
    private val mColumnCount: Int = 4

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        orientation = VERTICAL
        dividerPadding = context.resources.getDimensionPixelOffset(R.dimen.kaomojiVerticalSpace)
    }

    fun setData(page: KaomojiPage) {
        mPage = page
        removeAllViewsInLayout()
        val context = context
        val itemHeight = context.resources.getDimensionPixelOffset(R.dimen.kaomojiHeight)
        val color = Color.parseColor("#505962")
        for (row in 0 until mRowCount) {
            val ll = LinearLayout(context)
            ll.orientation = HORIZONTAL
            for (column in 0 until mColumnCount) {
                val tv = TextView(context)
                val kaomoji = mPage!!.get(row + column)
                tv.text = kaomoji.body
                tv.gravity = Gravity.CENTER
                tv.textSize = 10F
                tv.setTextColor(color)
                val layout = LayoutParams(0, itemHeight)
                layout.weight = 1F
                ll.addView(tv, layout)
            }
            addView(ll)
        }
    }
}