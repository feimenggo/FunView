package com.feimeng.keyboard.detector

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description: 动画辅助类
 */
class AnimHelper {
    companion object {
        fun anim(from: Int, to: Int, valueChange: ValueChange) {
            val anim = ValueAnimator.ofInt(from, to)
            anim!!.addUpdateListener { listener ->
                val value = listener.animatedValue as Int
                valueChange.onValueChange(value)
            }
            anim.start()
        }

        fun animWithToolkit(visible: Boolean, keyboardHeight: Int, view: View) {
            val from: Int
            val to: Int
            if (visible) {
                from = keyboardHeight - view.height / 2
                to = keyboardHeight
            } else {
                from = keyboardHeight
                to = 0
            }
            val anim = ValueAnimator.ofInt(from, to)
            anim!!.addUpdateListener { listener ->
                val value = listener.animatedValue as Int
                (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value
                view.requestLayout()
            }
            anim.start()
        }
    }

    interface ValueChange {
        fun onValueChange(value: Int)
    }
}