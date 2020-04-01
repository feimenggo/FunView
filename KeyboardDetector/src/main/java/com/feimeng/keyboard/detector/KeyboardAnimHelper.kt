package com.feimeng.keyboard.detector

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description: 动画辅助类 实现了OnKeyboardChangeListener接口
 */
open class KeyboardAnimHelper : OnKeyboardChangeListener {
    private var mSmoothViews: MutableList<View>? = null // 平滑移动
    private var mInstantViews: MutableList<View>? = null // 瞬间移动
    private var mAnimator: ValueAnimator? = null // 动画执行器
    private var mIgnoreInitChange: Boolean? = true // 是否忽略初始化的变化

    open fun addSmoothView(vararg views: View): KeyboardAnimHelper {
        if (mSmoothViews == null) mSmoothViews = ArrayList()
        mSmoothViews!!.addAll(views)
        return this
    }

    fun addInstantView(vararg views: View): KeyboardAnimHelper {
        if (mInstantViews == null) mInstantViews = ArrayList()
        mInstantViews!!.addAll(views)
        return this
    }

    fun ignoreInitChange(ignore: Boolean): KeyboardAnimHelper {
        mIgnoreInitChange = ignore
        return this
    }

    companion object {
        fun anim(from: Int, to: Int, valueChange: ValueChange) {
            val anim = ValueAnimator.ofInt(from, to)
            anim.addUpdateListener { listener ->
                val value = listener.animatedValue as Int
                valueChange.onValueChange(value)
            }
            anim.start()
        }

//        fun animWithToolkit(visible: Boolean, keyboardHeight: Int, view: View) {
//            var anim = view.getTag(R.id.anim) as ValueAnimator?
//            anim?.end()
//            var marginBottom = view.getTag(R.id.animMarginBottom) as Int? ?: -1
//            if (marginBottom == -1) {
//                marginBottom = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
//                view.setTag(R.id.animMarginBottom, marginBottom)
//            }
//            if (visible) {
//                anim = ValueAnimator.ofInt(keyboardHeight - view.height, keyboardHeight)
//                anim.interpolator = DecelerateInterpolator()
//                anim.duration = 200
//            } else {
//                anim = ValueAnimator.ofInt(keyboardHeight, 0)
//                anim.duration = 400
//            }
//            anim.addUpdateListener { listener ->
//                val value = listener.animatedValue as Int
//                (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value + marginBottom
//                view.requestLayout()
//            }
//            anim.start()
//            view.setTag(R.id.anim, anim)
//        }
    }

    interface ValueChange {
        fun onValueChange(value: Int)
    }

    override fun onKeyboardHeightChanged(visible: Boolean, height: Int, orientation: Int) {
        if (mIgnoreInitChange != null) {
            if (mIgnoreInitChange as Boolean) {
                mIgnoreInitChange = null
                return
            } else {
                mIgnoreInitChange = null
            }
        }
        mAnimator?.end()
        if (visible) {
            mAnimator = ValueAnimator.ofInt(0, height)
            mAnimator!!.interpolator = DecelerateInterpolator()
            mAnimator!!.duration = 220
        } else {
            mAnimator = ValueAnimator.ofInt(height, 0)
            mAnimator!!.duration = 400
        }
        if (mInstantViews != null) {
            val endHeight = if (visible) height else 0
            for (view in mInstantViews!!) {
                var marginBottom = view.getTag(R.id.animMarginBottom) as Int? ?: -1
                if (marginBottom == -1) {
                    marginBottom = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
                    view.setTag(R.id.animMarginBottom, marginBottom)
                }
                (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = endHeight + marginBottom
            }
        }
        mAnimator!!.addUpdateListener { listener ->
            val value = listener.animatedValue as Int
            if (mSmoothViews != null) {
                var handlerView: View? = null
                for (view in mSmoothViews!!) {
                    handlerView = view
                    var marginBottom = view.getTag(R.id.animMarginBottom) as Int? ?: -1
                    if (marginBottom == -1) {
                        marginBottom = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
                        view.setTag(R.id.animMarginBottom, marginBottom)
                    }
                    (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value + marginBottom
                }
                handlerView?.requestLayout()
            }
        }
        mAnimator!!.start()
    }
}