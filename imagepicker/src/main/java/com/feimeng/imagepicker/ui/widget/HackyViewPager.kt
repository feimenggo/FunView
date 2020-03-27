package com.feimeng.imagepicker.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Author: Feimeng
 * Time:   2020/3/26
 * Description: PhotoView Issues With ViewPager
 */
class HackyViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
}