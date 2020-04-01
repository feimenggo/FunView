package com.feimeng.keyboard.detector

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

/**
 * Author: Feimeng
 * Time:   2020/4/1
 * Description: 动画辅助类
 */
class KeyboardAnim {
    companion object {
        fun anim(from: Int, to: Int, valueChange: ValueChange, interpolator: TimeInterpolator = LinearInterpolator()): ValueAnimator {
            val anim = ValueAnimator.ofInt(from, to)
            anim.interpolator = interpolator
            anim.addUpdateListener { listener ->
                val value = listener.animatedValue as Int
                valueChange.onValueChange(value)
            }
            anim.start()
            return anim
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
}